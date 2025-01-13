package com.example.drawandguess.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserRoomService {
    // Maps room IDs to a list of session IDs (maintains order)
    private final Map<String, List<String>> roomToSessions = new ConcurrentHashMap<>();

    // Maps session IDs to room IDs
    private final Map<String, String> sessionToRoom = new ConcurrentHashMap<>();

    // Assign a session to a room
    public synchronized void assignRoomToSession(String sessionId, String roomId) {
        sessionToRoom.put(sessionId, roomId);
        roomToSessions.computeIfAbsent(roomId, k -> Collections.synchronizedList(new ArrayList<>())).add(sessionId);
    }

    // Remove a session from a room
    public synchronized void removeSessionFromRoom(String sessionId) {
        String roomId = sessionToRoom.remove(sessionId);
        if (roomId != null) {
            List<String> sessions = roomToSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    roomToSessions.remove(roomId);
                }
            }
        }
    }

    // Get all session IDs in a room
    public List<String> findSessionsByRoom(String roomId) {
        return roomToSessions.getOrDefault(roomId, Collections.emptyList());
    }

    // Get the room ID for a session
    public String findRoomBySession(String sessionId) {
        return sessionToRoom.get(sessionId);
    }

    // Get all session IDs
    public Set<String> getAllSessions() {
        return sessionToRoom.keySet();
    }

    // Get the first session (drawer) in a room
    public String getDrawerSession(String roomId) {
        List<String> sessions = roomToSessions.get(roomId);
        if (sessions != null && !sessions.isEmpty()) {
            return sessions.get(0);
        }
        return null;
    }
}