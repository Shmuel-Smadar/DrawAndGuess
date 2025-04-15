package com.example.drawandguess.service;

import static com.example.drawandguess.config.GameConstants.SERVER_MESSAGE_TYPE;
import static com.example.drawandguess.config.GameConstants.buildSystemMessage;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.MessageType;
import org.springframework.stereotype.Service;

/*
 * A service that creates system messages according to the ENUM sent.
 */
@Service
public class MessageService {
    /* A method responsible for building a system message
    * get a type and arguments, and sends to a function dedicated to parse the arguments
    * to create appropriate message */
    public ChatMessage systemMessage(MessageType type, String... args) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderSessionId(SERVER_MESSAGE_TYPE);
        msg.setMessageType(type);
        if(type == MessageType.WINNER_ANNOUNCED) {
            msg.setWinnerSessionId(args[0]);
            msg.setText(args[1]);
            return msg;
        }
        msg.setText(buildSystemMessage(type, args));
        return msg;
    }
}
