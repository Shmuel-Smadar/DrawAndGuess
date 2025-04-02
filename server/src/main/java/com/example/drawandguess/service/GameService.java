package com.example.drawandguess.service;

import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.model.WordOptions;
import com.example.drawandguess.model.MessageType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static com.example.drawandguess.config.Constants.HINT_INTERVAL_SECONDS;
import static com.example.drawandguess.config.Constants.GAME_ENDED_MSG;
import static com.example.drawandguess.config.Constants.NO_ONE_GUESSED_MSG;

@Service
public class GameService {
    private final ChatService chatService;
    private final ParticipantService participantService;
    private final RoomService roomService;
    private final TaskScheduler taskScheduler;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> hintTasks = new ConcurrentHashMap<>();
    private final LeaderboardService leaderboardService;
    private final MessageService messageService;
    private final WordService wordService;

    public GameService(
            ChatService chatService,
            ParticipantService participantService,
            RoomService roomService,
            TaskScheduler taskScheduler,
            LeaderboardService leaderboardService,
            MessageService messageService,
            WordService wordService
    ) {
        this.chatService = chatService;
        this.participantService = participantService;
        this.roomService = roomService;
        this.taskScheduler = taskScheduler;
        this.leaderboardService = leaderboardService;
        this.messageService = messageService;
        this.wordService = wordService;
    }

    public WordOptions requestWords(String roomId, String sessionId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return new WordOptions();
        Game game = room.getGame();
        if (game.isDrawer(sessionId)) {
            return wordService.getRandomWords();
        }
        return new WordOptions();
    }

    public void chooseWord(String roomId, String sessionId, String chosenWord) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Game game = room.getGame();
        if (game.isDrawer(sessionId)) {
            game.setChosenWord(chosenWord);
            ChatMessage msg = messageService.systemMessage(
                    MessageType.ROUND_STARTED,
                    participantService.findParticipantBySessionId(sessionId).getUsername(),
                    String.valueOf(game.getRoundCount() + 1),
                    String.valueOf(game.getTotalRounds()));
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
            if (game.getCurrentDrawer() != null) {
                game.addScore(game.getCurrentDrawer(), 5);
                String drawerId = game.getCurrentDrawer();
                String drawerUsername = drawerId == null
                        ? ""
                        : participantService.findParticipantBySessionId(drawerId).getUsername();
                if (!drawerUsername.isEmpty()) {
                    leaderboardService.updateScore(drawerUsername, game.getScore(drawerId));
                }
            }
            leaderboardService.updateScore(username, game.getScore(sessionId));
            stopHintProgression(roomId);
            game.nextRound();
            if (game.isGameOver()) {
                endGameAndStartNew(roomId, game, GAME_ENDED_MSG);
            } else {
                ChatMessage msg = messageService.systemMessage(MessageType.WORD_GUESSED, username);
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
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(
                r,
                Duration.ofSeconds(HINT_INTERVAL_SECONDS)
        );
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
            endGameAndStartNew(roomId, game, NO_ONE_GUESSED_MSG);
        } else {
            ChatMessage msg = messageService.systemMessage(MessageType.NO_GUESS);
            chatService.sendChatMessage(roomId, msg);
            updateDrawerAndBroadcast(roomId, game);
        }
    }

    private void sendHintToParticipants(String roomId, String currentHint) {
        Room r = roomService.getRoom(roomId);
        if (r == null) return;
        List<String> ids = r.getGame().getParticipantSessionIds();
        String d = r.getGame().getCurrentDrawer();
        ids.stream()
                .filter(s -> !s.equals(d))
                .forEach(s -> chatService.sendWordHint(s, roomId, currentHint));
    }

    private void updateDrawerAndBroadcast(String roomId, Game g) {
        String d = g.getCurrentDrawer();
        participantService.getAllParticipants().values().forEach(
                p -> participantService.setDrawer(
                        p.getSessionId(),
                        p.getSessionId().equals(d)
                )
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
        ChatMessage leaveMsg = messageService.systemMessage(MessageType.PARTICIPANT_LEFT, p.getUsername());
        chatService.sendChatMessage(roomId, leaveMsg);
        if (wasDrawer) {
            game.resetRound();
            stopHintProgression(roomId);
            if (!game.getParticipantSessionIds().isEmpty()) {
                String newDrawerId = game.getCurrentDrawer();
                if (newDrawerId != null) {
                    participantService.setDrawer(newDrawerId, true);
                    ChatMessage resetMsg = messageService.systemMessage(
                            MessageType.PREVIOUS_DRAWER_QUIT,
                            participantService.findParticipantBySessionId(newDrawerId).getUsername()
                    );
                    chatService.sendChatMessage(roomId, resetMsg);
                }
            }
        } else {
            String currentDrawerId = game.getCurrentDrawer();
            participantService.getAllParticipants().values().forEach(
                    participant -> participantService.setDrawer(
                            participant.getSessionId(),
                            participant.getSessionId().equals(currentDrawerId)
                    )
            );
        }
        roomService.broadcastParticipants(roomId);
        if (game.getParticipantSessionIds().isEmpty()) {
            roomService.deleteRoom(roomId);
        }
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

    private void endGameAndStartNew(String roomId, Game game, String messagePrefix) {
        StringBuilder sb = new StringBuilder(messagePrefix).append(" after ")
                .append(game.getTotalRounds()).append(" rounds. Final scores: ");
        for (String pid : game.getParticipantSessionIds()) {
            sb.append(participantService.findParticipantBySessionId(pid).getUsername())
                    .append("=")
                    .append(game.getScore(pid))
                    .append("  ");
        }
        ChatMessage m = messageService.systemMessage(MessageType.GAME_ENDED, sb.toString());
        chatService.sendChatMessage(roomId, m);
        game.resetGame();
        if (!game.getParticipantSessionIds().isEmpty()) {
            String firstDrawerId = game.getCurrentDrawer();
            participantService.getAllParticipants().values().forEach(
                    p -> participantService.setDrawer(
                            p.getSessionId(),
                            p.getSessionId().equals(firstDrawerId)
                    )
            );
            ChatMessage newGameMsg = messageService.systemMessage(
                    MessageType.NEW_GAME_STARTED,
                    String.valueOf(game.getRoundCount() + 1),
                    String.valueOf(game.getTotalRounds()),
                    participantService.findParticipantBySessionId(firstDrawerId).getUsername()
            );
            chatService.sendChatMessage(roomId, newGameMsg);
            roomService.broadcastParticipants(roomId);
            updateDrawerAndBroadcast(roomId, game);
        }
    }
}
