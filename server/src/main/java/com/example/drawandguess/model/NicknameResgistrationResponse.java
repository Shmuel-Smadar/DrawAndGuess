package com.example.drawandguess.model;

/*
 * Represents the response from registering a nickname:
 * success/failure, message to explain erros, and sessionId if successful.
 */
public class NicknameResgistrationResponse {
    private boolean success;
    private String message;
    private String sessionId;

    public NicknameResgistrationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public NicknameResgistrationResponse(boolean success, String message, String sessionId) {
        this.success = success;
        this.message = message;
        this.sessionId = sessionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}