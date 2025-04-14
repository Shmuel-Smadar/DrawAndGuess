package com.example.drawandguess.controller;

import com.example.drawandguess.model.WordOptions;
import com.example.drawandguess.service.GameLogicService;
import com.example.drawandguess.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import static com.example.drawandguess.config.APIConstants.REQUEST_WORDS_MAPPING;
import static com.example.drawandguess.config.APIConstants.WORD_OPTIONS_TOPIC;
import static com.example.drawandguess.config.APIConstants.CHOOSE_WORD_MAPPING;
import static com.example.drawandguess.config.APIConstants.GUESS_MAPPING;
import static com.example.drawandguess.config.APIConstants.CURRENT_HINT_MAPPING;
import static com.example.drawandguess.config.APIConstants.topicRoomWordHint;

@Controller
public class GameLogicController {
    private static final Logger logger = LoggerFactory.getLogger(GameLogicController.class);
    private final GameLogicService gameLogicService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameLogicController(GameLogicService gameLogicService, RoomService roomService, SimpMessagingTemplate messagingTemplate) {
        this.gameLogicService = gameLogicService;
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
    }

    // A method responsible for getting the drawer the words to choose from
    @MessageMapping(REQUEST_WORDS_MAPPING)
    @SendToUser(WORD_OPTIONS_TOPIC)
    public WordOptions handleWordRequest(@DestinationVariable String roomId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            return gameLogicService.requestWords(roomId, sessionId);
        } catch (Exception e) {
            logger.error("Error in handleWordRequest", e);
            return new WordOptions();
        }
    }

    /* A method that gets the choice made by the drawer (about which word to draw)
     and updates the state of the game according to that*/
    @MessageMapping(CHOOSE_WORD_MAPPING)
    public void handleWordChosen(@DestinationVariable String roomId, @Payload String chosenWord, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            gameLogicService.chooseWord(roomId, sessionId, chosenWord);
        } catch (Exception e) {
            logger.error("Error in handleWordChosen", e);
        }
    }

    /* A method that gets a request from a client to get the current hint for the game.
     it then sends it to all the users in the room */
    @MessageMapping(CURRENT_HINT_MAPPING)
    public void retrieveCurrentHint(@DestinationVariable String roomId) {
        try {
            String currentHint = roomService.getRoom(roomId).getGame().getCurrentHint();
            messagingTemplate.convertAndSend(topicRoomWordHint(roomId), currentHint);
        } catch (Exception e) {
            logger.error("Error in retrieveCurrentHint", e);
        }
    }
}
