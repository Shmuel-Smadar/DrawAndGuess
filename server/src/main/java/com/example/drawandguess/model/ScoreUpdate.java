package com.example.drawandguess.model;

import java.io.Serializable;

/*
 * Represents a score update for the leaderboard queue.
 */
public class ScoreUpdate implements Serializable {
    private String username;
    private int newScore;

    public ScoreUpdate(String username, int newScore) {
        this.username = username;
        this.newScore = newScore;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getNewScore() { return newScore; }
    public void setNewScore(int newScore) { this.newScore = newScore; }
}