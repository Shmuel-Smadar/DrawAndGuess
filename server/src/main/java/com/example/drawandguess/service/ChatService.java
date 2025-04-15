package com.example.drawandguess.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static com.example.drawandguess.config.APIConstants.topicRoomChat;
import static com.example.drawandguess.config.APIConstants.topicRoomWordHint;

import com.example.drawandguess.model.ChatMessage;

/*
 * Service for sending chat messages and word hint updates
 * to subscribed clients.
 */
@Service
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /*
     * Publishes a chat message to the room’s chat topic.
     */
    public void sendChatMessage(String roomId, ChatMessage msg) {
        messagingTemplate.convertAndSend(topicRoomChat(roomId), msg);
    }

    /*
     * Publishes the current word hint to the room’s hint topic.
     */
    public void sendWordHint(String roomId, String hint) {
        messagingTemplate.convertAndSend(topicRoomWordHint(roomId), hint);
    }
}
