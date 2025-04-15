package com.example.drawandguess.model;

import java.util.UUID;

/*
 * Represents a game room with a unique ID, a user-defined name,
 * and an associated Game for tracking gameplay state.
 */
public class Room {
    private String roomId;
    private String roomName;
    private Game game;

    public Room(String roomName) {
        this.roomId = UUID.randomUUID().toString();
        this.roomName = roomName;
        this.game = new Game();
    }

    public String getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public Game getGame() { return game; }
}
