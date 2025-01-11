package com.example.drawandguess.listener;

import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.service.NicknameRegistration;
import com.example.drawandguess.service.UserRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final NicknameRegistration nicknameRegistration;
    private final UserRoomService userRoomService;

    @Autowired
    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate,
                                  NicknameRegistration nicknameRegistration,
                                  UserRoomService userRoomService) {
        this.messagingTemplate = messagingTemplate;
        this.nicknameRegistration = nicknameRegistration;
        this.userRoomService = userRoomService;
    }

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/topic/room/") && destination.endsWith("/chat")) {
            String[] parts = destination.split("/");
            if (parts.length >= 5) {
                String roomId = parts[3];
                String sessionId = accessor.getSessionId();
                String nickname = nicknameRegistration.findNickname(sessionId);

                if (nickname != null) {
                    // Assign the room to the session
                    userRoomService.assignRoomToSession(sessionId, roomId);

                    // Send join message
                    ChatMessage joinMessage = new ChatMessage();
                    joinMessage.setSender("system");
                    joinMessage.setText(nickname + " has joined the room.");
                    joinMessage.setType("system");

                    messagingTemplate.convertAndSend("/topic/room/" + roomId + "/chat", joinMessage);

                    // Update and broadcast participant list
                    broadcastParticipantList(roomId);
                }
            }
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String nickname = nicknameRegistration.findNickname(sessionId);
        String roomId = userRoomService.findRoomBySession(sessionId);

        if (nickname != null && roomId != null) {
            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setSender("system");
            leaveMessage.setText(nickname + " has left the room.");
            leaveMessage.setType("system");

            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/chat", leaveMessage);

            // Remove user from room
            userRoomService.removeSessionFromRoom(sessionId);
            nicknameRegistration.removeNickname(sessionId);

            // Update and broadcast participant list
            broadcastParticipantList(roomId);
        }
    }

    private void broadcastParticipantList(String roomId) {
        Set<String> sessionIds = userRoomService.findSessionsByRoom(roomId);
        List<Participant> participants = sessionIds.stream()
                .map(sessionId -> {
                    String username = nicknameRegistration.findNickname(sessionId);
                    // Assuming you have a way to determine if a user is a drawer
                    boolean isDrawer = false; // Implement logic to check if user is a drawer
                    return new Participant(sessionId, username, isDrawer);
                })
                .collect(Collectors.toList());

        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/participants", participants);
    }
}
