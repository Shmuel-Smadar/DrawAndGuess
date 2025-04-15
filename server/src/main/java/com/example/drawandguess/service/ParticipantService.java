package com.example.drawandguess.service;

import static com.example.drawandguess.config.GameConstants.buildSystemMessage;
import com.example.drawandguess.model.NicknameResgistrationResponse;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.MessageType;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

/*
 * A service that Manages all participants (mapping sessionId ->to the Participant model)
 *  and andles registration and drawer updates (whether a participant is a drawer or not).
 */
@Service
public class ParticipantService {
    private final Map<String, Participant> sessionIdToParticipant = new ConcurrentHashMap<>();

    /*
     * Registers a new participant with the given nickname.
     * If nickname is taken, returns a failure response wit hapropriate message.
     */
    public NicknameResgistrationResponse registerParticipant(String sessionId, String nickname) {
        if (isNicknameTaken(nickname)) {
            return new NicknameResgistrationResponse(false, buildSystemMessage(MessageType.NICKNAME_TAKEN));
        }
        Participant participant = new Participant(sessionId, nickname, false);
        sessionIdToParticipant.put(sessionId, participant);
        return new NicknameResgistrationResponse(true, buildSystemMessage(MessageType.NICKNAME_REGISTERED));
    }

    public void removeParticipant(String sessionId) {
        sessionIdToParticipant.remove(sessionId);
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
        List<Participant> result = new ArrayList<>();
        for (String sid : sessionIds) {
            Participant p = sessionIdToParticipant.get(sid);
            if (p != null) result.add(p);
        }
        return result;
    }

    public Map<String, Participant> getAllParticipants() {
        return new ConcurrentHashMap<>(sessionIdToParticipant);
    }
}
