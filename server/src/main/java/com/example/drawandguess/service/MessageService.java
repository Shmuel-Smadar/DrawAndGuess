package com.example.drawandguess.service;

import static com.example.drawandguess.config.GameConstants.SERVER_MESSAGE_TYPE;
import static com.example.drawandguess.config.GameConstants.gameEndedMessage;
import static com.example.drawandguess.config.GameConstants.newGameStartedMessage;
import static com.example.drawandguess.config.GameConstants.noGuessMessage;
import static com.example.drawandguess.config.GameConstants.participantJoinedMessage;
import static com.example.drawandguess.config.GameConstants.participantLeftMessage;
import static com.example.drawandguess.config.GameConstants.previousDrawerQuitMessage;
import static com.example.drawandguess.config.GameConstants.roundStartedMessage;
import static com.example.drawandguess.config.GameConstants.wordGuessedMessage;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.MessageType;
import org.springframework.stereotype.Service;

/*
 * A service that creates system messages according to the ENUM sent.
 */
@Service
public class MessageService {
    public ChatMessage roundStarted(String drawerName, int currentRound, int totalRounds) {
        return systemMessage(MessageType.ROUND_STARTED, roundStartedMessage(drawerName, currentRound, totalRounds));
    }

    public ChatMessage wordGuessed(String username) {
        return systemMessage(MessageType.WORD_GUESSED, wordGuessedMessage(username));
    }

    public ChatMessage noGuess() {
        return systemMessage(MessageType.NO_GUESS, noGuessMessage());
    }

    public ChatMessage gameEnded(int totalRounds, String finalScores, int newGameDelaySeconds) {
        return systemMessage(MessageType.GAME_ENDED, gameEndedMessage(totalRounds, finalScores, newGameDelaySeconds));
    }

    public ChatMessage participantJoined(String username) {
        return systemMessage(MessageType.PARTICIPANT_JOINED, participantJoinedMessage(username));
    }

    public ChatMessage participantLeft(String username) {
        return systemMessage(MessageType.PARTICIPANT_LEFT, participantLeftMessage(username));
    }

    public ChatMessage previousDrawerQuit(String newDrawerName) {
        return systemMessage(MessageType.PREVIOUS_DRAWER_QUIT, previousDrawerQuitMessage(newDrawerName));
    }

    public ChatMessage newGameStarted(int currentRound, int totalRounds, String drawerName) {
        return systemMessage(MessageType.NEW_GAME_STARTED, newGameStartedMessage(currentRound, totalRounds, drawerName));
    }

    public ChatMessage winnerAnnounced(String winnerSessionId, String text) {
        ChatMessage msg = systemMessage(MessageType.WINNER_ANNOUNCED, text);
        msg.setWinnerSessionId(winnerSessionId);
        return msg;
    }

    private ChatMessage systemMessage(MessageType type, String text) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderSessionId(SERVER_MESSAGE_TYPE);
        msg.setMessageType(type);
        msg.setText(text);
        return msg;
    }
}
