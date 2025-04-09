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
    public static final String SCORE_SEPARATOR = ":";

    public static final String CREATE_ROOM = "/createRoom";
    public static final String ROOM_CREATED_TOPIC = "/topic/roomCreated";
    public static final String JOIN_ROOM = "/joinRoom";
    public static final String LEAVE_ROOM = "/leaveRoom";
    public static final String GET_ROOMS = "/getRooms";
    public static final String REGISTER_NICKNAME = "/registerNickname";
    public static final String NICKNAME_TOPIC = "/topic/nickname";
    public static final String CHAT_MAPPING = "/room/{roomId}/chat";
    public static final String PARTICIPANTS_MAPPING = "/room/{roomId}/getParticipants";

    public static final int MAX_ROOM_NAME_LENGTH = 13;
    public static final int MAX_CHAT_MESSAGE_LENGTH = 50;

    public static final String NICKNAME_REGEX = "^[A-Za-z0-9\\u0590-\\u05FF]+$";

    public static final int SCHEDULER_POOL_SIZE = 1;
    public static final String SCHEDULER_THREAD_PREFIX = "HintScheduler-";
    public static final int HINT_INTERVAL_SECONDS = 10;
    public static final int TIMER_DELAY_MS = 100;
    public static final int NEW_GAME_DELAY_SECONDS = 10;
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
    public static final String INVALID_NICKNAME_MSG = "Invalid nickname";
    public static final String REMOVED_PARTICIPANT_MSG_PREFIX = "Removed participant: ";

    public static final String DRAW_AND_GUESS_PATH = "/drawandguess";
    public static final String DRAW_AND_GUESS_SLASH_PATH = "/drawandguess/";
    public static final String HOME_PATH = "/home";
    public static final String MAIN_INDEX_FILE = "forward:/drawandguess/index.html";

    public static final String DRAW_MAPPING = "/room/{roomId}/draw";
    public static final String CLEAR_CANVAS_MAPPING = "/room/{roomId}/clearCanvas";
    public static final String LEADERBOARD_MAPPING = "/leaderboard";
    public static final String WINNER_MAPPING = "/winnerMessage";
    public static final String REQUEST_WORDS_MAPPING = "/room/{roomId}/requestWords";
    public static final String WORD_OPTIONS_TOPIC = "/topic/wordOptions";
    public static final String CHOOSE_WORD_MAPPING = "/room/{roomId}/chooseWord";
    public static final String CORRECT_GUESS_MAPPING = "/room/{roomId}/correctGuess";
    public static final String CURRENT_HINT_MAPPING = "/room/{roomId}/getCurrentHint";

    public static final String WORD_HINT_ENDPOINT = "/wordHint";
    public static final String USER_KEY = "user";
    public static final String MESSAGE_KEY = "message";
    public static final String ROOM_NAME_KEY = "roomName";
    public static final String ROOM_ID_KEY = "roomId";
    public static final String NUMBER_OF_PARTICIPANTS_KEY = "numberOfParticipants";
    public static final String PARTICIPANTS_ENDPOINT = "/participants";

    public static String topicRoomChat(String roomId) {
        return TOPIC_ROOM_PREFIX + roomId + "/chat";
    }
    public static String topicRoomParticipants(String roomId) {
        return TOPIC_ROOM_PREFIX + roomId + "/participants";
    }
    public static String topicRoomDrawing(String roomId) {
        return TOPIC_ROOM_PREFIX + roomId + "/drawing";
    }
    public static String topicRoomClearCanvas(String roomId) {
        return TOPIC_ROOM_PREFIX + roomId + "/clearCanvas";
    }
    public static String topicRoomWordHint(String roomId) {
        return TOPIC_ROOM_PREFIX + roomId + "/wordHint";
    }
}
