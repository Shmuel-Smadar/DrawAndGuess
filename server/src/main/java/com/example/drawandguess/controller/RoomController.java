package com.example.drawandguess.controller;

import com.example.drawandguess.config.Constants;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.service.RoomService;
import com.example.drawandguess.service.GameService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
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
    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;

    public RoomController(RoomService roomService, SimpMessagingTemplate messagingTemplate, GameService gameService) {
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    @MessageMapping(Constants.CREATE_ROOM)
    @SendToUser(Constants.ROOM_CREATED_TOPIC)
    public Room createRoom(@Payload String roomName, SimpMessageHeaderAccessor headerAccessor) {
        if (roomName == null || roomName.length() > Constants.MAX_ROOM_NAME_LENGTH) {
            return null;
        }
        Room room = roomService.createRoom(roomName);
        String sessionId = headerAccessor.getSessionId();
        roomService.joinRoom(sessionId, room.getRoomId());
        return room;
    }

    @MessageMapping(Constants.JOIN_ROOM)
    public void joinRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        roomService.joinRoom(sessionId, roomId);
    }

    @MessageMapping(Constants.LEAVE_ROOM)
    public void leaveRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        gameService.userLeftRoom(roomId, sessionId);
    }

    @MessageMapping(Constants.GET_ROOMS)
    @SendTo(Constants.TOPIC_ROOMS)
    public Collection<?> getRooms() {
        return roomService.getAllRooms().stream().map(room -> {
            Map<String, Object> roomInfo = new HashMap<>();
            roomInfo.put(Constants.ROOM_NAME_KEY, room.getRoomName());
            roomInfo.put(Constants.ROOM_ID_KEY, room.getRoomId());
            roomInfo.put(Constants.NUMBER_OF_PARTICIPANTS_KEY, room.getGame().getParticipantSessionIds().size());
            return roomInfo;
        }).collect(Collectors.toList());
    }

    @MessageMapping(Constants.PARTICIPANTS_MAPPING)
    public void handleParticipantsRequest(@DestinationVariable String roomId) {
        List<Participant> participants = roomService.getParticipants(roomId);
        messagingTemplate.convertAndSend(Constants.TOPIC_ROOM_PREFIX + roomId + Constants.PARTICIPANTS_ENDPOINT, participants);
    }
}
