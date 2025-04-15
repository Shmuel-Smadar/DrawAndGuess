package com.example.drawandguess.model;

/*
 * Represents the user’s request to register a nickname.
 */
public class RegistrationRequest {
    private String nickname;

    public RegistrationRequest() {}

    public RegistrationRequest(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

