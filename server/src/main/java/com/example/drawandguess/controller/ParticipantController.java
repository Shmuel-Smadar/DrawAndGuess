package com.example.drawandguess.controller;

import com.example.drawandguess.model.NicknameStatus;
import com.example.drawandguess.model.RegistrationRequest;
import com.example.drawandguess.service.ParticipantService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.Payload;

@Controller
public class ParticipantController {
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @MessageMapping("/registerNickname")
    @SendToUser("/topic/nickname")
    public NicknameStatus registerNickname(@Payload RegistrationRequest request, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        NicknameStatus status = participantService.registerParticipant(sessionId, request.getNickname());
        status.setSessionId(sessionId);
        return status;
    }
}