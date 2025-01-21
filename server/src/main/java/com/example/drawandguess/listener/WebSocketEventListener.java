package com.example.drawandguess.listener;

import com.example.drawandguess.model.Participant;
import com.example.drawandguess.service.ParticipantService;
import com.example.drawandguess.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private final RoomService roomService;
    private final ParticipantService participantService;

    @Autowired
    public WebSocketEventListener(RoomService roomService, ParticipantService participantService) {
        this.roomService = roomService;
        this.participantService = participantService;
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        Participant participant = participantService.findParticipantBySessionId(sessionId);
        if (participant != null) {
            roomService.removeParticipantFromAllRooms(participant);
            participantService.removeParticipant(sessionId);
        }
    }
}