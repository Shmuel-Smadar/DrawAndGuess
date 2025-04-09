package com.example.drawandguess.controller;

import static com.example.drawandguess.config.PathConstants.REGISTER_NICKNAME;
import static com.example.drawandguess.config.PathConstants.NICKNAME_TOPIC;
import static com.example.drawandguess.config.GameConstants.NICKNAME_REGEX;
import static com.example.drawandguess.config.GameConstants.INVALID_NICKNAME_MSG;

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

    @MessageMapping(REGISTER_NICKNAME)
    @SendToUser(NICKNAME_TOPIC)
    public NicknameStatus registerNickname(@Payload RegistrationRequest request, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String nickname = request.getNickname().trim();
        if (!nickname.matches(NICKNAME_REGEX)) {
            return new NicknameStatus(false, INVALID_NICKNAME_MSG);
        }
        NicknameStatus status = participantService.registerParticipant(sessionId, nickname);
        status.setSessionId(sessionId);
        return status;
    }
}
