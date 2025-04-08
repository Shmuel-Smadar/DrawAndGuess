package com.example.drawandguess.controller;

import com.example.drawandguess.config.Constants;
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

    @MessageMapping(Constants.REQUEST_WORDS_MAPPING)
    @SendToUser(Constants.WORD_OPTIONS_TOPIC)
    public WordOptions handleWordRequest(@DestinationVariable String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        return gameService.requestWords(roomId, sessionId);
    }

    @MessageMapping(Constants.CHOOSE_WORD_MAPPING)
    public void handleWordChosen(@DestinationVariable String roomId, @Payload String chosenWord, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        gameService.chooseWord(roomId, sessionId, chosenWord);
    }

    @MessageMapping(Constants.CORRECT_GUESS_MAPPING)
    public void handleCorrectGuess(@DestinationVariable String roomId, @Payload String guess, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        gameService.handleGuess(roomId, guess, sessionId);
    }

    @MessageMapping(Constants.CURRENT_HINT_MAPPING)
    public void retrieveCurrentHint(@DestinationVariable String roomId) {
        String currentHint = roomService.getRoom(roomId).getGame().getCurrentHint();
        messagingTemplate.convertAndSend(Constants.TOPIC_ROOM_PREFIX + roomId + Constants.WORD_HINT_ENDPOINT, currentHint);
    }
}
