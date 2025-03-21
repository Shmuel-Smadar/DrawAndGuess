package com.example.drawandguess.service;

import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.MessageType;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    public ChatMessage systemMessage(MessageType type, String... args) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderSessionId("system");
        msg.setType("system");
        switch (type) {
            case ROUND_STARTED -> msg.setText("A new round has started. The drawer is " + args[0] + ". (Round " + args[1] + "/" + args[2] + ")");
            case WORD_GUESSED -> msg.setText(args[0] + " guessed the word!");
            case NO_GUESS -> msg.setText("No one guessed the word.");
            case GAME_ENDED -> msg.setText(args[0]);
            case PARTICIPANT_JOINED -> msg.setText(args[0] + " has joined the room.");
            case PARTICIPANT_LEFT -> msg.setText(args[0] + " has left the room.");
            case PREVIOUS_DRAWER_QUIT -> msg.setText("The previous drawer quit abruptly. The round has been reset. New drawer is: " + args[0]);
            case NEW_GAME_STARTED -> msg.setText("A new game has started. (Round " + args[0] + "/" + args[1] + ") The drawer is " + args[2] + ".");
        }
        return msg;
    }

    public ChatMessage userMessage(String sessionId, String text) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderSessionId(sessionId);
        msg.setText(text);
        msg.setType("user");
        return msg;
    }
}
