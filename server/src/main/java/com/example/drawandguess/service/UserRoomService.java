package com.example.drawandguess.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserRoomService {
    private final Map<String, List<String>> roomToSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToRoom = new ConcurrentHashMap<>();

    public synchronized void assignRoomToSession(String sessionId, String roomId) {
        sessionToRoom.put(sessionId, roomId);
        roomToSessions.computeIfAbsent(roomId, k -> Collections.synchronizedList(new ArrayList<>())).add(sessionId);
    }

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

    public List<String> findSessionsByRoom(String roomId) {
        return roomToSessions.getOrDefault(roomId, Collections.emptyList());
    }

    public String findRoomBySession(String sessionId) {
        return sessionToRoom.get(sessionId);
    }

    public Set<String> getAllSessions() {
        return sessionToRoom.keySet();
    }

    public String getDrawerSession(String roomId) {
        List<String> sessions = roomToSessions.get(roomId);
        if (sessions != null && !sessions.isEmpty()) {
            return sessions.get(0);
        }
        return null;
    }
}