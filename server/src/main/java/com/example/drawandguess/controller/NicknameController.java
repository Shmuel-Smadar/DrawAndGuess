// src/main/java/com/example/drawandguess/controller/NicknameController.java
package com.example.drawandguess.controller;

import com.example.drawandguess.model.NicknameStatus;
import com.example.drawandguess.model.RegistrationMessage;
import com.example.drawandguess.service.NicknameRegistration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.Payload;

@Controller
public class NicknameController {
    private final NicknameRegistration nicknameRegistration;

    public NicknameController(NicknameRegistration nicknameRegistration) {
        this.nicknameRegistration = nicknameRegistration;
    }

    @MessageMapping("/registerNickname")
    @SendToUser("/topic/nickname")
    public NicknameStatus registerNickname(@Payload RegistrationMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        return nicknameRegistration.registerNickname(sessionId, message.getNickname());
    }
}
