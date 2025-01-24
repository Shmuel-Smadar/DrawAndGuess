package com.example.drawandguess.model;

public
class DrawMessage {
    private double normX;
    private double normY;
    private String color;
    private double brushSize;
    private String userID;
    private String eventType; // // TODO: make it an enum ("START", "DRAW", "STOP")

    public DrawMessage() {}

    public double getNormX() { return normX; }

    public double getNormY() { return normY; }

    public String getColor() { return color; }

    public double getBrushSize() { return brushSize; }

    public String getUserID() { return userID; }

    public String getEventType() { return eventType; }
}