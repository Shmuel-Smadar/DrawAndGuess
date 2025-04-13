package com.example.drawandguess.service;

import com.example.drawandguess.model.LeaderboardEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class LeaderboardService {
    private final JmsTemplate jmsTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final boolean useDatabase;
    private final Map<String, LeaderboardEntry> inMemoryLeaderboard = new ConcurrentHashMap<>();

    public LeaderboardService(JmsTemplate jmsTemplate, JdbcTemplate jdbcTemplate, @Value("${USE_DB:false}") boolean useDatabase) {
        this.jmsTemplate = jmsTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.useDatabase = useDatabase;
    }

    @JmsListener(destination = "leaderboardQueue")
    public void receiveScoreUpdate(String message) {
        String[] messageParts = message.split(":");
        if (messageParts.length != 2) {
            return;
        }
        String username = messageParts[0];
        int score = Integer.parseInt(messageParts[1]);
        updateScoreInMemory(username, score);
    }

    public void updateScore(String username, int score) {
        jmsTemplate.convertAndSend("leaderboardQueue", username + ":" + score);
    }

    private void updateScoreInMemory(String username, int newScore) {
        LeaderboardEntry entry = inMemoryLeaderboard.get(username);
        if (entry == null) {
            inMemoryLeaderboard.put(username, new LeaderboardEntry(username, newScore, ""));
        } else {
            int oldScore = entry.getScore();
            entry.setScore(Math.max(oldScore, newScore));
        }
    }

    private Integer getExistingScore(String username) {
        try {
            return jdbcTemplate.queryForObject("SELECT score FROM leaderboard WHERE username = ?", Integer.class, username);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public void saveScores(String username, int newScore) {
        if (useDatabase) {
            Integer currentScore = getExistingScore(username);
            if (currentScore == null) {
                jdbcTemplate.update("INSERT INTO leaderboard (username, score) VALUES (?, ?)", username, newScore);
            } else {
                jdbcTemplate.update("UPDATE leaderboard SET score = ? WHERE username = ?", Math.max(newScore, currentScore), username);
            }
        } else {
            updateScoreInMemory(username, newScore);
        }
    }

    public void updateWinnerMessage(String username, String message) {
        if (useDatabase) {
            if (message == null) {
                message = "";
            }
            Integer existingScore = getExistingScore(username);
            if (existingScore != null) {
                jdbcTemplate.update("UPDATE leaderboard SET message = ? WHERE username = ?", message, username);
            }
        } else {
            LeaderboardEntry entry = inMemoryLeaderboard.get(username);
            if (entry == null) {
                inMemoryLeaderboard.put(username, new LeaderboardEntry(username, 0, message));
            } else {
                entry.setMessage(message);
            }
        }
    }

    public Map<String, LeaderboardEntry> getLeaderboard() {
        if (useDatabase) {
            Map<String, LeaderboardEntry> databaseLeaderboard = new LinkedHashMap<>();
            jdbcTemplate.query("SELECT username, score, message FROM leaderboard ORDER BY score DESC", rs -> {
                String username = rs.getString("username");
                int score = rs.getInt("score");
                String winnerMessage = rs.getString("message");
                if (winnerMessage == null) {
                    winnerMessage = "";
                }
                databaseLeaderboard.put(username, new LeaderboardEntry(username, score, winnerMessage));
            });
            return databaseLeaderboard;
        } else {
            return inMemoryLeaderboard;
        }
    }

    public List<LeaderboardEntry> getSortedLeaderboardEntries() {
        Map<String, LeaderboardEntry> board = getLeaderboard();
        List<LeaderboardEntry> data = new ArrayList<>(board.values());
        data.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return data;
    }
}
