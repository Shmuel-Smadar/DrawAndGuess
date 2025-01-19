package com.example.drawandguess.controller;

import com.example.drawandguess.model.Participant;
import com.example.drawandguess.service.ParticipantService;
import com.example.drawandguess.service.RoomService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import java.util.Collection;
import java.util.List;

@Controller
public class RoomController {
    private final RoomService roomService;
    private final ParticipantService participantService;
    private final SimpMessagingTemplate messagingTemplate;

    public RoomController(RoomService roomService, ParticipantService participantService, SimpMessagingTemplate messagingTemplate) {
        this.roomService = roomService;
        this.participantService = participantService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/createRoom")
    @SendTo("/topic/rooms")
    public Collection<?> createRoom(@Payload String roomName) {
        roomService.createRoom(roomName);
        return roomService.getAllRooms();
    }

    @MessageMapping("/joinRoom")
    public void joinRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        roomService.joinRoom(sessionId, roomId);
    }

    @MessageMapping("/leaveRoom")
    public void leaveRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        roomService.removeParticipantFromRoom(roomId, participantService.findParticipantBySessionId(sessionId));
    }

    @MessageMapping("/getRooms")
    @SendTo("/topic/rooms")
    public Collection<?> getRooms() {
        return roomService.getAllRooms();
    }

    @MessageMapping("/room/{roomId}/getParticipants")
    public void handleParticipantsRequest(@DestinationVariable String roomId) {
        List<Participant> participants = roomService.getParticipants(roomId);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/participants", participants);
    }
}
