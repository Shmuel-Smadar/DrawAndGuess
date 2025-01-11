package com.example.drawandguess.controller;

import com.example.drawandguess.model.Room;
import com.example.drawandguess.service.RoomRegistration;
import com.example.drawandguess.service.UserRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.Collection;

@Controller
public class RoomController {

    private final RoomRegistration roomRegistration;
    private final UserRoomService userRoomService;


    @Autowired
    public RoomController(RoomRegistration roomRegistration, UserRoomService userRoomService) {
        this.roomRegistration = roomRegistration;
        this.userRoomService = userRoomService;
    }

    @MessageMapping("/createRoom")
    @SendTo("/topic/rooms")
    public Collection<Room> createRoom(@Payload String roomName) {
        roomRegistration.createRoom(roomName);
        return roomRegistration.getAllRooms();
    }

    @MessageMapping("/joinRoom")
    public void joinRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        userRoomService.assignRoomToSession(sessionId, roomId);
    }

    @MessageMapping("/leaveRoom")
    public void leaveRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        userRoomService.removeSessionFromRoom(sessionId);
    }

    @MessageMapping("/getRooms")
    @SendTo("/topic/rooms")
    public Collection<Room> getRooms() {
        return roomRegistration.getAllRooms();
    }
}
