package com.example.drawandguess.controller;

import com.example.drawandguess.config.Constants;
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

    @MessageMapping(Constants.CHAT_MAPPING)
    public void processChatMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        if (chatMessage.getText() == null || chatMessage.getText().length() > Constants.MAX_CHAT_MESSAGE_LENGTH) {
            return;
        }
        chatService.sendChatMessage(roomId, chatMessage);
        gameService.handleGuess(roomId, chatMessage.getText(), chatMessage.getSenderSessionId());
    }
}
