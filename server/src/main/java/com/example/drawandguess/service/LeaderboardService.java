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

    public LeaderboardService(JmsTemplate jmsTemplate, JdbcTemplate jdbcTemplate, @Value("${USE_DB:false}") boolean useDatabase) {
        this.jmsTemplate = jmsTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.useDatabase = useDatabase;
    }

    @JmsListener(destination = LEADERBOARD_QUEUE)
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
        jmsTemplate.convertAndSend(LEADERBOARD_QUEUE, username + ":" + score);
    }

    private void updateScoreInMemory(String username, int newScore) {
        String existingValue = inMemoryLeaderboard.get(username);
        if (existingValue == null) {
            inMemoryLeaderboard.put(username, newScore + ":");
        } else {
            String[] scoreParts = existingValue.split(":", 2);
            int oldScore = Integer.parseInt(scoreParts[0]);
            String oldMessage = scoreParts.length > 1 ? scoreParts[1] : "";
            int maxScore = Math.max(oldScore, newScore);
            inMemoryLeaderboard.put(username, maxScore + ":" + oldMessage);
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
                int maxScore = Math.max(newScore, currentScore);
                jdbcTemplate.update("UPDATE leaderboard SET score = ? WHERE username = ?", maxScore, username);
            }
        } else {
            updateScoreInMemory(username, newScore);
        }
    }

    public void updateWinnerMessage(String username, String message) {
        if (useDatabase) {
            String winnerMessage = message == null ? "" : message;
            Integer existingScore = getExistingScore(username);
            if (existingScore != null) {
                jdbcTemplate.update("UPDATE leaderboard SET message = ? WHERE username = ?", winnerMessage, username);
            }
        } else {
            String existingValue = inMemoryLeaderboard.get(username);
            if (existingValue == null) {
                inMemoryLeaderboard.put(username, "0:" + message);
            } else {
                String[] scoreParts = existingValue.split(":", 2);
                String scoreString = scoreParts[0];
                inMemoryLeaderboard.put(username, scoreString + ":" + message);
            }
        }
    }

    public Map<String, String> getLeaderboard() {
        if (useDatabase) {
            Map<String, String> databaseLeaderboard = new LinkedHashMap<>();
            jdbcTemplate.query("SELECT username, score, message FROM leaderboard ORDER BY score DESC", resultSet -> {
                String username = resultSet.getString("username");
                int score = resultSet.getInt("score");
                String winnerMessage = resultSet.getString("message");
                if (winnerMessage == null) {
                    winnerMessage = "";
                }
                databaseLeaderboard.put(username, score + ":" + winnerMessage);
            });
            return databaseLeaderboard;
        } else {
            LinkedHashMap<String, String> sortedLeaderboard = new LinkedHashMap<>();
            inMemoryLeaderboard.entrySet().stream()
                    .sorted((record1, record2) -> {
                        int firstScore = Integer.parseInt(record1.getValue().split(":", 2)[0]);
                        int secondScore = Integer.parseInt(record2.getValue().split(":", 2)[0]);
                        return Integer.compare(secondScore, firstScore);
                    })
                    .forEachOrdered(record -> sortedLeaderboard.put(record.getKey(), record.getValue()));
            return sortedLeaderboard;
        }
    }
}
