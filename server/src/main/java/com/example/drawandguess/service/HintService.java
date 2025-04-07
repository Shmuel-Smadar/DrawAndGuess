package com.example.drawandguess.service;

import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.Room;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class HintService {
    private final TaskScheduler taskScheduler;
    private final ChatService chatService;
    private final RoomService roomService;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> hintTasks = new ConcurrentHashMap<>();

    public HintService(TaskScheduler taskScheduler, ChatService chatService, RoomService roomService, DrawingService drawingService) {
        this.taskScheduler = taskScheduler;
        this.chatService = chatService;
        this.roomService = roomService;
    }

    public void startHintProgression(String roomId, Game game, Runnable onNoHint) {
        if (hintTasks.containsKey(roomId)) return;
        Runnable runnable = () -> {
            synchronized (game) {
                if (!game.hasMoreHints()) {
                    onNoHint.run();
                    return;
                }
                String hint = game.nextHint();
                sendHintToParticipants(roomId, hint, game);
                if (!game.hasMoreHints()) {
                    onNoHint.run();
                }
            }
        };
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(runnable, Duration.ofSeconds(com.example.drawandguess.config.Constants.HINT_INTERVAL_SECONDS));
        hintTasks.put(roomId, future);
    }

    public void stopHintProgression(String roomId) {
        ScheduledFuture<?> scheduledFuture = hintTasks.remove(roomId);
        if (scheduledFuture != null) scheduledFuture.cancel(false);
    }

    private void sendHintToParticipants(String roomId, String currentHint, Game game) {
        Room room = roomService.getRoom(roomId);
        if (room == null) {
            return;
        }

        Game roomGame = room.getGame();
        List<String> participantSessions = roomGame.getParticipantSessionIds();
        String currentDrawer = roomGame.getCurrentDrawer();

        participantSessions.stream()
                .filter(session -> !session.equals(currentDrawer))
                .forEach(session -> chatService.sendWordHint(session, roomId, currentHint));
    }
}
