package com.example.drawandguess.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashMap;
import static com.example.drawandguess.config.Constants.LEADERBOARD_QUEUE;

@Service
public class LeaderboardService {
    private final JmsTemplate jmsTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final boolean useDatabase;
    private final Map<String, String> inMemoryLeaderboard = new ConcurrentHashMap<>();

    public LeaderboardService(
            JmsTemplate jmsTemplate,
            JdbcTemplate jdbcTemplate,
            @Value("${USE_DB:false}") boolean useDatabase
    ) {
        this.jmsTemplate = jmsTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.useDatabase = useDatabase;
    }

    @JmsListener(destination = LEADERBOARD_QUEUE)
    public void receiveScoreUpdate(String message) {
        String[] parts = message.split(":");
        if (parts.length != 2) {
            return;
        }
        String username = parts[0];
        int score = Integer.parseInt(parts[1]);
        updateScoreInMemory(username, score);
    }

    public void updateScore(String username, int score) {
        jmsTemplate.convertAndSend(LEADERBOARD_QUEUE, username + ":" + score);
    }

    private void updateScoreInMemory(String username, int newScore) {
        String existingValue = inMemoryLeaderboard.get(username);
        if (existingValue == null) {
            inMemoryLeaderboard.put(username, newScore + ":");
        } else {
            String[] arr = existingValue.split(":", 2);
            int oldScore = Integer.parseInt(arr[0]);
            String oldMsg = arr.length > 1 ? arr[1] : "";
            int mergedScore = Math.max(oldScore, newScore);
            inMemoryLeaderboard.put(username, mergedScore + ":" + oldMsg);
        }
    }

    private Integer getExistingScore(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT score FROM leaderboard WHERE username = ?",
                    Integer.class,
                    username
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public void saveScores(Map<String, Integer> scores) {
        if (!useDatabase) {
            for (Map.Entry<String, Integer> e : scores.entrySet()) {
                updateScoreInMemory(e.getKey(), e.getValue());
            }
            return;
        }
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String username = entry.getKey();
            int newScore = entry.getValue();
            Integer currentScore = getExistingScore(username);
            if (currentScore == null) {
                jdbcTemplate.update(
                        "INSERT INTO leaderboard (username, score) VALUES (?, ?)",
                        username,
                        newScore
                );
            } else {
                int maxScore = Math.max(newScore, currentScore);
                jdbcTemplate.update(
                        "UPDATE leaderboard SET score = ? WHERE username = ?",
                        maxScore,
                        username
                );
            }
        }
    }

    public void updateWinnerMessage(String username, String message) {
        if (!useDatabase) {
            String existingValue = inMemoryLeaderboard.get(username);
            if (existingValue == null) {
                inMemoryLeaderboard.put(username, "0:" + message);
            } else {
                String[] arr = existingValue.split(":", 2);
                String sscore = arr[0];
                inMemoryLeaderboard.put(username, sscore + ":" + message);
            }
            return;
        }
        String winnerMessage = message == null ? "" : message;
        Integer existingScore = getExistingScore(username);
        if (existingScore != null) {
            jdbcTemplate.update(
                    "UPDATE leaderboard SET message = ? WHERE username = ?",
                    winnerMessage,
                    username
            );
        }
    }

    public Map<String, String> getLeaderboard() {
        if (!useDatabase) {
            LinkedHashMap<String, String> sortedLeaderboard = new LinkedHashMap<>();
            inMemoryLeaderboard.entrySet().stream()
                    .sorted((a, b) -> {
                        String[] aa = a.getValue().split(":", 2);
                        String[] bb = b.getValue().split(":", 2);
                        int sa = Integer.parseInt(aa[0]);
                        int sb = Integer.parseInt(bb[0]);
                        return Integer.compare(sb, sa);
                    })
                    .forEachOrdered(e -> sortedLeaderboard.put(e.getKey(), e.getValue()));
            return sortedLeaderboard;
        }
        Map<String, String> dbLeaderboard = new LinkedHashMap<>();
        jdbcTemplate.query(
                "SELECT username, score, COALESCE(message,'') AS message FROM leaderboard ORDER BY score DESC",
                rs -> {
                    String username = rs.getString("username");
                    int score = rs.getInt("score");
                    String winnerMessage = rs.getString("message");
                    dbLeaderboard.put(username, score + ":" + winnerMessage);
                }
        );
        return dbLeaderboard;
    }
}
