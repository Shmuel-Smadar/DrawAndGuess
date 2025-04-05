package com.example.drawandguess.controller;

import com.example.drawandguess.service.LeaderboardService;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.Map;

@Controller
public class WinnerController {

    private final LeaderboardService leaderboardService;

    public WinnerController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @MessageMapping("/winnerMessage")
    public void handleWinnerMessage(@Payload Map<String, String> body, SimpMessageHeaderAccessor headerAccessor) {
        String user = body.get("user");
        String message = body.get("message");
        if (user != null && message != null) {
            leaderboardService.updateWinnerMessage(user, message);
        }
    }
}
