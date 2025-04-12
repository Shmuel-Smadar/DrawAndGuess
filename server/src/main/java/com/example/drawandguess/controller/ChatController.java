package com.example.drawandguess.controller;

import java.io.FileWriter;
import java.io.IOException;
import static com.example.drawandguess.config.APIConstants.CHAT_MAPPING;
import static com.example.drawandguess.config.APIConstants.ERROR_LOG_FILE;
import static com.example.drawandguess.config.GameConstants.MAX_CHAT_MESSAGE_LENGTH;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.service.ChatService;
import com.example.drawandguess.service.GameLogicService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final ChatService chatService;
    private final GameLogicService gameService;

    public ChatController(ChatService chatService, GameLogicService gameService) {
        this.gameService = gameService;
        this.chatService = chatService;
    }

    @MessageMapping(CHAT_MAPPING)
    public void processChatMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        try {
            if (chatMessage.getText() == null || chatMessage.getText().length() > MAX_CHAT_MESSAGE_LENGTH) {
                try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                    w.write("Invalid chat message from session " + chatMessage.getSenderSessionId() + "\n");
                } catch (IOException ignored) {
                }
                return;
            }
            chatService.sendChatMessage(roomId, chatMessage);
            gameService.handleGuess(roomId, chatMessage.getText(), chatMessage.getSenderSessionId());
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error processing chat message: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
        }
    }
}
