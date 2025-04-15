package com.example.drawandguess.service;

import static com.example.drawandguess.config.GameConstants.HINT_INTERVAL_SECONDS;

import com.example.drawandguess.model.Game;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/*
 * A servie that Handles scheduling and sending incremental hints (revealing letters) to participants.
 * if the word is fully revealed and no one guesses, triggers the "no guess" function.
 */
@Service
public class HintService {
    private final TaskScheduler taskScheduler;
    private final ChatService chatService;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> hintTasks = new ConcurrentHashMap<>();

    public HintService(TaskScheduler taskScheduler, ChatService chatService, DrawingService drawingService) {
        this.taskScheduler = taskScheduler;
        this.chatService = chatService;
    }

    /* A method responsible for sending hints to a given room,
    * and a function to call when hints are over */
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
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(runnable, Duration.ofSeconds(HINT_INTERVAL_SECONDS));
        hintTasks.put(roomId, future);
    }

    public void stopHintProgression(String roomId) {
        ScheduledFuture<?> scheduledFuture = hintTasks.remove(roomId);
        if (scheduledFuture != null) scheduledFuture.cancel(false);
    }

    private void sendHintToParticipants(String roomId, String currentHint, Game game) {
        chatService.sendWordHint(roomId, currentHint);
    }
}
