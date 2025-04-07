package com.example.drawandguess.service;

import com.example.drawandguess.config.Constants;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.model.WordOptions;
import com.example.drawandguess.model.MessageType;
import com.example.drawandguess.model.ClearCanvasMessage;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import static com.example.drawandguess.config.Constants.GAME_ENDED_MSG;
import static com.example.drawandguess.config.Constants.NO_ONE_GUESSED_MSG;

@Service
public class GameService {
    private final ChatService chatService;
    private final ParticipantService participantService;
    private final RoomService roomService;
    private final TaskScheduler taskScheduler;
    private final LeaderboardService leaderboardService;
    private final MessageService messageService;
    private final WordService wordService;
    private final DrawingService drawingService;
    private final HintService hintService;

    public GameService(
            ChatService chatService,
            ParticipantService participantService,
            RoomService roomService,
            TaskScheduler taskScheduler,
            LeaderboardService leaderboardService,
            MessageService messageService,
            WordService wordService,
            DrawingService drawingService,
            HintService hintService
    ) {
        this.chatService = chatService;
        this.participantService = participantService;
        this.roomService = roomService;
        this.taskScheduler = taskScheduler;
        this.leaderboardService = leaderboardService;
        this.messageService = messageService;
        this.wordService = wordService;
        this.drawingService = drawingService;
        this.hintService = hintService;
    }

    public WordOptions requestWords(String roomId, String sessionId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return new WordOptions();
        Game game = room.getGame();
        if (game.isDrawer(sessionId)) {
            return wordService.getRandomWords();
        }
        return new WordOptions();
    }

