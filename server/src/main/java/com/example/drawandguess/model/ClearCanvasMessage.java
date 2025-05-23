package com.example.drawandguess.model;

/*
 * Represents a message indicating that the canvas should be cleared.
 */
public class ClearCanvasMessage {
    private String userID;

    public ClearCanvasMessage() {}

    public ClearCanvasMessage(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}