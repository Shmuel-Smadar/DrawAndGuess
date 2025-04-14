package com.example.drawandguess.config;

public final class APIConstants {

    private APIConstants() {}

    // Allow all origins for CORS
    public static final String ALLOWED_ORIGINS = "*";

    //Endpoint for establishing STOMP connection (the client will use it to contact the server)
    public static final String STOMP_ENDPOINT = "/draw-and-guess";

    /* These prefixes manage how messages are routed:
     * APP_PREFIX: For messages sent to the controllers (by the user)
     * TOPIC_PREFIX: For broadcasting messages to multiple subscribers (users can subscribe).
     * USER_PREFIX: For sending messages to a specific user. */
    public static final String APP_PREFIX = "/app";
    public static final String TOPIC_PREFIX = "/topic";
    public static final String USER_PREFIX = "/user";


    // These constants define topic the server broadcasts to all the client who subscribed
    public static final String TOPIC_ROOMS = "/topic/rooms";
    public static final String TOPIC_ROOM_PREFIX = "/topic/room/";

    // These are actions the client can do and notify the server about
    public static final String CREATE_ROOM = "/createRoom";
    public static final String JOIN_ROOM = "/joinRoom";
    public static final String LEAVE_ROOM = "/leaveRoom";
    public static final String GET_ROOMS = "/getRooms";
    public static final String REGISTER_NICKNAME = "/registerNickname";
    public static final String CHAT_MAPPING = "/room/{roomId}/chat";
    public static final String PARTICIPANTS_MAPPING = "/room/{roomId}/getParticipants";
    public static final String DRAW_MAPPING = "/room/{roomId}/draw";
    public static final String CLEAR_CANVAS_MAPPING = "/room/{roomId}/clearCanvas";
    public static final String LEADERBOARD_MAPPING = "/leaderboard";
    public static final String WINNER_MAPPING = "/winnerMessage";
    public static final String REQUEST_WORDS_MAPPING = "/room/{roomId}/requestWords";
    public static final String CHOOSE_WORD_MAPPING = "/room/{roomId}/chooseWord";
    public static final String CORRECT_GUESS_MAPPING = "/room/{roomId}/correctGuess";
    public static final String CURRENT_HINT_MAPPING = "/room/{roomId}/getCurrentHint";


    //These constants are for the server to send to the client
    public static final String ROOM_CREATED_TOPIC = TOPIC_PREFIX + "/roomCreated";
    public static final String NICKNAME_TOPIC = TOPIC_PREFIX + "/nickname";
    public static final String WORD_OPTIONS_TOPIC = TOPIC_PREFIX +  "/wordOptions";

    // These are routing constants. where the server will serve the static files for the client (frontend)
    public static final String DRAW_AND_GUESS_PATH = "/drawandguess";
    public static final String DRAW_AND_GUESS_SLASH_PATH = "/drawandguess/";
    public static final String HOME_PATH = "/home";
    public static final String MAIN_INDEX_FILE = "forward:/drawandguess/index.html";

    // vm connector for EmbeddedActiveMQ
    public static final String VM_CONNECTOR = "vm://localhost";

    // static helper methods for path construction
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
