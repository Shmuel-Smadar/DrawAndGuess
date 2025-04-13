package com.example.drawandguess.controller;

import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.service.ChatService;
import com.example.drawandguess.service.GameLogicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import static com.example.drawandguess.config.APIConstants.CHAT_MAPPING;
import static com.example.drawandguess.config.GameConstants.MAX_CHAT_MESSAGE_LENGTH;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;
    private final GameLogicService gameService;

    public ChatController(ChatService chatService, GameLogicService gameService) {
        this.chatService = chatService;
        this.gameService = gameService;
    }

    @MessageMapping(CHAT_MAPPING)
    public void processChatMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        try {
            if (chatMessage.getText() == null || chatMessage.getText().length() > MAX_CHAT_MESSAGE_LENGTH) {
                logger.error("Invalid chat message");
                return;
            }
            chatService.sendChatMessage(roomId, chatMessage);
            gameService.handleGuess(roomId, chatMessage.getText(), chatMessage.getSenderSessionId());
        } catch (Exception e) {
            logger.error("Error processing chat message", e);
        }
    }
}
