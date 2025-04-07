package com.example.drawandguess.service;

import com.example.drawandguess.config.Constants;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.ClearCanvasMessage;
import com.example.drawandguess.model.MessageType;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {
    private final ParticipantService participantService;

    public ScoringService(ParticipantService participantService) {
        this.participantService = participantService;
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
    }
}
