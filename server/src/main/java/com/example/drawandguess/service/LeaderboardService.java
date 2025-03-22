package com.example.drawandguess.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.drawandguess.config.Constants.LEADERBOARD_QUEUE;

@Service
public class LeaderboardService {
    private final JmsTemplate jmsTemplate;
    private final Map<String,Integer> leaderboard = new ConcurrentHashMap<>();

    public LeaderboardService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void updateScore(String username, int score) {
        jmsTemplate.convertAndSend(LEADERBOARD_QUEUE, username + ":" + score);
    }

    @JmsListener(destination = LEADERBOARD_QUEUE)
    public void receiveScoreUpdate(String message) {
        String[] parts = message.split(":");
        String user = parts[0];
        int score = Integer.parseInt(parts[1]);
        leaderboard.put(user, score);
    }

    public Map<String,Integer> getLeaderboard() {
        return leaderboard;
    }
}
