package com.example.drawandguess.service;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.Room;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class RoomService {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final ParticipantService participantService;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public RoomService(ParticipantService participantService, ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.participantService = participantService;
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    public Room createRoom(String roomName) {
        Room room = new Room(roomName);
        rooms.put(room.getRoomId(), room);
        return room;
    }
    public boolean deleteRoom(String roomId) {
        return rooms.remove(roomId) != null;
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
        if(newDrawerId != null) {
            participantService.setDrawer(newDrawerId, true);
        }
        String nickname = participantService.findParticipantBySessionId(sessionId).getUsername();
        ChatMessage msg = new ChatMessage();
        msg.setSenderSessionId("system");
        msg.setText(nickname + " has joined the room.");
        msg.setType("system");
        chatService.sendChatMessage(roomId, msg);
        broadcastParticipants(roomId);
    }
    public void removeParticipantFromRoom(String roomId, Participant participant) {
        Room room = rooms.get(roomId);
        if (room == null) return;
        room.getGame().removeParticipant(participant.getSessionId());
        ChatMessage leaveMsg = new ChatMessage();
        leaveMsg.setSenderSessionId("system");
        leaveMsg.setText(participant.getUsername() + " has left the room.");
        leaveMsg.setType("system");
        chatService.sendChatMessage(roomId, leaveMsg);
        String newDrawerId = room.getGame().getCurrentDrawer();
        participantService.getAllParticipants().values().forEach(p ->
            participantService.setDrawer(
                    p.getSessionId(),
                    p.getSessionId().equals(newDrawerId)
                )      
             );
        broadcastParticipants(roomId);
    }

    public void removeParticipantFromAllRooms(Participant participant) {
        String sessionId = participant.getSessionId();
        for (Room room : rooms.values()) {
            if (room.getGame().getParticipantSessionIds().contains(sessionId)) {
                removeParticipantFromRoom(room.getRoomId(), participantService.findParticipantBySessionId(sessionId));
                broadcastParticipants(room.getRoomId());
            }
        }
    }

    public void broadcastParticipants(String roomId) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Room room = rooms.get(roomId);
                if (room == null) return;
                List<String> participantIds = room.getGame().getParticipantSessionIds();
                List<Participant> participantList = participantService.getParticipantsBySessionIds(participantIds);
                for (Participant p : participantList) {
                    p.setScore(room.getGame().getScore(p.getSessionId()));
                }
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/participants", participantList);
            }
        }, 100);
    }

    public List<Participant> getParticipants(String roomId) {
        return participantService.getParticipantsBySessionIds(rooms.get(roomId).getGame().getParticipantSessionIds());
    }
}