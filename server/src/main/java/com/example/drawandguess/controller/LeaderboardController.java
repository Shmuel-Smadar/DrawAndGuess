package com.example.drawandguess.controller;

import static com.example.drawandguess.config.APIConstants.LEADERBOARD_MAPPING;
import static com.example.drawandguess.config.APIConstants.ALLOWED_ORIGINS;
import static com.example.drawandguess.config.APIConstants.ERROR_LOG_FILE;
import com.example.drawandguess.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import java.util.List; import java.util.ArrayList;
import java.io.FileWriter; import java.io.IOException;

@RestController
@CrossOrigin(origins = ALLOWED_ORIGINS)
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping(LEADERBOARD_MAPPING)
    public List<String> getLeaderboard() {
        try {
            return leaderboardService.getSortedLeaderboard();
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in getLeaderboard: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
            return new ArrayList<>();
        }
    }
}