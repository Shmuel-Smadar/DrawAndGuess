package com.example.drawandguess.config;

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

    private static final String ROUND_STARTED_MESSAGE =
            "A new round has started. The drawer is %s. (Round %d/%d)";
    private static final String WORD_GUESSED_MESSAGE = "%s guessed the word!";
    private static final String NO_GUESS_MESSAGE = "No one guessed the word.";
    private static final String GAME_ENDED_MESSAGE =
            "The game has ended after %d rounds. Final scores: %s.\nA new game will start in %d seconds.";
    private static final String PARTICIPANT_JOINED_MESSAGE = "%s has joined the room.";
    private static final String PARTICIPANT_LEFT_MESSAGE = "%s has left the room.";
    private static final String PREVIOUS_DRAWER_QUIT_MESSAGE =
            "The previous drawer quit abruptly. The round has been reset. New drawer is: %s";
    private static final String NEW_GAME_STARTED_MESSAGE =
            "A new game has started. (Round %d/%d) The drawer is %s.";

    public static final String NICKNAME_TAKEN_MESSAGE = "Nickname is already taken";
    public static final String NICKNAME_REGISTERED_MESSAGE = "Nickname registered successfully";
    public static final String INVALID_NICKNAME_MESSAGE = "Invalid nickname";

    public static String roundStartedMessage(String drawerName, int currentRound, int totalRounds) {
        return ROUND_STARTED_MESSAGE.formatted(drawerName, currentRound, totalRounds);
    }

    public static String wordGuessedMessage(String username) {
        return WORD_GUESSED_MESSAGE.formatted(username);
    }

    public static String noGuessMessage() {
        return NO_GUESS_MESSAGE;
    }

    public static String gameEndedMessage(int totalRounds, String finalScores, int newGameDelaySeconds) {
        return GAME_ENDED_MESSAGE.formatted(totalRounds, finalScores, newGameDelaySeconds);
    }

    public static String participantJoinedMessage(String username) {
        return PARTICIPANT_JOINED_MESSAGE.formatted(username);
    }

    public static String participantLeftMessage(String username) {
        return PARTICIPANT_LEFT_MESSAGE.formatted(username);
    }

    public static String previousDrawerQuitMessage(String newDrawerName) {
        return PREVIOUS_DRAWER_QUIT_MESSAGE.formatted(newDrawerName);
    }

    public static String newGameStartedMessage(int currentRound, int totalRounds, String drawerName) {
        return NEW_GAME_STARTED_MESSAGE.formatted(currentRound, totalRounds, drawerName);
    }
}
