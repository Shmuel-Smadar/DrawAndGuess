package com.example.drawandguess.service;

import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.MessageType;
import org.springframework.stereotype.Service;

import static com.example.drawandguess.config.Constants.ROUND_STARTED_MSG_PREFIX;
import static com.example.drawandguess.config.Constants.NO_GUESS_MSG;
import static com.example.drawandguess.config.Constants.GAME_ENDED_MSG;
import static com.example.drawandguess.config.Constants.PARTICIPANT_JOINED_MSG;
import static com.example.drawandguess.config.Constants.PARTICIPANT_LEFT_MSG;
import static com.example.drawandguess.config.Constants.PREVIOUS_DRAWER_QUIT_MSG;
import static com.example.drawandguess.config.Constants.NEW_GAME_STARTED_MSG_PREFIX;

@Service
public class MessageService {
    public ChatMessage systemMessage(MessageType type, String... args) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderSessionId("system");
        msg.setMessageType(type);
        switch (type) {
            case ROUND_STARTED ->
                msg.setText(ROUND_STARTED_MSG_PREFIX + args[0] + ". (Round " + args[1] + "/" + args[2] + ")");
            case WORD_GUESSED ->
                msg.setText(args[0] + " guessed the word!");
            case NO_GUESS ->
                msg.setText(NO_GUESS_MSG);
            case GAME_ENDED ->
                msg.setText(args[0]);
            case PARTICIPANT_JOINED ->
                msg.setText(args[0] + PARTICIPANT_JOINED_MSG);
            case PARTICIPANT_LEFT ->
                msg.setText(args[0] + PARTICIPANT_LEFT_MSG);
            case PREVIOUS_DRAWER_QUIT ->
                msg.setText(PREVIOUS_DRAWER_QUIT_MSG + args[0]);
            case NEW_GAME_STARTED ->
                msg.setText(NEW_GAME_STARTED_MSG_PREFIX + args[0] + "/" + args[1] + ") The drawer is " + args[2] + ".");
        }
        return msg;
    }

    public ChatMessage userMessage(String sessionId, String text) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderSessionId(sessionId);
        msg.setText(text);
        msg.setMessageType(null);
        return msg;
    }

    public ChatMessage winnerAnnounce(String winnerSessionId, String text) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderSessionId("system");
        msg.setMessageType(MessageType.WINNER_ANNOUNCED);
        msg.setWinnerSessionId(winnerSessionId);
        msg.setText(text);
        return msg;
    }
}
