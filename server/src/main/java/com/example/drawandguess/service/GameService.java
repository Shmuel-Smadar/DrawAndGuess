package com.example.drawandguess.service;

import com.example.drawandguess.config.Constants;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.model.WordOptions;
import com.example.drawandguess.model.MessageType;
import com.example.drawandguess.model.ClearCanvasMessage;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.TaskScheduler;

@Service
public class GameService {
    private final ChatService chatService;
    private final ParticipantService participantService;
    private final RoomService roomService;
    private final MessageService messageService;
    private final WordService wordService;
    private final TaskScheduler taskScheduler;
    private final DrawingService drawingService;
    private final HintService hintService;
    private final RoundService roundService;
    private final ScoringService scoringService;
    private final LeaderboardService leaderboardService;

    public GameService(ChatService chatService,
                       ParticipantService participantService,
                       RoomService roomService,
                       MessageService messageService,
                       WordService wordService,
                       TaskScheduler taskScheduler,
                       DrawingService drawingService,
                       HintService hintService,
                       RoundService roundService,
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
        this.roundService = roundService;
        this.leaderboardService = leaderboardService;
        this.scoringService = scoringService;
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
            String drawerUsername = participantService.findParticipantBySessionId(sessionId).getUsername();
            chatService.sendChatMessage(roomId, messageService.systemMessage(
                    MessageType.ROUND_STARTED,
                    drawerUsername,
                    String.valueOf(game.getRoundCount() + 1),
                    String.valueOf(game.getTotalRounds())
            ));
            hintService.startHintProgression(roomId, game, () -> handleNoGuess(roomId, game));
        }
    }
    public void handleNoGuess(String roomId, Game game) {
        hintService.stopHintProgression(roomId);
        drawingService.clearCanvas(roomId, new ClearCanvasMessage("system"));
        ChatMessage msg = messageService.systemMessage(MessageType.NO_GUESS);
        chatService.sendChatMessage(roomId, msg);
        game.nextRound();
        if (game.isGameOver()) {
            endGame(roomId, game);
        } else {
            roundService.updateDrawerAndBroadcast(roomId, game);
        }
    }
    public void handleGuess(String roomId, String guess, String sessionId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Game game = room.getGame();
        if (game.isCorrectGuess(guess)) {
            scoringService.handleScoring(roomId, game, guess, sessionId);
            roomService.broadcastParticipants(roomId);
            hintService.stopHintProgression(roomId);
            drawingService.clearCanvas(roomId, new ClearCanvasMessage("system"));
            game.nextRound();
            String username = participantService.findParticipantBySessionId(sessionId).getUsername();
            chatService.sendChatMessage(roomId, messageService.systemMessage(MessageType.WORD_GUESSED, username));
            if (game.isGameOver()) {
                endGame(roomId, game);
            } else {
                roundService.updateDrawerAndBroadcast(roomId, game);
            }
        }
    }

    public void userLeftRoom(String roomId, String sessionId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
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
        } else {
            String currentDrawerId = game.getCurrentDrawer();
            participantService.getAllParticipants().values().forEach(
                    p2 -> participantService.setDrawer(p2.getSessionId(), p2.getSessionId().equals(currentDrawerId))
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

    private void endGame(String roomId, Game game) {
        drawingService.clearCanvas(roomId, new ClearCanvasMessage("system"));
        StringBuilder sb = new StringBuilder(Constants.GAME_ENDED_MSG).append(" after ")
                .append(game.getTotalRounds()).append(" rounds. Final scores: ");
        for (String pid : game.getParticipantSessionIds()) {
            String username = participantService.findParticipantBySessionId(pid).getUsername();
            sb.append(username).append("=").append(game.getScore(username)).append("  ");
        }
        chatService.sendChatMessage(roomId, messageService.systemMessage(MessageType.GAME_ENDED, sb.toString()));
        String winnerSessionId = scoringService.getWinnerSessionId(game);
        if (winnerSessionId != null) {
            String winnerName = participantService.findParticipantBySessionId(winnerSessionId).getUsername();
            leaderboardService.saveScores(winnerName, game.getScore(winnerName));
            chatService.sendChatMessage(roomId, messageService.winnerAnnounce(winnerSessionId, winnerName + " is the winner!"));
        }
        scheduleNewGame(roomId, game);
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
                roundService.updateDrawerAndBroadcast(roomId, game);
            }
        }, java.util.Date.from(java.time.Instant.now().plusSeconds(10)));
    }
}
