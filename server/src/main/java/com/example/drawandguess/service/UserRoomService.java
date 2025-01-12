package com.example.drawandguess.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

@Service
public class UserRoomService {
    // Maps room IDs to a set of session IDs
    private final Map<String, Set<String>> roomToSessions = new ConcurrentHashMap<>();

    // Maps session IDs to room IDs
    private final Map<String, String> sessionToRoom = new ConcurrentHashMap<>();

    // Assign a session to a room
    public void assignRoomToSession(String sessionId, String roomId) {
        sessionToRoom.put(sessionId, roomId);
        roomToSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    // Remove a session from a room
    public void removeSessionFromRoom(String sessionId) {
        String roomId = sessionToRoom.remove(sessionId);
        if (roomId != null) {
            Set<String> sessions = roomToSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    roomToSessions.remove(roomId);
                }
            }
        }
    }

    // Get all session IDs in a room
    public Set<String> findSessionsByRoom(String roomId) {
        return roomToSessions.getOrDefault(roomId, Collections.emptySet());
    }

    // Get the room ID for a session
    public String findRoomBySession(String sessionId) {
        return sessionToRoom.get(sessionId);
    }

    // Get all session IDs
    public Set<String> getAllSessions() {
        return sessionToRoom.keySet();
    }
}
