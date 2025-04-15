package com.example.drawandguess.model;

/*
 * Represents a drawing event, including coordinates,
 * color, brush size, user ID, and event type.
 */
public class DrawMessage {
    private double normX;
    private double normY;
    private String color;
    private double brushSize;
    private String userID;
    private String eventType;

    public DrawMessage() {}

    public double getNormX() { return normX; }

    public double getNormY() { return normY; }

    public String getColor() { return color; }

    public double getBrushSize() { return brushSize; }

    public String getUserID() { return userID; }

    public String getEventType() { return eventType; }
}