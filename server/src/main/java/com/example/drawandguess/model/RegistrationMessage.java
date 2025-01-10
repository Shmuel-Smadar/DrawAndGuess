package com.example.drawandguess.model;

public class RegistrationMessage {
    private String nickname;

    public RegistrationMessage() {}

    public RegistrationMessage(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

