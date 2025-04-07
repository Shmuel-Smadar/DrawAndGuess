package com.example.drawandguess.controller;

import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.service.ChatService;
import com.example.drawandguess.service.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final ChatService chatService;
    private final GameService gameService;

    public ChatController(ChatService chatService, GameService gameService) {
        this.gameService = gameService;
        this.chatService = chatService;
    }
    @MessageMapping("/room/{roomId}/chat")
    public void processChatMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        chatService.sendChatMessage(roomId, chatMessage);
        gameService.handleGuess(roomId, chatMessage.getText(), chatMessage.getSenderSessionId());
    }
}
