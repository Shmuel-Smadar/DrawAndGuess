package com.example.drawandguess.service;

import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.model.WordOptions;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class GameService {
    private final ChatService chatService;
    private final ParticipantService participantService;
    private final RoomService roomService;
    private final TaskScheduler taskScheduler;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> hintTasks = new ConcurrentHashMap<>();
    private final JmsTemplate jmsTemplate;

    public GameService(
            ChatService chatService,
            ParticipantService participantService,
            RoomService roomService,
            TaskScheduler taskScheduler,
            JmsTemplate jmsTemplate
    ) {
        this.chatService = chatService;
        this.participantService = participantService;
        this.roomService = roomService;
        this.taskScheduler = taskScheduler;
        this.jmsTemplate = jmsTemplate;
    }

    public WordOptions requestWords(String roomId, String sessionId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return new WordOptions();
        Game game = room.getGame();
        return game.isDrawer(sessionId) ? game.getRandomWords() : new WordOptions();
    }

    public void chooseWord(String roomId, String sessionId, String chosenWord) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Game game = room.getGame();
        if (game.isDrawer(sessionId)) {
            game.setChosenWord(chosenWord);
            ChatMessage msg = new ChatMessage();
            msg.setSenderSessionId("system");
            msg.setText("A new round has started. The drawer is "
                    + participantService.findParticipantBySessionId(sessionId).getUsername()
                    + ". (Round " + (game.getRoundCount() + 1) + "/" + game.getTotalRounds() + ")");
            msg.setType("system");
            chatService.sendChatMessage(roomId, msg);
            startHintProgression(roomId);
        }
    }

    public void correctGuess(String roomId, String guess, String sessionId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Game game = room.getGame();
        if (game.isCorrectGuess(guess)) {
            String username = participantService.findParticipantBySessionId(sessionId).getUsername();
            game.addScore(sessionId, 10);
            if (game.getCurrentDrawer() != null) game.addScore(game.getCurrentDrawer(), 5);
            String drawerId = game.getCurrentDrawer();
            String drawerUsername = drawerId == null ? "" : participantService.findParticipantBySessionId(drawerId).getUsername();
            jmsTemplate.convertAndSend("leaderboardQueue", username + ":" + game.getScore(sessionId));
            if (!drawerUsername.isEmpty()) jmsTemplate.convertAndSend("leaderboardQueue", drawerUsername + ":" + game.getScore(drawerId));
            stopHintProgression(roomId);
            game.nextRound();
            if (game.isGameOver()) {
                announceScoresAndReset(roomId, "The game has ended after " + game.getTotalRounds() + " rounds. Final scores:");
            } else {
                ChatMessage msg = new ChatMessage();
                msg.setSenderSessionId("system");
                msg.setText(username + " guessed the word!");
                msg.setType("system");
                chatService.sendChatMessage(roomId, msg);
                updateDrawerAndBroadcast(roomId, game);
            }
        }
    }

    public void startHintProgression(String roomId) {
        if (hintTasks.containsKey(roomId)) return;
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Runnable r = () -> {
            Game g = room.getGame();
            synchronized (g) {
                if (!g.hasMoreHints()) {
                    handleNoGuess(roomId, g);
                    return;
                }
                String hint = g.nextHint();
                sendHintToParticipants(roomId, hint);
                if (!g.hasMoreHints()) handleNoGuess(roomId, g);
            }
        };
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(r, Duration.ofSeconds(10));
        hintTasks.put(roomId, future);
    }

    public void stopHintProgression(String roomId) {
        ScheduledFuture<?> f = hintTasks.remove(roomId);
        if (f != null) f.cancel(false);
    }

    private void handleNoGuess(String roomId, Game game) {
        stopHintProgression(roomId);
        game.nextRound();
        if (game.isGameOver()) {
            announceScoresAndReset(roomId, "No one guessed the word. The game has ended after " + game.getTotalRounds() + " rounds. Final scores:");
        } else {
            ChatMessage msg = new ChatMessage();
            msg.setSenderSessionId("system");
            msg.setText("No one guessed the word.");
            msg.setType("system");
            chatService.sendChatMessage(roomId, msg);
            updateDrawerAndBroadcast(roomId, game);
        }
    }

    private void announceScoresAndReset(String roomId, String endMessage) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Game game = room.getGame();
        StringBuilder sb = new StringBuilder(endMessage).append(" ");
        for (String pid : game.getParticipantSessionIds()) {
            String uname = participantService.findParticipantBySessionId(pid).getUsername();
            sb.append(uname).append("=").append(game.getScore(pid)).append("  ");
        }
        ChatMessage m = new ChatMessage();
        m.setSenderSessionId("system");
        m.setText(sb.toString());
        m.setType("system");
        chatService.sendChatMessage(roomId, m);
        game.resetGame();
        if (!game.getParticipantSessionIds().isEmpty()) {
            String firstDrawerId = game.getCurrentDrawer();
            participantService.getAllParticipants().values().forEach(p ->
                    participantService.setDrawer(p.getSessionId(), p.getSessionId().equals(firstDrawerId))
            );
            ChatMessage newGameMsg = new ChatMessage();
            newGameMsg.setSenderSessionId("system");
            newGameMsg.setText(
                    "A new game has started. (Round " + (game.getRoundCount() + 1) + "/" + game.getTotalRounds()
                            + ") The drawer is " + participantService.findParticipantBySessionId(firstDrawerId).getUsername() + "."
            );
            newGameMsg.setType("system");
            chatService.sendChatMessage(roomId, newGameMsg);
            roomService.broadcastParticipants(roomId);
        }
    }

    private void sendHintToParticipants(String roomId, String currentHint) {
        Room r = roomService.getRoom(roomId);
        if (r == null) return;
        List<String> ids = r.getGame().getParticipantSessionIds();
        String d = r.getGame().getCurrentDrawer();
        ids.stream().filter(s -> !s.equals(d)).forEach(s -> chatService.sendWordHint(s, roomId, currentHint));
    }

    private void updateDrawerAndBroadcast(String roomId, Game g) {
        String d = g.getCurrentDrawer();
        participantService.getAllParticipants().values().forEach(p ->
                participantService.setDrawer(p.getSessionId(), p.getSessionId().equals(d))
        );
        roomService.broadcastParticipants(roomId);
    }

    public void userLeftRoom(String roomId, String sessionId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Game game = room.getGame();
        Participant p = participantService.findParticipantBySessionId(sessionId);
        if (p == null) return;
        boolean wasDrawer = game.isDrawer(sessionId);
        game.removeParticipant(sessionId);
        ChatMessage leaveMsg = new ChatMessage();
        leaveMsg.setSenderSessionId("system");
        leaveMsg.setText(p.getUsername() + " has left the room.");
        leaveMsg.setType("system");
        chatService.sendChatMessage(roomId, leaveMsg);
        if (wasDrawer) {
            game.resetRound();
            stopHintProgression(roomId);
            if (!game.getParticipantSessionIds().isEmpty()) {
                String newDrawerId = game.getCurrentDrawer();
                if (newDrawerId != null) {
                    participantService.setDrawer(newDrawerId, true);
                    ChatMessage resetMsg = new ChatMessage();
                    resetMsg.setSenderSessionId("system");
                    resetMsg.setText(
                            "The previous drawer quit abruptly. The round has been reset. New drawer is: "
                                    + participantService.findParticipantBySessionId(newDrawerId).getUsername()
                    );
                    resetMsg.setType("system");
                    chatService.sendChatMessage(roomId, resetMsg);
                }
            }
        } else {
            String currentDrawerId = game.getCurrentDrawer();
            participantService.getAllParticipants().values().forEach(pp ->
                    participantService.setDrawer(pp.getSessionId(), pp.getSessionId().equals(currentDrawerId))
            );
        }
        roomService.broadcastParticipants(roomId);
        roomService.broadcastRooms();
    }

    public void handleDisconnect(String sessionId) {
        Participant participant = participantService.findParticipantBySessionId(sessionId);
        if (participant == null) return;
        for (Room room : roomService.getAllRooms()) {
            if (room.getGame().getParticipantSessionIds().contains(sessionId)) {
                userLeftRoom(room.getRoomId(), sessionId);
            }
        }
        participantService.removeParticipant(sessionId);
    }
}
