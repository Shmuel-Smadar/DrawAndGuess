package com.example.drawandguess.service;

import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.MessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final ParticipantService participantService;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public RoomService(ParticipantService participantService, ChatService chatService, SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.participantService = participantService;
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
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

    public void joinRoom(String sessionId, String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) return;
        room.getGame().addParticipant(sessionId);
        String newDrawerId = room.getGame().getCurrentDrawer();
        if (newDrawerId != null) {
            participantService.setDrawer(newDrawerId, true);
        }
        String nickname = participantService.findParticipantBySessionId(sessionId).getUsername();
        ChatMessage msg = messageService.systemMessage(MessageType.PARTICIPANT_JOINED, nickname);
        chatService.sendChatMessage(roomId, msg);
        broadcastParticipants(roomId);
        broadcastRooms();
    }

    public void broadcastParticipants(String roomId) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Room room = rooms.get(roomId);
                if (room == null) return;
                List<String> ids = room.getGame().getParticipantSessionIds();
                List<Participant> list = participantService.getParticipantsBySessionIds(ids);
                for (Participant p : list) {
                    p.setScore(room.getGame().getScore(p.getSessionId()));
                }
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/participants", list);
            }
        }, 100);
    }

    public void broadcastRooms() {
        List<Map<String, Object>> data = getAllRooms().stream().map(room -> {
            Map<String, Object> map = new ConcurrentHashMap<>();
            map.put("roomName", room.getRoomName());
            map.put("roomId", room.getRoomId());
            map.put("numberOfParticipants", room.getGame().getParticipantSessionIds().size());
            return map;
        }).collect(Collectors.toList());
        messagingTemplate.convertAndSend("/topic/rooms", data);
    }

    public List<Participant> getParticipants(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) return new ArrayList<>();
        List<String> ids = room.getGame().getParticipantSessionIds();
        return participantService.getParticipantsBySessionIds(ids);
    }
}
