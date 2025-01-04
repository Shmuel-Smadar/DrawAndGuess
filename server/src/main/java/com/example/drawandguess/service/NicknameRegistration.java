package com.example.drawandguess.service;

import org.springframework.stereotype.Service;
import com.example.drawandguess.model.NicknameStatus;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class NicknameRegistration {
    private final Map<String, String> sessionToNickname = new ConcurrentHashMap<>();

    public NicknameStatus registerNickname(String sessionId, String nickname) {
        if (isNicknameTaken(nickname)) {
            return new NicknameStatus(false, "Nickname is already taken");
        }
        // Otherwise store it
        sessionToNickname.put(sessionId, nickname);
        System.out.println("Registered\n");
        return new NicknameStatus(true, "Nickname registered successfully");
    }

    public void removeNickname(String sessionId) {
        sessionToNickname.remove(sessionId);
    }
    public boolean isNicknameTaken(String nickname) {
        return sessionToNickname.containsValue(nickname);
    }
    public String findNickname(String sessionId) {
        return sessionToNickname.get(sessionId);
    }
}
