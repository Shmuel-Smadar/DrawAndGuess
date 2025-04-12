package com.example.drawandguess.controller;

import static com.example.drawandguess.config.APIConstants.REQUEST_WORDS_MAPPING;
import static com.example.drawandguess.config.APIConstants.WORD_OPTIONS_TOPIC;
import static com.example.drawandguess.config.APIConstants.CHOOSE_WORD_MAPPING;
import static com.example.drawandguess.config.APIConstants.CORRECT_GUESS_MAPPING;
import static com.example.drawandguess.config.APIConstants.CURRENT_HINT_MAPPING;
import static com.example.drawandguess.config.APIConstants.TOPIC_ROOM_PREFIX;
import static com.example.drawandguess.config.APIConstants.WORD_HINT_ENDPOINT;

import com.example.drawandguess.model.WordOptions;
import com.example.drawandguess.service.GameService;
import com.example.drawandguess.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller
public class GameLogicController {
    private final GameService gameService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameLogicController(GameService gameService, RoomService roomService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping(REQUEST_WORDS_MAPPING)
    @SendToUser(WORD_OPTIONS_TOPIC)
    public WordOptions handleWordRequest(@DestinationVariable String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        return gameService.requestWords(roomId, sessionId);
    }

    @MessageMapping(CHOOSE_WORD_MAPPING)
    public void handleWordChosen(@DestinationVariable String roomId, @Payload String chosenWord, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        gameService.chooseWord(roomId, sessionId, chosenWord);
    }

    @MessageMapping(CORRECT_GUESS_MAPPING)
    public void handleCorrectGuess(@DestinationVariable String roomId, @Payload String guess, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        gameService.handleGuess(roomId, guess, sessionId);
    }

    @MessageMapping(CURRENT_HINT_MAPPING)
    public void retrieveCurrentHint(@DestinationVariable String roomId) {
        String currentHint = roomService.getRoom(roomId).getGame().getCurrentHint();
        messagingTemplate.convertAndSend(TOPIC_ROOM_PREFIX + roomId + WORD_HINT_ENDPOINT, currentHint);
    }
}
