package com.example.drawandguess.controller;

import com.example.drawandguess.config.Constants;
import com.example.drawandguess.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping(Constants.LEADERBOARD_MAPPING)
    public List<String> getLeaderboard() {
        return leaderboardService.getSortedLeaderboard();
    }
}
