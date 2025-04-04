package com.example.drawandguess.model;

public class ChatMessage {
    private String text;
    private String senderSessionId;
    private String senderUsername;
    private String type;
    private MessageType messageType;

    public ChatMessage() {
    }

    public ChatMessage(String text, String senderSessionId, String senderUsername, String type, MessageType messageType) {
        this.text = text;
        this.senderSessionId = senderSessionId;
        this.senderUsername = senderUsername;
        this.type = type;
        this.messageType = messageType;
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

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
