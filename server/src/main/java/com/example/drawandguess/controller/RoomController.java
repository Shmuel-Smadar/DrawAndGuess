package com.example.drawandguess.controller;

import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.service.RoomService;
import com.example.drawandguess.service.GameLogicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.drawandguess.config.APIConstants.CREATE_ROOM;
import static com.example.drawandguess.config.APIConstants.ROOM_CREATED_TOPIC;
import static com.example.drawandguess.config.APIConstants.JOIN_ROOM;
import static com.example.drawandguess.config.APIConstants.LEAVE_ROOM;
import static com.example.drawandguess.config.APIConstants.GET_ROOMS;
import static com.example.drawandguess.config.APIConstants.TOPIC_ROOMS;
import static com.example.drawandguess.config.APIConstants.PARTICIPANTS_MAPPING;
import static com.example.drawandguess.config.GameConstants.ROOM_NAME_KEY;
import static com.example.drawandguess.config.GameConstants.ROOM_ID_KEY;
import static com.example.drawandguess.config.GameConstants.NUMBER_OF_PARTICIPANTS_KEY;
import static com.example.drawandguess.config.GameConstants.MAX_ROOM_NAME_LENGTH;

@Controller
public class RoomController {
    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);
    private final RoomService roomService;
    private final GameLogicService gameService;

    public RoomController(RoomService roomService, GameLogicService gameService) {
        this.roomService = roomService;
        this.gameService = gameService;
    }

    // A method responsible for creating a room
    @MessageMapping(CREATE_ROOM)
    @SendToUser(ROOM_CREATED_TOPIC)
    public Room createRoom(@Payload String roomName, SimpMessageHeaderAccessor headerAccessor) {
        try {
            if (roomName == null || roomName.length() > MAX_ROOM_NAME_LENGTH) {
                return null;
            }
            Room room = roomService.createRoom(roomName);
            String sessionId = headerAccessor.getSessionId();
            roomService.joinRoom(sessionId, room.getRoomId());
            return room;
        } catch (Exception e) {
            logger.error("Error in createRoom", e);
            return null;
        }
    }

    // A method responsible for handling a user request to join a room
    @MessageMapping(JOIN_ROOM)
    public void joinRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            roomService.joinRoom(sessionId, roomId);
        } catch (Exception e) {
            logger.error("Error in joinRoom", e);
        }
    }
    // A method responsible for handling a user request to leave a room
    @MessageMapping(LEAVE_ROOM)
    public void leaveRoom(@Payload String roomId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            gameService.userLeftRoom(roomId, sessionId);
        } catch (Exception e) {
            logger.error("Error in leaveRoom", e);
        }
    }

    // A method responsible for updating the room list in the lobby (and number of players in each room)
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
            logger.error("Error in getRooms", e);
            return null;
        }
    }

    // A method responsible for updating the participant list in a given room
    @MessageMapping(PARTICIPANTS_MAPPING)
    public void handleParticipantsRequest(@DestinationVariable String roomId) {
        try {
            roomService.broadcastParticipants(roomId);
        } catch (Exception e) {
            logger.error("Error in handleParticipantsRequest", e);
        }
    }
}
