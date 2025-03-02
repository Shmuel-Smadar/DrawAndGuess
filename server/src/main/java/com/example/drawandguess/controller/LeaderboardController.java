package com.example.drawandguess.controller;

import com.example.drawandguess.service.LeaderboardConsumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class LeaderboardController {
    private final LeaderboardConsumer leaderboardConsumer;

    public LeaderboardController(LeaderboardConsumer leaderboardConsumer) {
        this.leaderboardConsumer = leaderboardConsumer;
    }

    @GetMapping("/leaderboard")
    public List<String> getLeaderboard() {
        List<String> data = new ArrayList<>();
        for (Map.Entry<String,Integer> e : leaderboardConsumer.getLeaderboard().entrySet()) {
            data.add(e.getKey() + ":" + e.getValue());
        }
        data.sort(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                int sa = Integer.parseInt(a.split(":")[1]);
                int sb = Integer.parseInt(b.split(":")[1]);
                return Integer.compare(sb, sa);
            }
        });
        return data;
    }
}
