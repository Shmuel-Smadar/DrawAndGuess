package com.example.drawandguess.service;

import static com.example.drawandguess.config.GameConstants.NEW_GAME_DELAY_SECONDS;
import static com.example.drawandguess.config.GameConstants.SERVER_MESSAGE_TYPE;
import static com.example.drawandguess.config.GameConstants.TOTAL_ROUNDS;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.model.WordOptions;
import com.example.drawandguess.model.MessageType;
import com.example.drawandguess.model.ClearCanvasMessage;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class GameLogicService {
    private final ChatService chatService;
    private final ParticipantService participantService;
    private final RoomService roomService;
    private final MessageService messageService;
    private final WordService wordService;
    private final TaskScheduler taskScheduler;
    private final DrawingService drawingService;
    private final HintService hintService;
    private final ScoringService scoringService;
    private final LeaderboardService leaderboardService;

    public GameLogicService(ChatService chatService,
                            ParticipantService participantService,
                            RoomService roomService,
                            MessageService messageService,
                            WordService wordService,
                            TaskScheduler taskScheduler,
                            DrawingService drawingService,
                            HintService hintService,
                            LeaderboardService leaderboardService,
                            ScoringService scoringService) {
        this.chatService = chatService;
        this.participantService = participantService;
        this.roomService = roomService;
        this.messageService = messageService;
        this.wordService = wordService;
        this.drawingService = drawingService;
        this.hintService = hintService;
        this.taskScheduler = taskScheduler;
        this.leaderboardService = leaderboardService;
        this.scoringService = scoringService;
    }

    // A method which select randomly 3 words for the drawer to choose from and send it
    public WordOptions requestWords(String roomId, String sessionId) {
        Room room = roomService.getRoom(roomId);
        Game game = room.getGame();
        if (game.isDrawer(sessionId)) {
            return wordService.getRandomWords();
        }
        return new WordOptions();
    }

    // A method that gets the word the drawer has chosen and update the state of the game accordingly
    public void chooseWord(String roomId, String sessionId, String chosenWord) {
        Room room = roomService.getRoom(roomId);
        Game game = room.getGame();
        if (game.isDrawer(sessionId)) {
            game.setChosenWord(chosenWord);
            String drawerUsername = participantService.findParticipantBySessionId(sessionId).getUsername();
            chatService.sendChatMessage(roomId, messageService.systemMessage(
                    MessageType.ROUND_STARTED,
                    drawerUsername,
                    String.valueOf(game.getRoundCount() + 1),
                    String.valueOf(TOTAL_ROUNDS)
            ));
            hintService.startHintProgression(roomId, game, () -> handleNoGuess(roomId, game));
        }
    }

    /* A method that gets a guess made by a non drawer and checks if it's a match,
     * if so, update the state of the game accordingly and starts the next round.
     */
    public void handleGuess(String roomId, String guess, String sessionId) {
        Room room = roomService.getRoom(roomId);
        Game game = room.getGame();
        if (game.isCorrectGuess(guess)) {
            scoringService.handleScoring(roomId, game, guess, sessionId);
            roomService.broadcastParticipants(roomId);
            hintService.stopHintProgression(roomId);
            drawingService.clearCanvas(roomId, new ClearCanvasMessage(SERVER_MESSAGE_TYPE));
            game.nextRound();
            String username = participantService.findParticipantBySessionId(sessionId).getUsername();
            chatService.sendChatMessage(roomId, messageService.systemMessage(MessageType.WORD_GUESSED, username));
            if (game.isGameOver()) {
                endGame(roomId, game);
            } else {
                updateDrawerAndBroadcast(roomId, game);
            }
        }
    }

    // A method that handles the case in which no one was able to guess the correct word. starts the next round.
    public void handleNoGuess(String roomId, Game game) {
        hintService.stopHintProgression(roomId);
        drawingService.clearCanvas(roomId, new ClearCanvasMessage(SERVER_MESSAGE_TYPE));
        ChatMessage msg = messageService.systemMessage(MessageType.NO_GUESS);
        chatService.sendChatMessage(roomId, msg);
        game.nextRound();
        if (game.isGameOver()) {
            endGame(roomId, game);
        } else {
            updateDrawerAndBroadcast(roomId, game);
        }
    }

    public void userLeftRoom(String roomId, String sessionId) {
        Room room = roomService.getRoom(roomId);
        Game game = room.getGame();
        Participant p = participantService.findParticipantBySessionId(sessionId);
        if (p == null) return;
        boolean wasDrawer = game.isDrawer(sessionId);
        game.removeParticipant(sessionId);
        chatService.sendChatMessage(roomId, messageService.systemMessage(MessageType.PARTICIPANT_LEFT, p.getUsername()));
        if (game.isGameOver()) {
            roomService.broadcastParticipants(roomId);
            if (game.getParticipantSessionIds().isEmpty()) {
                roomService.deleteRoom(roomId);
            }
            roomService.broadcastRooms();
            return;
        }
        if (wasDrawer) {
            game.resetRound();
            hintService.stopHintProgression(roomId);
            String newDrawerId = game.getCurrentDrawer();
            if (newDrawerId != null) {
                participantService.setDrawer(newDrawerId, true);
                chatService.sendChatMessage(roomId, messageService.systemMessage(
                        MessageType.PREVIOUS_DRAWER_QUIT,
                        participantService.findParticipantBySessionId(newDrawerId).getUsername()
                ));
            }
        }
        roomService.broadcastParticipants(roomId);
        if (game.getParticipantSessionIds().isEmpty()) {
            roomService.deleteRoom(roomId);
        }
        roomService.broadcastRooms();
    }

    /* A method that responsible for handling sudden user disconnection.
    * goes through all the room and make sure to update the room the user left. */
    public void handleDisconnect(String sessionId) {
        Participant participant = participantService.findParticipantBySessionId(sessionId);
        if (participant == null) return;
        for (Room room : roomService.getAllRooms()) {
            if (room.getGame().getParticipantSessionIds().contains(sessionId)) {
                userLeftRoom(room.getRoomId(), sessionId);
            }
        }
        participantService.removeParticipant(sessionId);
    }

    /* A method responsible for managing the end of the game.
    * notifying the room about the winner and the scores, and calling for a new game to start. */
    private void endGame(String roomId, Game game) {
        drawingService.clearCanvas(roomId, new ClearCanvasMessage(SERVER_MESSAGE_TYPE));
        String finalScoreMessage = scoringService.buildFinalScoreMessage(game);
        chatService.sendChatMessage(roomId, messageService.systemMessage(MessageType.GAME_ENDED,  String.valueOf(TOTAL_ROUNDS), finalScoreMessage,  String.valueOf(NEW_GAME_DELAY_SECONDS)));
        String winnerSessionId = scoringService.getWinnerSessionId(game);
        if (winnerSessionId != null) {
            String winnerName = participantService.findParticipantBySessionId(winnerSessionId).getUsername();
            leaderboardService.saveScores(winnerName, game.getScore(winnerName));
            chatService.sendChatMessage(roomId, messageService.systemMessage(MessageType.WINNER_ANNOUNCED, winnerSessionId, winnerName + " is the winner!"));
        }
        scheduleNewGame(roomId, game);
    }

    // A method that responsible for setting the drawer chosen by the game and updating the room about the current drawer.
    public void updateDrawerAndBroadcast(String roomId, Game game) {
        String currentDrawer = game.getCurrentDrawer();
        participantService.getAllParticipants().values().forEach(
                p -> participantService.setDrawer(p.getSessionId(), p.getSessionId().equals(currentDrawer))
        );
        roomService.broadcastParticipants(roomId);
    }

    // A method that responsible for scheduling a new game and notifying the room about it when it starts.
    private void scheduleNewGame(String roomId, Game game) {
        taskScheduler.schedule(() -> {
            game.resetGame();
            if (!game.getParticipantSessionIds().isEmpty()) {
                String firstDrawerId = game.getCurrentDrawer();
                participantService.getAllParticipants().values().forEach(
                        p -> participantService.setDrawer(p.getSessionId(), p.getSessionId().equals(firstDrawerId))
                );
                ChatMessage newGameMsg = messageService.systemMessage(
                        MessageType.NEW_GAME_STARTED,
                        String.valueOf(game.getRoundCount() + 1),
                        String.valueOf(TOTAL_ROUNDS),
                        participantService.findParticipantBySessionId(firstDrawerId).getUsername()
                );
                chatService.sendChatMessage(roomId, newGameMsg);
                roomService.broadcastParticipants(roomId);
                updateDrawerAndBroadcast(roomId, game);
            }
        }, Instant.now().plusSeconds(NEW_GAME_DELAY_SECONDS));
    }
}
