package com.example.drawandguess.service;

import static com.example.drawandguess.config.GameConstants.GUESSER_BASE_POINTS;
import static com.example.drawandguess.config.GameConstants.DRAWER_BASE_POINTS;
import static com.example.drawandguess.config.GameConstants.MULTIPLIER_BASE;
import com.example.drawandguess.model.Game;
import org.springframework.stereotype.Service;

/*
 * A service that calculates and assigns points to the guesser and drawer at round completion.
 * Also helps determine the game’s winner.
 */
@Service
public class ScoringService {
    private final ParticipantService participantService;

    public ScoringService(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /*
     * A method that add points to the guesser and the current drawer,
     * factoring in how many hints have been revealed. (more hints = lower score)
     */
    public void handleScoring(String roomId, Game game, String guess, String sessionId) {
        String username = participantService.findParticipantBySessionId(sessionId).getUsername();
        int used = game.getHintsUsed();
        int multiplier = Math.max(1, MULTIPLIER_BASE - used);
        int guesserPoints = GUESSER_BASE_POINTS * multiplier;
        game.addScore(username, guesserPoints);

        if (game.getCurrentDrawer() != null) {
            String drawerId = game.getCurrentDrawer();
            String drawerName = participantService.findParticipantBySessionId(drawerId).getUsername();
            int drawerPoints = DRAWER_BASE_POINTS * multiplier;
            game.addScore(drawerName, drawerPoints);
        }
    }

    
    // A method that finds the winner in a game. return null if it's a tie.
    public String getWinnerSessionId(Game game) {
        String winnerSessionId = null;
        int maxScore = -1;
        boolean tie = false;
        for (String pid : game.getParticipantSessionIds()) {
            String username = participantService.findParticipantBySessionId(pid).getUsername();
            int score = game.getScore(username);
            if (score > maxScore) {
                maxScore = score;
                winnerSessionId = pid;
                tie = false;
            } else if (score == maxScore) {
                tie = true;
            }
        }
        return (winnerSessionId != null && !tie) ? winnerSessionId : null;
    }

    // A method that builds the message notifying about the end of the game and the results.
    public String buildFinalScoreMessage(Game game) {
        StringBuilder scorePart = new StringBuilder();
        for (String pid : game.getParticipantSessionIds()) {
            String username = participantService.findParticipantBySessionId(pid).getUsername();
            scorePart.append(username).append("=").append(game.getScore(username)).append("  ");
        }

        return scorePart.toString();
    }
}
