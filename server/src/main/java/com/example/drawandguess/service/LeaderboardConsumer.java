package com.example.drawandguess.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LeaderboardConsumer {
    private final Map<String,Integer> leaderboard = new ConcurrentHashMap<>();

    @JmsListener(destination = "leaderboardQueue")
    public void receiveScoreUpdate(String message) {
        System.out.println("Received: " + message);
        String[] parts = message.split(":");
        String username = parts[0];
        int score = Integer.parseInt(parts[1]);
        leaderboard.put(username, score);
    }

    public Map<String,Integer> getLeaderboard() {
        return leaderboard;
    }
}
