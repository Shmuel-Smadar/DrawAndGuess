package com.example.drawandguess.service;

import static com.example.drawandguess.config.APIConstants.TOPIC_ROOMS;
import static com.example.drawandguess.config.APIConstants.topicRoomParticipants;
import static com.example.drawandguess.config.GameConstants.TIMER_DELAY_MS;

import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.MessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final ParticipantService participantService;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final TaskScheduler taskScheduler;

    public RoomService(ParticipantService participantService, ChatService chatService, SimpMessagingTemplate messagingTemplate, MessageService messageService, TaskScheduler taskScheduler) {
        this.participantService = participantService;
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.taskScheduler = taskScheduler;
    }

    public Room createRoom(String roomName) {
        Room room = new Room(roomName);
        rooms.put(room.getRoomId(), room);
        broadcastRooms();
        return room;
    }

    public boolean deleteRoom(String roomId) {
        boolean removed = rooms.remove(roomId) != null;
        if (removed) {
            broadcastRooms();
        }
        return removed;
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    // A method that adds participant to a room
    public void joinRoom(String sessionId, String roomId) {
        Room room = getRoom(roomId);
        Game game = room.getGame();
        game.addParticipant(sessionId);
        // If a game is over we will only update room and participants lists, without choosing new drawer
        if (game.isGameOver()) {
            broadcastParticipants(roomId);
            broadcastRooms();
            return;
        }
        String newDrawerId = game.getCurrentDrawer();
        if (newDrawerId != null) {
            participantService.setDrawer(newDrawerId, true);
        }
        String nickname = participantService.findParticipantBySessionId(sessionId).getUsername();
        scheduleParticipantJoinedMessage(roomId, nickname);
        broadcastParticipants(roomId);
        broadcastRooms();
    }

    /* A method that sends a message about user joining a room.
    * with slight delay so that the joined user will see it as well. */
    private void scheduleParticipantJoinedMessage(String roomId, String nickname) {
        taskScheduler.schedule(() -> {
            ChatMessage msg = messageService.systemMessage(MessageType.PARTICIPANT_JOINED, nickname);
            chatService.sendChatMessage(roomId, msg);
        }, Instant.now().plusMillis(TIMER_DELAY_MS));
    }

    // A method that update the list of participants to a given room
    public void broadcastParticipants(String roomId) {
        taskScheduler.schedule(() -> {
            Room room = rooms.get(roomId);
            if (room == null) return;
            List<String> ids = room.getGame().getParticipantSessionIds();
            List<Participant> list = participantService.getParticipantsBySessionIds(ids);
            for (Participant p : list) {
                p.setScore(room.getGame().getScore(p.getUsername()));
            }
            messagingTemplate.convertAndSend(topicRoomParticipants(roomId), list);
        }, Instant.now().plusMillis(TIMER_DELAY_MS));
    }

    // A method that update the list of rooms
    public void broadcastRooms() {
        List<Map<String, Object>> data = getAllRooms().stream().map(room -> {
            Map<String, Object> map = new ConcurrentHashMap<>();
            map.put("roomName", room.getRoomName());
            map.put("roomId", room.getRoomId());
            map.put("numberOfParticipants", room.getGame().getParticipantSessionIds().size());
            return map;
        }).collect(Collectors.toList());
        messagingTemplate.convertAndSend(TOPIC_ROOMS, data);
    }
}
