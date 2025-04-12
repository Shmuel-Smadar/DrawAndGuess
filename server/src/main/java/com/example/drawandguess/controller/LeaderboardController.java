package com.example.drawandguess.controller;

import static com.example.drawandguess.config.APIConstants.LEADERBOARD_MAPPING;
import static com.example.drawandguess.config.APIConstants.ALLOWED_ORIGINS;
import com.example.drawandguess.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@CrossOrigin(origins = ALLOWED_ORIGINS)
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping(LEADERBOARD_MAPPING)
    public List<String> getLeaderboard() {
        return leaderboardService.getSortedLeaderboard();
    }
}
