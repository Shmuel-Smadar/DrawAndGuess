package com.example.drawandguess.model;

/*
 * Enumerates possible types of system messages.
 */
public enum MessageType {
    ROUND_STARTED,
    WORD_GUESSED,
    NO_GUESS,
    GAME_ENDED,
    PARTICIPANT_JOINED,
    PARTICIPANT_LEFT,
    PREVIOUS_DRAWER_QUIT,
    NEW_GAME_STARTED,
    WINNER_ANNOUNCED,
    CHAT_MESSAGE,
    NICKNAME_TAKEN,
    NICKNAME_REGISTERED,
    INVALID_NICKNAME
}
