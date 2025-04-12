package com.example.drawandguess.controller;

import static com.example.drawandguess.config.APIConstants.REQUEST_WORDS_MAPPING;
import static com.example.drawandguess.config.APIConstants.WORD_OPTIONS_TOPIC;
import static com.example.drawandguess.config.APIConstants.ERROR_LOG_FILE;
import static com.example.drawandguess.config.APIConstants.CHOOSE_WORD_MAPPING;
import static com.example.drawandguess.config.APIConstants.CORRECT_GUESS_MAPPING;
import static com.example.drawandguess.config.APIConstants.CURRENT_HINT_MAPPING;
import static com.example.drawandguess.config.APIConstants.TOPIC_ROOM_PREFIX;
import static com.example.drawandguess.config.APIConstants.WORD_HINT_ENDPOINT;
import java.io.FileWriter; import java.io.IOException;
import com.example.drawandguess.model.WordOptions;
import com.example.drawandguess.service.GameLogicService;
import com.example.drawandguess.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller public class GameLogicController {
    private final GameLogicService gameLogicService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;


    public GameLogicController(GameLogicService gameLogicService, RoomService roomService, SimpMessagingTemplate messagingTemplate) {
        this.gameLogicService = gameLogicService;
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping(REQUEST_WORDS_MAPPING)
    @SendToUser(WORD_OPTIONS_TOPIC)
    public WordOptions handleWordRequest(@DestinationVariable String roomId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            return gameLogicService.requestWords(roomId, sessionId);
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in handleWordRequest: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
            return new WordOptions();
        }
    }

    @MessageMapping(CHOOSE_WORD_MAPPING)
    public void handleWordChosen(@DestinationVariable String roomId, @Payload String chosenWord, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            gameLogicService.chooseWord(roomId, sessionId, chosenWord);
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in handleWordChosen: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
        }
    }

    @MessageMapping(CORRECT_GUESS_MAPPING)
    public void handleCorrectGuess(@DestinationVariable String roomId, @Payload String guess, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            gameLogicService.handleGuess(roomId, guess, sessionId);
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in handleCorrectGuess: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
        }
    }

    @MessageMapping(CURRENT_HINT_MAPPING)
    public void retrieveCurrentHint(@DestinationVariable String roomId) {
        try {
            String currentHint = roomService.getRoom(roomId).getGame().getCurrentHint();
            messagingTemplate.convertAndSend(TOPIC_ROOM_PREFIX + roomId + WORD_HINT_ENDPOINT, currentHint);
        } catch (Exception e) {
            try (FileWriter w = new FileWriter(ERROR_LOG_FILE, true)) {
                w.write("Error in retrieveCurrentHint: " + e.getMessage() + "\n");
            } catch (IOException ignored) {
            }
        }
    }
}