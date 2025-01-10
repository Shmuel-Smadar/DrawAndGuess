package com.example.drawandguess.controller;

import com.example.drawandguess.model.Room;
import com.example.drawandguess.service.RoomRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Collection;

@Controller
public class RoomController {

    @Autowired
    private RoomRegistration roomRegistration;

    @MessageMapping("/createRoom")
    @SendTo("/topic/rooms")
    public Collection<Room> createRoom(@Payload String roomName) {
        roomRegistration.createRoom(roomName);
        return roomRegistration.getAllRooms();
    }

    @MessageMapping("/getRooms")
    @SendTo("/topic/rooms")
    public Collection<Room> getRooms() {
        return roomRegistration.getAllRooms();
    }
}
