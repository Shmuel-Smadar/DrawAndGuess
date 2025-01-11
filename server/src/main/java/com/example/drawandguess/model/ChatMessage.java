package com.example.drawandguess.model;

public class ChatMessage {
    private String text;
    private String sender;
    private String type;

    public ChatMessage() {
    }

    public ChatMessage(String text, String sender, String type) {
        this.text = text;
        this.sender = sender;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}