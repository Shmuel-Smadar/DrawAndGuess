package com.example.drawandguess.service;

import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.model.WordOptions;
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

    public GameService(
            ChatService chatService,
            ParticipantService participantService,
            RoomService roomService,
            TaskScheduler taskScheduler
    ) {
        this.chatService = chatService;
        this.participantService = participantService;
        this.roomService = roomService;
        this.taskScheduler = taskScheduler;
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
            msg.setText(
                    "A new round has started. The drawer is " +
                            participantService.findParticipantBySessionId(sessionId).getUsername() + "."
            );
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
            ChatMessage msg = new ChatMessage();
            msg.setSenderSessionId("system");
            msg.setText(username + " guessed the word correctly! Starting next round.");
            msg.setType("system");
            chatService.sendChatMessage(roomId, msg);
            stopHintProgression(roomId);
            game.nextRound();
            String newDrawerId = game.getCurrentDrawer();
            participantService.getAllParticipants().values().forEach(p ->
                    participantService.setDrawer(
                            p.getSessionId(),
                            p.getSessionId().equals(newDrawerId)
                    )
            );
            roomService.broadcastParticipants(roomId);
        }
    }

    public void startHintProgression(String roomId) {
        if (hintTasks.containsKey(roomId)) {
            return;
        }

        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Runnable hintTask = getRunnable(roomId, room);
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(hintTask, Duration.ofSeconds(10));
        hintTasks.put(roomId, future);
    }

    private Runnable getRunnable(String roomId, Room room) {
        Game game = room.getGame();

        return new Runnable() {
            @Override
            public void run() {
                synchronized (game) {
                    if (!game.hasMoreHints()) {
                        stopHintProgression(roomId);
                        return;
                    }
                    String hint = game.nextHint();
                    sendHintToParticipants(roomId, hint);
                    if (!game.hasMoreHints()) {
                        stopHintProgression(roomId);
                    }
                }
            }
        };
    }

    private void stopHintProgression(String roomId) {
        ScheduledFuture<?> future = hintTasks.remove(roomId);
        if (future != null) {
            future.cancel(false);
        }
    }

    private void sendHintToParticipants(String roomId, String currentHint) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        List<String> participantIds = room.getGame().getParticipantSessionIds();
        String drawerId = room.getGame().getCurrentDrawer();
        participantIds.stream()
                .filter(sessionId -> !sessionId.equals(drawerId))
                .forEach(sessionId -> {
                    chatService.sendWordHint(sessionId, roomId, currentHint);
                });
    }
}