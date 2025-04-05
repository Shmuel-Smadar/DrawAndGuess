package com.example.drawandguess.controller;

import com.example.drawandguess.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/leaderboard")
    public List<String> getLeaderboard() {
        List<String> data = new ArrayList<>();
        for (Map.Entry<String,String> e : leaderboardService.getLeaderboard().entrySet()) {
            String username = e.getKey();
            String[] parts = e.getValue().split(":");
            String score = parts[0];
            String msg = parts.length > 1 ? parts[1] : "";
            data.add(username + ":" + score + ":" + msg);
        }
        data.sort(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                String[] aa = a.split(":");
                String[] bb = b.split(":");
                int sa = Integer.parseInt(aa[1]);
                int sb = Integer.parseInt(bb[1]);
                return Integer.compare(sb, sa);
            }
        });
        return data;
    }
}
