package com.example.drawandguess.config;

public final class Constants {
    private Constants() {}

    public static final String VM_CONNECTOR = "vm://localhost";
    public static final String ALLOWED_ORIGINS = "*";
    public static final String STOMP_ENDPOINT = "/draw-and-guess";
    public static final String APP_PREFIX = "/app";
    public static final String TOPIC_PREFIX = "/topic";
    public static final String USER_PREFIX = "/user";
    public static final String TOPIC_ROOMS = "/topic/rooms";
    public static final String TOPIC_ROOM_PREFIX = "/topic/room/";
    public static final String LEADERBOARD_QUEUE = "leaderboardQueue";

    public static final int SCHEDULER_POOL_SIZE = 1;
    public static final String SCHEDULER_THREAD_PREFIX = "HintScheduler-";
    public static final int HINT_INTERVAL_SECONDS = 10;
    public static final int TIMER_DELAY_MS = 100;

    public static final int TOTAL_ROUNDS = 2;

    public static final int GUESSER_BASE_POINTS = 10;
    public static final int DRAWER_BASE_POINTS = 5;
    public static final int MULTIPLIER_BASE = 5;

    public static final String ROUND_STARTED_MSG_PREFIX = "A new round has started. The drawer is ";
    public static final String NO_GUESS_MSG = "No one guessed the word.";
    public static final String GAME_ENDED_MSG = "The game has ended";
    public static final String NO_ONE_GUESSED_MSG = "No one guessed the word. The game has ended";
    public static final String PARTICIPANT_JOINED_MSG = " has joined the room.";
    public static final String PARTICIPANT_LEFT_MSG = " has left the room.";
    public static final String PREVIOUS_DRAWER_QUIT_MSG = "The previous drawer quit abruptly. The round has been reset. New drawer is: ";
    public static final String NEW_GAME_STARTED_MSG_PREFIX = "A new game has started. (Round ";
    public static final String NICKNAME_TAKEN_MSG = "Nickname is already taken";
    public static final String NICKNAME_REGISTERED_MSG = "Nickname registered successfully";
    public static final String REMOVED_PARTICIPANT_MSG_PREFIX = "Removed participant: ";
}
