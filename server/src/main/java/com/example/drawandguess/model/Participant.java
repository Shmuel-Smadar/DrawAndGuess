package com.example.drawandguess.model;

public class Participant {
    private String socketID;
    private String username;
    private boolean isDrawer;

    public Participant() {}

    public Participant(String socketID, String username, boolean isDrawer) {
        this.socketID = socketID;
        this.username = username;
        this.isDrawer = isDrawer;
    }

    public String getSocketID() {
        return socketID;
    }

    public void setSocketID(String socketID) {
        this.socketID = socketID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isDrawer() {
        return isDrawer;
    }

    public void setDrawer(boolean drawer) {
        isDrawer = drawer;
    }
}
