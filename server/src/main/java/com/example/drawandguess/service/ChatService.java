package com.example.drawandguess.service;

import com.example.drawandguess.config.Constants;
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
        messagingTemplate.convertAndSend(Constants.topicRoomChat(roomId), msg);
    }

    public void sendWordHint(String sessionId, String roomId, String hint) {
        messagingTemplate.convertAndSend(Constants.topicRoomWordHint(roomId), hint);
    }
}
