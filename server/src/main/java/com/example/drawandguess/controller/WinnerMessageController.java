package com.example.drawandguess.controller;

import java.io.FileWriter;
import java.io.IOException;
import static com.example.drawandguess.config.APIConstants.WINNER_MAPPING;
import static com.example.drawandguess.config.GameConstants.USER_KEY;
import static com.example.drawandguess.config.GameConstants.MESSAGE_KEY;
import static com.example.drawandguess.config.APIConstants.ERROR_LOG_FILE;
import com.example.drawandguess.service.LeaderboardService;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller
public class WinnerMessageController {
    private final LeaderboardService leaderboardService;
    public WinnerMessageController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }
    @MessageMapping(WINNER_MAPPING)
    public void handleWinnerMessage(@Payload java.util.Map<String, String> body, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String user = body.get(USER_KEY);
            String message = body.get(MESSAGE_KEY);
            if (user != null && message != null) {
                leaderboardService.updateWinnerMessage(user, message);
            }
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in handleWinnerMessage: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
        }
    }
}
