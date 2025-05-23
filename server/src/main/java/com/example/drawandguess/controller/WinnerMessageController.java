package com.example.drawandguess.controller;

import com.example.drawandguess.service.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.Map;

import static com.example.drawandguess.config.APIConstants.WINNER_MAPPING;
import static com.example.drawandguess.config.GameConstants.USER_KEY;
import static com.example.drawandguess.config.GameConstants.MESSAGE_KEY;

/*
 * Controller that listens for a "winner message" after the game ends,
 * allowing the winner to set a custom message on the leaderboard.
 */
@Controller
public class WinnerMessageController {
    private static final Logger logger = LoggerFactory.getLogger(WinnerMessageController.class);
    private final LeaderboardService leaderboardService;

    public WinnerMessageController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    /*
     * Called when the winner sets a custom message. Updates the leaderboard accordingly.
     */
    @MessageMapping(WINNER_MAPPING)
    public void handleWinnerMessage(@Payload Map<String, String> body, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String user = body.get(USER_KEY);
            String message = body.get(MESSAGE_KEY);
            if (user != null && message != null) {
                leaderboardService.updateWinnerMessage(user, message);
            }
        } catch (Exception e) {
            logger.error("Error in handleWinnerMessage", e);
        }
    }
}
