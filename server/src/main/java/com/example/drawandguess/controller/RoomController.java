package com.example.drawandguess.controller;

import java.io.FileWriter;
import java.io.IOException;
import static com.example.drawandguess.config.APIConstants.CREATE_ROOM;
import static com.example.drawandguess.config.APIConstants.ROOM_CREATED_TOPIC;
import static com.example.drawandguess.config.APIConstants.JOIN_ROOM;
import static com.example.drawandguess.config.APIConstants.LEAVE_ROOM;
import static com.example.drawandguess.config.APIConstants.GET_ROOMS;
import static com.example.drawandguess.config.APIConstants.TOPIC_ROOMS;
import static com.example.drawandguess.config.APIConstants.PARTICIPANTS_MAPPING;
import static com.example.drawandguess.config.APIConstants.PARTICIPANTS_ENDPOINT;
import static com.example.drawandguess.config.APIConstants.TOPIC_ROOM_PREFIX;
import static com.example.drawandguess.config.GameConstants.ROOM_NAME_KEY;
import static com.example.drawandguess.config.GameConstants.ROOM_ID_KEY;
import static com.example.drawandguess.config.GameConstants.NUMBER_OF_PARTICIPANTS_KEY;
import static com.example.drawandguess.config.APIConstants.ERROR_LOG_FILE;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.service.RoomService;
import com.example.drawandguess.service.GameLogicService;
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
    private final GameLogicService gameService;

    public RoomController(RoomService roomService, SimpMessagingTemplate messagingTemplate, GameLogicService gameService) {
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    @MessageMapping(CREATE_ROOM)
    @SendToUser(ROOM_CREATED_TOPIC)
    public Room createRoom(@Payload String roomName, SimpMessageHeaderAccessor headerAccessor) {
        try {
            if (roomName == null || roomName.length() > com.example.drawandguess.config.GameConstants.MAX_ROOM_NAME_LENGTH) {
                return null;
            }
            Room room = roomService.createRoom(roomName);
            String sessionId = headerAccessor.getSessionId();
            roomService.joinRoom(sessionId, room.getRoomId());
            return room;
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in createRoom: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
            return null;
        }
    }

    @MessageMapping(JOIN_ROOM)
    public void joinRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            roomService.joinRoom(sessionId, roomId);
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in joinRoom: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
        }
    }

    @MessageMapping(LEAVE_ROOM)
    public void leaveRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            gameService.userLeftRoom(roomId, sessionId);
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in leaveRoom: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
        }
    }

    @MessageMapping(GET_ROOMS)
    @SendTo(TOPIC_ROOMS)
    public Collection<?> getRooms() {
        try {
            return roomService.getAllRooms().stream().map(room -> {
                Map<String, Object> roomInfo = new HashMap<>();
                roomInfo.put(ROOM_NAME_KEY, room.getRoomName());
                roomInfo.put(ROOM_ID_KEY, room.getRoomId());
                roomInfo.put(NUMBER_OF_PARTICIPANTS_KEY, room.getGame().getParticipantSessionIds().size());
                return roomInfo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in getRooms: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
            return null;
        }
    }

    @MessageMapping(PARTICIPANTS_MAPPING)
    public void handleParticipantsRequest(@DestinationVariable String roomId) {
        try {
            List<Participant> participants = roomService.getParticipants(roomId);
            messagingTemplate.convertAndSend(
                TOPIC_ROOM_PREFIX + roomId + PARTICIPANTS_ENDPOINT,
                participants
            );
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in handleParticipantsRequest: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
        }
    }
}
