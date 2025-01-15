package com.example.drawandguess.service;

import com.example.drawandguess.model.ChatMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendChatMessage(String roomId, ChatMessage msg) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/chat", msg);
    }
}
