package com.example.drawandguess.config;

import com.example.drawandguess.model.MessageType;

public final class GameConstants {
    private GameConstants() {}

    // Constants for game logic, scoring and messages
    public static final String NICKNAME_REGEX = "^[A-Za-z0-9\\u0590-\\u05FF]+$";
    public static final int HINT_INTERVAL_SECONDS = 10;
    public static final int TIMER_DELAY_MS = 100;
    public static final int NEW_GAME_DELAY_SECONDS = 10;
    public static final int TOTAL_ROUNDS = 2;
    public static final int GUESSER_BASE_POINTS = 10;
    public static final int DRAWER_BASE_POINTS = 5;
    public static final int MULTIPLIER_BASE = 5;
    public static final int MAX_ROOM_NAME_LENGTH = 13;
    public static final int MAX_CHAT_MESSAGE_LENGTH = 50;
    public static final int NUMBER_OF_WORDS_TO_CHOOSE_FROM = 3;
    public static final String SERVER_MESSAGE_TYPE = "system";
    public static final String USER_KEY = "user";
    public static final String MESSAGE_KEY = "message";
    public static final String ROOM_NAME_KEY = "roomName";
    public static final String ROOM_ID_KEY = "roomId";
    public static final String NUMBER_OF_PARTICIPANTS_KEY = "numberOfParticipants";
    public static final int SCHEDULER_POOL_SIZE = 1;
    public static final String SCHEDULER_THREAD_PREFIX = "HintScheduler-";

    public static String buildSystemMessage(MessageType type, String... args) {
        return switch(type) {
            case ROUND_STARTED ->
                    "A new round has started. The drawer is " + args[0]
                            + ". (Round " + args[1] + "/" + args[2] + ")";
            case WORD_GUESSED ->
                    args[0] + " guessed the word!";
            case NO_GUESS ->
                    "No one guessed the word.";
            case GAME_ENDED ->
                    "The game has ended after " + args[0] + " rounds. Final scores: " +
                            args[1] + "\nA new game will start in " +
                            args[2] + " seconds.";


            case PARTICIPANT_JOINED ->
                    args[0] + " has joined the room.";
            case PARTICIPANT_LEFT ->
                    args[0] + " has left the room.";
            case PREVIOUS_DRAWER_QUIT ->
                    "The previous drawer quit abruptly. The round has been reset. New drawer is: " + args[0];
            case NEW_GAME_STARTED ->
                    "A new game has started. (Round " + args[0] + "/" + args[1]
                            + ") The drawer is " + args[2] + ".";
            case WINNER_ANNOUNCED, CHAT_MESSAGE ->
                    "";
            case NICKNAME_TAKEN ->
                    "Nickname is already taken";
            case NICKNAME_REGISTERED ->
                    "Nickname registered successfully";
            case INVALID_NICKNAME ->
                    "Invalid nickname";
        };
    }
}
