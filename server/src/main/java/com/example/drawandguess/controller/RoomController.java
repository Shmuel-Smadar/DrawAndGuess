package com.example.drawandguess.controller;

import com.example.drawandguess.model.Participant;
import com.example.drawandguess.service.ParticipantService;
import com.example.drawandguess.service.RoomService;
import com.example.drawandguess.service.GameService;
import com.example.drawandguess.model.Room;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RoomController {
    private final RoomService roomService;
    private final ParticipantService participantService;
    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;

    public RoomController(
            RoomService roomService,
            ParticipantService participantService,
            SimpMessagingTemplate messagingTemplate,
            GameService gameService
    ) {
        this.roomService = roomService;
        this.participantService = participantService;
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    @MessageMapping("/createRoom")
    public void createRoom(@Payload String roomName) {
        roomService.createRoom(roomName);
    }

    @MessageMapping("/joinRoom")
    public void joinRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        roomService.joinRoom(sessionId, roomId);
    }

    @MessageMapping("/leaveRoom")
    public void leaveRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Participant p = participantService.findParticipantBySessionId(sessionId);
        if (p == null) return;
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        if (room.getGame().isDrawer(sessionId)) {
            gameService.stopHintProgression(roomId);
        }
        roomService.removeParticipantFromRoom(roomId, p);
    }

    @MessageMapping("/getRooms")
    @SendTo("/topic/rooms")
    public Collection<?> getRooms() {
        return roomService.getAllRooms().stream().map(room -> {
            Map<String, Object> roomInfo = new HashMap<>();
            roomInfo.put("roomName", room.getRoomName());
            roomInfo.put("roomId", room.getRoomId());
            roomInfo.put("numberOfParticipants", room.getGame().getParticipantSessionIds().size());
            return roomInfo;
        }).collect(Collectors.toList());
    }

    @MessageMapping("/room/{roomId}/getParticipants")
    public void handleParticipantsRequest(@DestinationVariable String roomId) {
        List<Participant> participants = roomService.getParticipants(roomId);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/participants", participants);
    }
}
