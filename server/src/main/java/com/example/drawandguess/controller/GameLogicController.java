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

import static com.example.drawandguess.config.APIConstants.*;

/*
 * Controller that manages the game flow requests:
 * requesting word options, choosing a word, retrieving the current hint, etc.
 */
@Controller
public class GameLogicController {
    private static final Logger logger = LoggerFactory.getLogger(GameLogicController.class);
    private final GameLogicService gameLogicService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameLogicController(GameLogicService gameLogicService,
                               RoomService roomService,
                               SimpMessagingTemplate messagingTemplate) {
        this.gameLogicService = gameLogicService;
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
    }

    /*
     * Gets a request from the drawer to get words to choose from
     * uses the GameLogicService to obtain them and return to the drawer.
     */
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

    /*
     * Receives word the drawer chosen to draw.
     * Tells the GameLogicService to set that word and updatethe  game state accordingly.
     */
    @MessageMapping(CHOOSE_WORD_MAPPING)
    public void handleWordChosen(@DestinationVariable String roomId,
                                 @Payload String chosenWord,
                                 SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            gameLogicService.chooseWord(roomId, sessionId, chosenWord);
        } catch (Exception e) {
            logger.error("Error in handleWordChosen", e);
        }
    }

    /*
     * Receives a client hint request,
     * it then sends it to all the users in the room
     */
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
