package com.example.drawandguess.service;

import com.example.drawandguess.model.Game;
import org.springframework.stereotype.Service;

@Service
public class RoundService {
    private final ParticipantService participantService;
    private final RoomService roomService;

    public RoundService(ChatService chatService,
                        ParticipantService participantService,
                        RoomService roomService) {
        this.participantService = participantService;
        this.roomService = roomService;
    }



    public void updateDrawerAndBroadcast(String roomId, Game game) {
        String currentDrawer = game.getCurrentDrawer();
        participantService.getAllParticipants().values().forEach(
                p -> participantService.setDrawer(p.getSessionId(), p.getSessionId().equals(currentDrawer))
        );
        roomService.broadcastParticipants(roomId);
    }
}
