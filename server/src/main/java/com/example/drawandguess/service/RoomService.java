// src/main/java/com/example/drawandguess/service/RoomService.java
package com.example.drawandguess.service;

import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Game;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RoomService {
    private final GameService gameService;
    private final DrawingService drawingService;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NicknameRegistration nicknameRegistration;

    public RoomService(GameService gameService, DrawingService drawingService, ChatService chatService, SimpMessagingTemplate messagingTemplate, NicknameRegistration nicknameRegistration) {
        this.gameService = gameService;
        this.drawingService = drawingService;
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.nicknameRegistration = nicknameRegistration;
    }

    public void createRoom(String roomName) {
        gameService.createRoom(roomName);
    }

    public Collection<?> getRooms() {
        return gameService.getRooms();
    }

    public void joinRoom(String sessionId, String roomId) {
        gameService.joinRoom(sessionId, roomId);
    }

    public void leaveRoom(String sessionId) {
        gameService.leaveRoom(sessionId);
    }

    public void broadcastParticipants(String roomId) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Game game = gameService.getGames().get(roomId);
                if (game != null) {
                    messagingTemplate.convertAndSend("/topic/room/" + roomId + "/participants", game.getParticipants());
                }
            }
        }, 100);
    }

    public void handleSubscription(String sessionId, String roomId) {
        joinRoom(sessionId, roomId);
        String nickname = nicknameRegistration.findNickname(sessionId);
        ChatMessage m = new ChatMessage();
        m.setSender("system");
        m.setText(nickname + " has joined the room.");
        m.setType("system");
        chatService.sendChatMessage(roomId, m);
        broadcastParticipants(roomId);
    }

    public void handleDisconnect(String sessionId) {
        String nickname = nicknameRegistration.findNickname(sessionId);
        Game target = null;
        String roomId = null;
        for (Game g : gameService.getGames().values()) {
            if (g.hasSession(sessionId)) {
                target = g;
                roomId = g.getRoomId();
                break;
            }
        }
        if (target != null && roomId != null) {
            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setSender("system");
            leaveMessage.setText(nickname + " has left the room.");
            leaveMessage.setType("system");
            chatService.sendChatMessage(roomId, leaveMessage);
            target.leave(sessionId);
            nicknameRegistration.removeNickname(sessionId);
            if (target.isDrawer(sessionId)) {
                ChatMessage m = new ChatMessage();
                m.setSender("system");
                m.setText("The drawer has left the room. Starting a new round.");
                m.setType("system");
                chatService.sendChatMessage(roomId, m);
                target.nextRound();
                broadcastParticipants(roomId);
            } else {
                broadcastParticipants(roomId);
            }
        }
    }
}
