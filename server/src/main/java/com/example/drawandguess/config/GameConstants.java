package com.example.drawandguess.config;

import com.example.drawandguess.model.MessageType;

/*
 * GameConstants defines various constants for game logic,
 * including round configurations, scoring, timers, etc.
 */
public final class GameConstants {
    private GameConstants() {}

    // Regex for nickname validation
    public static final String NICKNAME_REGEX = "^[A-Za-z0-9\\u0590-\\u05FF]+$";

    // Intervals, delays
    public static final int HINT_INTERVAL_SECONDS = 15;
    public static final int TIMER_DELAY_MS = 100;
    public static final int NEW_GAME_DELAY_SECONDS = 10;
    public static final int TOTAL_ROUNDS = 6;

    // Scoring logic
    public static final int GUESSER_BASE_POINTS = 10;
    public static final int DRAWER_BASE_POINTS = 5;
    public static final int MULTIPLIER_BASE = 5;

    // Room and message constraints
    public static final int MAX_ROOM_NAME_LENGTH = 13;
    public static final int MAX_CHAT_MESSAGE_LENGTH = 50;
    public static final int NUMBER_OF_WORDS_TO_CHOOSE_FROM = 3;

    // Special keys for system messages, user, etc.
    public static final String SERVER_MESSAGE_TYPE = "system";
    public static final String USER_KEY = "user";
    public static final String MESSAGE_KEY = "message";
    public static final String ROOM_NAME_KEY = "roomName";
    public static final String ROOM_ID_KEY = "roomId";
    public static final String NUMBER_OF_PARTICIPANTS_KEY = "numberOfParticipants";

    // Scheduler config
    public static final int SCHEDULER_POOL_SIZE = 1;
    public static final String SCHEDULER_THREAD_PREFIX = "HintScheduler-";

    // Builds a system message string based on  the enum 'MessageType'.
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
                    "The game has ended after " + args[0] + " rounds. Final scores: "
                            + args[1] + ".\nA new game will start in "
                            + args[2] + " seconds.";

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
