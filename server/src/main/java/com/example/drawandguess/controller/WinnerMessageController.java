package com.example.drawandguess.controller;

import com.example.drawandguess.config.Constants;
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

    @MessageMapping(Constants.WINNER_MAPPING)
    public void handleWinnerMessage(@Payload java.util.Map<String, String> body, SimpMessageHeaderAccessor headerAccessor) {
        String user = body.get(Constants.USER_KEY);
        String message = body.get(Constants.MESSAGE_KEY);
        if (user != null && message != null) {
            leaderboardService.updateWinnerMessage(user, message);
        }
    }
}
