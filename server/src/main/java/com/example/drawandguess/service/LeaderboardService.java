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
import static com.example.drawandguess.config.Constants.SCORE_SEPARATOR;

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
        String[] messageParts = message.split(SCORE_SEPARATOR);
        if (messageParts.length != 2) {
            return;
        }
        String username = messageParts[0];
        int score = Integer.parseInt(messageParts[1]);
        updateScoreInMemory(username, score);
    }

    public void updateScore(String username, int score) {
        jmsTemplate.convertAndSend(LEADERBOARD_QUEUE, username + SCORE_SEPARATOR + score);
    }

    private void updateScoreInMemory(String username, int newScore) {
        String existingValue = inMemoryLeaderboard.get(username);
        if (existingValue == null) {
            inMemoryLeaderboard.put(username, newScore + SCORE_SEPARATOR);
        } else {
            String[] scoreParts = existingValue.split(SCORE_SEPARATOR, 2);
            int oldScore = Integer.parseInt(scoreParts[0]);
            String oldMessage = scoreParts.length > 1 ? scoreParts[1] : "";
            int maxScore = Math.max(oldScore, newScore);
            inMemoryLeaderboard.put(username, maxScore + SCORE_SEPARATOR + oldMessage);
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
            String winnerMessage = message;
            if(winnerMessage == null)
                winnerMessage = "";

            Integer existingScore = getExistingScore(username);
            if (existingScore != null) {
                jdbcTemplate.update("UPDATE leaderboard SET message = ? WHERE username = ?", winnerMessage, username);
            }
        } else {
            String existingValue = inMemoryLeaderboard.get(username);
            if (existingValue == null) {
                inMemoryLeaderboard.put(username, "0" + SCORE_SEPARATOR + message);
            } else {
                String[] scoreParts = existingValue.split(SCORE_SEPARATOR, 2);
                String scoreString = scoreParts[0];
                inMemoryLeaderboard.put(username, scoreString + SCORE_SEPARATOR + message);
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
                databaseLeaderboard.put(username, score + SCORE_SEPARATOR + winnerMessage);
            });
            return databaseLeaderboard;
        } else {
            return inMemoryLeaderboard;
        }
    }

    public java.util.List<String> getSortedLeaderboard() {
        java.util.Map<String, String> board = getLeaderboard();
        java.util.List<String> data = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, String> e : board.entrySet()) {
            String username = e.getKey();
            String[] parts = e.getValue().split(SCORE_SEPARATOR, 2);
            String score = parts[0];
            String msg = parts.length > 1 ? parts[1] : "";
            data.add(username + SCORE_SEPARATOR + score + SCORE_SEPARATOR + msg);
        }
        data.sort((a, b) -> {
            String[] aa = a.split(SCORE_SEPARATOR, 3);
            String[] bb = b.split(SCORE_SEPARATOR, 3);
            int scoreA = Integer.parseInt(aa[1]);
            int scoreB = Integer.parseInt(bb[1]);
            return Integer.compare(scoreB, scoreA);
        });
        return data;
    }
}
