package com.example.drawandguess.handler;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ExampleWebSocketHandler extends TextWebSocketHandler {

    // store all active WebSocket sessions
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("New WebSocket connection established. Session ID: " + session.getId());

        session.sendMessage(new TextMessage("Welcome! Connected to the Draw and Guess server."));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received message from session " + session.getId() + ": " + payload);

        // broadcast the received message to all connected clients
        broadcastMessage(payload, session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Transport error in session " + session.getId() + ": " + exception.getMessage());
        sessions.remove(session);
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("WebSocket connection closed. Session ID: " + session.getId());
    }

    private void broadcastMessage(String message, WebSocketSession senderSession) {
        synchronized (sessions) {
            for (WebSocketSession session : sessions) {
                if (!session.equals(senderSession) && session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (Exception e) {
                        System.err.println("Error sending message to session " + session.getId() + ": " + e.getMessage());
                    }
                }
            }
        }
    }
}
