package com.example.drawandguess.model;

// Represents an entry in the leaderboard: username, score, and a custom message.

public class LeaderboardEntry {
    private String username;
    private int score;
    private String message;

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(String username, int score, String message) {
        this.username = username;
        this.score = score;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
