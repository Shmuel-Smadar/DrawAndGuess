package com.example.drawandguess.controller;

import com.example.drawandguess.model.WordOptions;
import com.example.drawandguess.service.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller
public class
WordGameController {
    private final GameService gameService;


    public WordGameController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/room/{roomId}/requestWords")
    @SendToUser("/topic/wordOptions")
    public WordOptions handleWordRequest(@DestinationVariable String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        return gameService.requestWords(roomId, sessionId);
    }

    @MessageMapping("/room/{roomId}/chooseWord")
    public void handleWordChosen(@DestinationVariable String roomId, @Payload String chosenWord, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        gameService.chooseWord(roomId, sessionId, chosenWord);
    }

    @MessageMapping("/room/{roomId}/correctGuess")
    public void handleCorrectGuess(@DestinationVariable String roomId, @Payload String guess, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        gameService.correctGuess(roomId, guess, sessionId);
    }
}
