package com.example.drawandguess.controller;

import com.example.drawandguess.model.LeaderboardEntry;
import com.example.drawandguess.service.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ArrayList;

import static com.example.drawandguess.config.APIConstants.LEADERBOARD_MAPPING;
import static com.example.drawandguess.config.APIConstants.ALLOWED_ORIGINS;

/*
 * A controller that exposes a REST endpoint to retrieve the leaderboard entries (sorted by score).
 */
@RestController
@CrossOrigin(origins = ALLOWED_ORIGINS)
public class LeaderboardController {
    private static final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    /*
     * Gets the sorted leaderboard as a list of LeaderboardEntry model.
     */
    @GetMapping(LEADERBOARD_MAPPING)
    public List<LeaderboardEntry> getLeaderboard() {
        try {
            return leaderboardService.getSortedLeaderboardEntries();
        } catch (Exception e) {
            logger.error("Error in getLeaderboard", e);
            return new ArrayList<>();
        }
    }
}