    public void chooseWord(String roomId, String sessionId, String chosenWord) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Game game = room.getGame();
        if (game.isDrawer(sessionId)) {
            game.setChosenWord(chosenWord);
            ChatMessage msg = messageService.systemMessage(
                    MessageType.ROUND_STARTED,
                    participantService.findParticipantBySessionId(sessionId).getUsername(),
                    String.valueOf(game.getRoundCount() + 1),
                    String.valueOf(game.getTotalRounds())
            );
            chatService.sendChatMessage(roomId, msg);
            // Start hint progression using HintService and pass a callback to handle no remaining hints
            hintService.startHintProgression(roomId, game, () -> handleNoGuess(roomId, game));
        }
    }

    public void correctGuess(String roomId, String guess, String sessionId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Game game = room.getGame();
        if (game.isCorrectGuess(guess)) {
            handleScoring(roomId, game, guess, sessionId);
        }
    }

    private void handleScoring(String roomId, Game game, String guess, String sessionId) {
        String username = participantService.findParticipantBySessionId(sessionId).getUsername();
        int used = game.getHintsUsed();
        int multiplier = Math.max(1, Constants.MULTIPLIER_BASE - used);
        int guesserPoints = Constants.GUESSER_BASE_POINTS * multiplier;
        game.addScore(username, guesserPoints);
        if (game.getCurrentDrawer() != null) {
            String drawerId = game.getCurrentDrawer();
            String drawerName = participantService.findParticipantBySessionId(drawerId).getUsername();
            int drawerPoints = Constants.DRAWER_BASE_POINTS * multiplier;
            game.addScore(drawerName, drawerPoints);
        }
        roomService.broadcastParticipants(roomId);
        // Stop hint progression via HintService
        hintService.stopHintProgression(roomId);
        drawingService.clearCanvas(roomId, new ClearCanvasMessage("system"));
        game.nextRound();
        if (game.isGameOver()) {
            endGameAndStartNew(roomId, game, GAME_ENDED_MSG);
        } else {
            ChatMessage msg = messageService.systemMessage(MessageType.WORD_GUESSED, username);
            chatService.sendChatMessage(roomId, msg);
            updateDrawerAndBroadcast(roomId, game);
        }
    }

    private void handleNoGuess(String roomId, Game game) {
        // Stop hint progression via HintService
        hintService.stopHintProgression(roomId);
        drawingService.clearCanvas(roomId, new ClearCanvasMessage("system"));
        game.nextRound();
        if (game.isGameOver()) {
            endGameAndStartNew(roomId, game, NO_ONE_GUESSED_MSG);
        } else {
            ChatMessage msg = messageService.systemMessage(MessageType.NO_GUESS);
            chatService.sendChatMessage(roomId, msg);
            updateDrawerAndBroadcast(roomId, game);
        }
    }

    private void updateDrawerAndBroadcast(String roomId, Game game) {
        String currentDrawer = game.getCurrentDrawer();
        participantService.getAllParticipants().values().forEach(
                p -> participantService.setDrawer(p.getSessionId(), p.getSessionId().equals(currentDrawer))
        );
        roomService.broadcastParticipants(roomId);
    }

    public void userLeftRoom(String roomId, String sessionId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Game game = room.getGame();
        Participant p = participantService.findParticipantBySessionId(sessionId);
        if (p == null) return;
        boolean wasDrawer = game.isDrawer(sessionId);
        game.removeParticipant(sessionId);
        ChatMessage leaveMsg = messageService.systemMessage(MessageType.PARTICIPANT_LEFT, p.getUsername());
        chatService.sendChatMessage(roomId, leaveMsg);
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
            // Stop hint progression if the current drawer left
            hintService.stopHintProgression(roomId);
            if (!game.getParticipantSessionIds().isEmpty()) {
                String newDrawerId = game.getCurrentDrawer();
                if (newDrawerId != null) {
                    participantService.setDrawer(newDrawerId, true);
                    ChatMessage resetMsg = messageService.systemMessage(
                            MessageType.PREVIOUS_DRAWER_QUIT,
                            participantService.findParticipantBySessionId(newDrawerId).getUsername()
                    );
                    chatService.sendChatMessage(roomId, resetMsg);
                }
            }
        } else {
            String currentDrawerId = game.getCurrentDrawer();
            participantService.getAllParticipants().values().forEach(
                    participant -> participantService.setDrawer(
                            participant.getSessionId(),
                            participant.getSessionId().equals(currentDrawerId)
                    )
            );
        }
        roomService.broadcastParticipants(roomId);
        if (game.getParticipantSessionIds().isEmpty()) {
            roomService.deleteRoom(roomId);
        }
        roomService.broadcastRooms();
    }

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

    private void endGameAndStartNew(String roomId, Game game, String messagePrefix) {
        drawingService.clearCanvas(roomId, new ClearCanvasMessage("system"));
        StringBuilder sb = new StringBuilder(messagePrefix).append(" after ")
                .append(game.getTotalRounds()).append(" rounds. Final scores: ");
        for (String pid : game.getParticipantSessionIds()) {
            String username = participantService.findParticipantBySessionId(pid).getUsername();
            sb.append(username).append("=").append(game.getScore(username)).append("  ");
        }
        ChatMessage m = messageService.systemMessage(MessageType.GAME_ENDED, sb.toString());
        chatService.sendChatMessage(roomId, m);
        handleWinnerAnnouncement(roomId, game);
        // Ensure hint progression is stopped
        hintService.stopHintProgression(roomId);
        scheduleNewGame(roomId, game);
    }

    private void handleWinnerAnnouncement(String roomId, Game game) {
        String singleTopUserSessionId = null;
        int maxScore = -1;
        boolean tie = false;
        for (String pid : game.getParticipantSessionIds()) {
            String uname = participantService.findParticipantBySessionId(pid).getUsername();
            int sc = game.getScore(uname);
            if (sc > maxScore) {
                maxScore = sc;
                singleTopUserSessionId = pid;
                tie = false;
            } else if (sc == maxScore) {
                tie = true;
            }
        }
        if (singleTopUserSessionId != null && !tie) {
            String winnerName = participantService.findParticipantBySessionId(singleTopUserSessionId).getUsername();
            Map<String, Integer> singleMap = new HashMap<>();
            singleMap.put(winnerName, game.getScore(winnerName));
            leaderboardService.saveScores(singleMap);
            ChatMessage winner = messageService.winnerAnnounce(singleTopUserSessionId, winnerName + " is the winner!");
            chatService.sendChatMessage(roomId, winner);
        }
    }

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
                        String.valueOf(game.getTotalRounds()),
                        participantService.findParticipantBySessionId(firstDrawerId).getUsername()
                );
                chatService.sendChatMessage(roomId, newGameMsg);
                roomService.broadcastParticipants(roomId);
                updateDrawerAndBroadcast(roomId, game);
            }
        }, java.util.Date.from(java.time.Instant.now().plusSeconds(10)));
    }
}
