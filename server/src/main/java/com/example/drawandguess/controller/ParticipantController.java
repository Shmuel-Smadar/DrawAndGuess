package com.example.drawandguess.controller;

import java.io.FileWriter;
import java.io.IOException;
import static com.example.drawandguess.config.APIConstants.REGISTER_NICKNAME;
import static com.example.drawandguess.config.APIConstants.NICKNAME_TOPIC;
import static com.example.drawandguess.config.GameConstants.NICKNAME_REGEX;
import static com.example.drawandguess.config.GameConstants.invalidNicknameMsg;
import static com.example.drawandguess.config.APIConstants.ERROR_LOG_FILE;
import com.example.drawandguess.model.NicknameResgistrationResponse;
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
    public NicknameResgistrationResponse registerNickname(@Payload RegistrationRequest request, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            String nickname = request.getNickname().trim();
            if (!nickname.matches(NICKNAME_REGEX)) {
                return new NicknameResgistrationResponse(false, invalidNicknameMsg());
            }
            NicknameResgistrationResponse res = participantService.registerParticipant(sessionId, nickname);
            res.setSessionId(sessionId);
            return res;
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in registerNickname: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
            return new NicknameResgistrationResponse(false, "Error");
        }
    }
}
