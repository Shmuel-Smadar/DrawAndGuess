package com.example.drawandguess.service;
import com.example.drawandguess.model.Room;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Collection;

@Service
public class RoomRegistration {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public void createRoom(String roomName) {
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId, roomName);
        rooms.put(roomId, room);
    }

    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    public Room getRoomById(String roomId) {
        return rooms.get(roomId);
    }

    public void removeRoom(String roomId) {
        rooms.remove(roomId);
    }
}
