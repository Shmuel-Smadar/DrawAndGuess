package com.example.drawandguess.model;

public // A simple POJO for drawing data
class DrawMessage {
    private double normX;
    private double normY;
    private String color;
    private double brushSize;
    private String userID;
    private String eventType; // e.g. "START", "DRAW", "STOP"

    public DrawMessage() {}

    // Getters and setters for all fields

    public double getNormX() { return normX; }
    public void setNormX(double normX) { this.normX = normX; }

    public double getNormY() { return normY; }
    public void setNormY(double normY) { this.normY = normY; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public double getBrushSize() { return brushSize; }
    public void setBrushSize(double brushSize) { this.brushSize = brushSize; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}