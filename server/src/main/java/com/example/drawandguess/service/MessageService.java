package com.example.drawandguess.service;

import static com.example.drawandguess.config.GameConstants.SERVER_MESSAGE_TYPE;
import static com.example.drawandguess.config.GameConstants.buildSystemMessage;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.MessageType;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    public ChatMessage systemMessage(MessageType type, String... args) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderSessionId(SERVER_MESSAGE_TYPE);
        msg.setMessageType(type);
        msg.setText(buildSystemMessage(type, args));
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
