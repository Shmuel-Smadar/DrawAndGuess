package com.example.drawandguess.service;

import static com.example.drawandguess.config.GameConstants.NICKNAME_TAKEN_MSG;
import static com.example.drawandguess.config.GameConstants.NICKNAME_REGISTERED_MSG;
import static com.example.drawandguess.config.GameConstants.REMOVED_PARTICIPANT_MSG_PREFIX;

import org.springframework.stereotype.Service;
import com.example.drawandguess.model.NicknameResgistrationResponse;
import com.example.drawandguess.model.Participant;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ParticipantService {
    private final Map<String, Participant> sessionIdToParticipant = new ConcurrentHashMap<>();

    public NicknameResgistrationResponse registerParticipant(String sessionId, String nickname) {
        if (isNicknameTaken(nickname)) {
            return new NicknameResgistrationResponse(false, NICKNAME_TAKEN_MSG);
        }
        Participant participant = new Participant(sessionId, nickname, false);
        sessionIdToParticipant.put(sessionId, participant);
        return new NicknameResgistrationResponse(true, NICKNAME_REGISTERED_MSG);
    }

    public void removeParticipant(String sessionId) {
        Participant removed = sessionIdToParticipant.remove(sessionId);
        if (removed != null) {
            System.out.println(REMOVED_PARTICIPANT_MSG_PREFIX + removed.getUsername());
        }
    }

    public Participant findParticipantBySessionId(String sessionId) {
        return sessionIdToParticipant.get(sessionId);
    }

    public Optional<Participant> findParticipantByNickname(String nickname) {
        return sessionIdToParticipant.values().stream()
                .filter(p -> p.getUsername().equals(nickname))
                .findFirst();
    }

    public boolean isNicknameTaken(String nickname) {
        return sessionIdToParticipant.values().stream()
                .anyMatch(p -> p.getUsername().equals(nickname));
    }

    public void setDrawer(String sessionId, boolean isDrawer) {
        Participant participant = sessionIdToParticipant.get(sessionId);
        if (participant != null) {
            participant.setDrawer(isDrawer);
        }
    }

    public List<Participant> getParticipantsBySessionIds(List<String> sessionIds) {
        return sessionIds.stream()
                .map(sessionIdToParticipant::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Map<String, Participant> getAllParticipants() {
        return new ConcurrentHashMap<>(sessionIdToParticipant);
    }
}
