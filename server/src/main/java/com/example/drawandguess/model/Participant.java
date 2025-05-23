package com.example.drawandguess.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Represents a user participant in a room/game, including session ID,
 * nickname (username), a drawer flag, and the participant's score.
 */
public class Participant {
    private String sessionId;
    private String username;
    @JsonProperty("isDrawer")
    private boolean isDrawer;
    private int score;

    public Participant() {}

    public Participant(String sessionId, String username, boolean isDrawer) {
        this.sessionId = sessionId;
        this.username = username;
        this.isDrawer = isDrawer;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    @JsonProperty("isDrawer")
    public boolean isDrawer() {
        return isDrawer;
    }

    @JsonProperty("isDrawer")
    public void setDrawer(boolean drawer) {
        this.isDrawer = drawer;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
