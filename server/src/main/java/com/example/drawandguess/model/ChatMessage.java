package com.example.drawandguess.model;

public class ChatMessage {
    private String text;
    private String senderSessionId;
    private String senderUsername;
    private String type;

    public ChatMessage() {
    }

    public ChatMessage(String text, String senderSessionId, String senderUsername, String type) {
        this.text = text;
        this.senderSessionId = senderSessionId;
        this.senderUsername = senderUsername;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderSessionId() {
        return senderSessionId;
    }

    public void setSenderSessionId(String senderSessionId) {
        this.senderSessionId = senderSessionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}