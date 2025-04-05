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
    private final Map<String, Integer> leaderboard = new ConcurrentHashMap<>();

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
        String user = parts[0];
        int score = Integer.parseInt(parts[1]);
        leaderboard.put(user, score);
    }

    public void updateScore(String username, int score) {
        jmsTemplate.convertAndSend(LEADERBOARD_QUEUE, username + ":" + score);
    }

    public void saveScores(Map<String, Integer> scores) {
        if (!useDatabase) {
            return;
        }
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            String username = e.getKey();
            int finalScore = e.getValue();
            Integer existingScore;
            try {
                existingScore = jdbcTemplate.queryForObject(
                        "SELECT score FROM leaderboard WHERE username = ?",
                        Integer.class,
                        username
                );
            } catch (EmptyResultDataAccessException ex) {
                existingScore = null;
            }
            if (existingScore == null) {
                jdbcTemplate.update(
                        "INSERT INTO leaderboard (username, score) VALUES (?, ?)",
                        username,
                        finalScore
                );
            } else {
                jdbcTemplate.update(
                        "UPDATE leaderboard SET score = ? WHERE username = ?",
                        Math.max(finalScore, existingScore),
                        username
                );
            }
        }
    }

    public void updateWinnerMessage(String username, String message) {
        if (!useDatabase) {
            return;
        }
        String winnerMessage = message == null ? "" : message;
        Integer existingScore;
        try {
            existingScore = jdbcTemplate.queryForObject(
                    "SELECT score FROM leaderboard WHERE username = ?",
                    Integer.class,
                    username
            );
        } catch (EmptyResultDataAccessException ex) {
            existingScore = null;
        }
        if (existingScore != null) {
            jdbcTemplate.update(
                    "UPDATE leaderboard SET winner_message = ? WHERE username = ?",
                    winnerMessage,
                    username
            );
        }
    }

    public Map<String, String> getLeaderboard() {
        if (!useDatabase) {
            LinkedHashMap<String, String> copySorted = new LinkedHashMap<>();
            leaderboard.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                    .forEachOrdered(e -> copySorted.put(e.getKey(), e.getValue() + ":"));
            return copySorted;
        }
        Map<String, String> dbResult = new LinkedHashMap<>();
        jdbcTemplate.query(
                "SELECT username, score, COALESCE(winner_message,'') AS wm FROM leaderboard ORDER BY score DESC",
                rs -> {
                    String u = rs.getString("username");
                    int s = rs.getInt("score");
                    String m = rs.getString("wm");
                    dbResult.put(u, s + ":" + m);
                }
        );
        return dbResult;
    }
}
