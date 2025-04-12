package com.example.drawandguess.service;

import static com.example.drawandguess.config.GameConstants.GUESSER_BASE_POINTS;
import static com.example.drawandguess.config.GameConstants.DRAWER_BASE_POINTS;
import static com.example.drawandguess.config.GameConstants.MULTIPLIER_BASE;
import static com.example.drawandguess.config.GameConstants.gameEndedMsg;
import static com.example.drawandguess.config.GameConstants.finalScoreMsgAfter;
import static com.example.drawandguess.config.GameConstants.finalScoreMsgRounds;
import static com.example.drawandguess.config.GameConstants.finalScoreMsgNewGame;
import static com.example.drawandguess.config.GameConstants.finalScoreMsgSeconds;
import static com.example.drawandguess.config.GameConstants.NEW_GAME_DELAY_SECONDS;
import com.example.drawandguess.model.Game;
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

    public String buildFinalScoreMessage(Game game) {
        StringBuilder sb = new StringBuilder(gameEndedMsg())
                .append(finalScoreMsgAfter())
                .append(game.getTotalRounds())
                .append(finalScoreMsgRounds());
        for (String pid : game.getParticipantSessionIds()) {
            String username = participantService.findParticipantBySessionId(pid).getUsername();
            sb.append(username).append("=").append(game.getScore(username)).append("  ");
        }
        sb.append("./n");
        sb.append(finalScoreMsgNewGame())
                .append(NEW_GAME_DELAY_SECONDS)
                .append(finalScoreMsgSeconds());
        return sb.toString();
    }
}
