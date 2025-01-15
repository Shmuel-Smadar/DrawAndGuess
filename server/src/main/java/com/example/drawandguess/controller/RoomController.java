// src/main/java/com/example/drawandguess/controller/RoomController.java
package com.example.drawandguess.controller;

import com.example.drawandguess.model.Room;
import com.example.drawandguess.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import java.util.Collection;

@Controller
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @MessageMapping("/createRoom")
    @SendTo("/topic/rooms")
    public Collection<?> createRoom(@Payload String roomName) {
        roomService.createRoom(roomName);
        return roomService.getRooms();
    }

    @MessageMapping("/joinRoom")
    public void joinRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        roomService.joinRoom(sessionId, roomId);
    }

    @MessageMapping("/leaveRoom")
    public void leaveRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        roomService.leaveRoom(sessionId);
    }

    @MessageMapping("/getRooms")
    @SendTo("/topic/rooms")
    public Collection<?> getRooms() {
        return roomService.getRooms();
    }
}
