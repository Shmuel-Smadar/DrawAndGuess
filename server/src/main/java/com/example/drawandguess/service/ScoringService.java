package com.example.drawandguess.service;

import com.example.drawandguess.config.Constants;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.MessageType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class ScoringService {
    private final ParticipantService participantService;
    private final LeaderboardService leaderboardService;
    private final MessageService messageService;
    private final ChatService chatService;
    private final DrawingService drawingService;
    private final RoomService roomService;
    private final TaskScheduler taskScheduler;

    public ScoringService(ParticipantService participantService, LeaderboardService leaderboardService, MessageService messageService, ChatService chatService, DrawingService drawingService, RoomService roomService, TaskScheduler taskScheduler) {
        this.participantService = participantService;
        this.leaderboardService = leaderboardService;
        this.messageService = messageService;
        this.chatService = chatService;
        this.drawingService = drawingService;
        this.roomService = roomService;
        this.taskScheduler = taskScheduler;
    }

    public void handleScoring(String roomId, Game game, String guess, String sessionId) {
        String username = participantService.findParticipantBySessionId(sessionId).getUsername();
        int used = game.getHintsUsed();
        int multiplier = Math.max(1, Constants.MULTIPLIER_BASE - used);
        int guesserPoints = Constants.GUESSER_BASE_POINTS * multiplier;
        game.addScore(username, guesserPoints);
        if (game.getCurrentDrawer() != null) {
            String drawerId = game.getCurrentDrawer();
            String drawerName = participantService.findParticipantBySessionId(drawerId).getUsername();
            int drawerPoints = Constants.DRAWER_BASE_POINTS * multiplier;
            game.addScore(drawerName, drawerPoints);
        }
        roomService.broadcastParticipants(roomId);
        drawingService.clearCanvas(roomId, new com.example.drawandguess.model.ClearCanvasMessage("system"));
        game.nextRound();
        if (game.isGameOver()) {
            endGameAndStartNew(roomId, game, Constants.GAME_ENDED_MSG);
        } else {
            ChatMessage msg = messageService.systemMessage(MessageType.WORD_GUESSED, username);
            chatService.sendChatMessage(roomId, msg);
            updateDrawerAndBroadcast(roomId, game);
        }
    }

    
}
