package com.example.drawandguess.controller;

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
public class
WordGameController {
    private final GameService gameService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    public WordGameController(GameService gameService, RoomService roomService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
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

    @MessageMapping("/room/{roomId}/getCurrentHint")
    public void retrieveCurrentHint(@DestinationVariable String roomId) {
        String currentHint = roomService.getRoom(roomId).getGame().getCurrentHint();
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/wordHint", currentHint);
    }

}
