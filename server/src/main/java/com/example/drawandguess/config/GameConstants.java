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
    public static final String SERVER_MESSAGE_TYPE = "system";

    public static final String USER_KEY = "user";
    public static final String MESSAGE_KEY = "message";
    public static final String ROOM_NAME_KEY = "roomName";
    public static final String ROOM_ID_KEY = "roomId";
    public static final String NUMBER_OF_PARTICIPANTS_KEY = "numberOfParticipants";
    public static final String LEADERBOARD_QUEUE = "leaderboardQueue";
    public static final int SCHEDULER_POOL_SIZE = 1;
    public static final String SCHEDULER_THREAD_PREFIX = "HintScheduler-";
    
    public static String roundStartedMsg(String drawerName, String round, String totalRounds) {
        return "A new round has started. The drawer is " + drawerName + ". (Round " + round + "/" + totalRounds + ")";
    }

    public static String wordGuessedMsg(String guesserName) {
        return guesserName + " guessed the word!";
    }

    public static String noGuessMsg() {
        return "No one guessed the word.";
    }

    public static String participantJoinedMsg(String username) {
        return username + " has joined the room.";
    }

    public static String participantLeftMsg(String username) {
        return username + " has left the room.";
    }

    public static String previousDrawerQuitMsg(String newDrawer) {
        return "The previous drawer quit abruptly. The round has been reset. New drawer is: " + newDrawer;
    }

    public static String newGameStartedMsg(String round, String totalRounds, String drawerName) {
        return "A new game has started. (Round " + round + "/" + totalRounds + ") The drawer is " + drawerName + ".";
    }

    public static String gameEndedMsg() {
        return "The game has ended";
    }

    public static String finalScoreMsgAfter() {
        return " after ";
    }

    public static String finalScoreMsgRounds() {
        return " rounds. Final scores: ";
    }

    public static String finalScoreMsgNewGame() {
        return "A new game will start in ";
    }

    public static String finalScoreMsgSeconds() {
        return " seconds.";
    }

    public static String nicknameTakenMsg() {
        return "Nickname is already taken";
    }

    public static String nicknameRegisteredMsg() {
        return "Nickname registered successfully";
    }

    public static String invalidNicknameMsg() {
        return "Invalid nickname";
    }

    public static String removedParticipantMsg(String username) {
        return "Removed participant: " + username;
    }
}
