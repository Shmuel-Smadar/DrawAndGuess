package com.example.drawandguess.controller;

import com.example.drawandguess.model.NicknameResgistrationResponse;
import com.example.drawandguess.model.RegistrationRequest;
import com.example.drawandguess.model.MessageType;
import com.example.drawandguess.service.ParticipantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.Payload;

import static com.example.drawandguess.config.APIConstants.REGISTER_NICKNAME;
import static com.example.drawandguess.config.APIConstants.NICKNAME_TOPIC;
import static com.example.drawandguess.config.GameConstants.NICKNAME_REGEX;
import static com.example.drawandguess.config.GameConstants.buildSystemMessage;
import static com.example.drawandguess.model.MessageType.INVALID_NICKNAME;

@Controller
public class ParticipantController {
    private static final Logger logger = LoggerFactory.getLogger(ParticipantController.class);
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @MessageMapping(REGISTER_NICKNAME)
    @SendToUser(NICKNAME_TOPIC)
    public NicknameResgistrationResponse registerNickname(@Payload RegistrationRequest request, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            String nickname = request.getNickname().trim();
            if (!nickname.matches(NICKNAME_REGEX)) {
                return new NicknameResgistrationResponse(false, buildSystemMessage(INVALID_NICKNAME));
            }
            NicknameResgistrationResponse res = participantService.registerParticipant(sessionId, nickname);
            res.setSessionId(sessionId);
            return res;
        } catch (Exception e) {
            logger.error("Error in registerNickname", e);
            return new NicknameResgistrationResponse(false, "Error");
        }
    }
}
