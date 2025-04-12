package com.example.drawandguess.service;

import static com.example.drawandguess.config.GameConstants.SERVER_MESSAGE_TYPE;
import static com.example.drawandguess.config.GameConstants.roundStartedMsg;
import static com.example.drawandguess.config.GameConstants.wordGuessedMsg;
import static com.example.drawandguess.config.GameConstants.noGuessMsg;
import static com.example.drawandguess.config.GameConstants.participantJoinedMsg;
import static com.example.drawandguess.config.GameConstants.participantLeftMsg;
import static com.example.drawandguess.config.GameConstants.previousDrawerQuitMsg;
import static com.example.drawandguess.config.GameConstants.newGameStartedMsg;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.MessageType;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    public ChatMessage systemMessage(MessageType type, String... args) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderSessionId(SERVER_MESSAGE_TYPE);
        msg.setMessageType(type);
        switch (type) {
            case ROUND_STARTED ->
                    msg.setText(roundStartedMsg(args[0], args[1], args[2]));
            case WORD_GUESSED ->
                    msg.setText(wordGuessedMsg(args[0]));
            case NO_GUESS ->
                    msg.setText(noGuessMsg());
            case GAME_ENDED ->
                    msg.setText(args[0]);
            case PARTICIPANT_JOINED ->
                    msg.setText(participantJoinedMsg(args[0]));
            case PARTICIPANT_LEFT ->
                    msg.setText(participantLeftMsg(args[0]));
            case PREVIOUS_DRAWER_QUIT ->
                    msg.setText(previousDrawerQuitMsg(args[0]));
            case NEW_GAME_STARTED ->
                    msg.setText(newGameStartedMsg(args[0], args[1], args[2]));
        }
        return msg;
    }

    public ChatMessage winnerAnnounce(String winnerSessionId, String text) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderSessionId(SERVER_MESSAGE_TYPE);
        msg.setMessageType(MessageType.WINNER_ANNOUNCED);
        msg.setWinnerSessionId(winnerSessionId);
        msg.setText(text);
        return msg;
    }
}
