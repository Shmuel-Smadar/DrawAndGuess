package com.example.drawandguess.config;

public final class GameConstants {
    private GameConstants() {}

    //names can have english and hebrew characters and numbers
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
    public static final String SCORE_SEPARATOR = ":";

    public static final String ROUND_STARTED_MSG_PREFIX = "A new round has started. The drawer is ";
    public static final String NO_GUESS_MSG = "No one guessed the word.";
    public static final String GAME_ENDED_MSG = "The game has ended";
    public static final String PARTICIPANT_JOINED_MSG = " has joined the room.";
    public static final String PARTICIPANT_LEFT_MSG = " has left the room.";
    public static final String PREVIOUS_DRAWER_QUIT_MSG = "The previous drawer quit abruptly. The round has been reset. New drawer is: ";
    public static final String NEW_GAME_STARTED_MSG_PREFIX = "A new game has started. (Round ";
    public static final String NICKNAME_TAKEN_MSG = "Nickname is already taken";
    public static final String NICKNAME_REGISTERED_MSG = "Nickname registered successfully";
    public static final String INVALID_NICKNAME_MSG = "Invalid nickname";
    public static final String REMOVED_PARTICIPANT_MSG_PREFIX = "Removed participant: ";

    public static final String USER_KEY = "user";
    public static final String MESSAGE_KEY = "message";
    public static final String ROOM_NAME_KEY = "roomName";
    public static final String ROOM_ID_KEY = "roomId";
    public static final String NUMBER_OF_PARTICIPANTS_KEY = "numberOfParticipants";
    public static final String LEADERBOARD_QUEUE = "leaderboardQueue";
    public static final int SCHEDULER_POOL_SIZE = 1;
    public static final String SCHEDULER_THREAD_PREFIX = "HintScheduler-";
}
