package com.example.drawandguess.service;

import com.example.drawandguess.config.Constants;
import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.ClearCanvasMessage;
import com.example.drawandguess.model.MessageType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class RoundService {
    private final ChatService chatService;
    private final ParticipantService participantService;
    private final RoomService roomService;
    private final MessageService messageService;
    private final DrawingService drawingService;
    private final LeaderboardService leaderboardService;
    private final TaskScheduler taskScheduler;
    private final HintService hintService;

    public RoundService(ChatService chatService,
                        ParticipantService participantService,
                        RoomService roomService,
                        MessageService messageService,
                        DrawingService drawingService,
                        LeaderboardService leaderboardService,
                        TaskScheduler taskScheduler,
                        HintService hintService) {
        this.chatService = chatService;
        this.participantService = participantService;
        this.roomService = roomService;
        this.messageService = messageService;
        this.drawingService = drawingService;
        this.leaderboardService = leaderboardService;
        this.taskScheduler = taskScheduler;
        this.hintService = hintService;
    }

    public void handleNoGuess(String roomId, Game game) {
        hintService.stopHintProgression(roomId);
        drawingService.clearCanvas(roomId, new ClearCanvasMessage("system"));
        game.nextRound();
        if (game.isGameOver()) {
            endGameAndStartNew(roomId, game, Constants.NO_ONE_GUESSED_MSG);
        } else {
            ChatMessage msg = messageService.systemMessage(MessageType.NO_GUESS);
            chatService.sendChatMessage(roomId, msg);
            updateDrawerAndBroadcast(roomId, game);
        }
    }

    public void updateDrawerAndBroadcast(String roomId, Game game) {
        String currentDrawer = game.getCurrentDrawer();
        participantService.getAllParticipants().values().forEach(
                p -> participantService.setDrawer(p.getSessionId(), p.getSessionId().equals(currentDrawer))
        );
        roomService.broadcastParticipants(roomId);
    }

    public void endGameAndStartNew(String roomId, Game game, String messagePrefix) {
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
            java.util.Map<String, Integer> singleMap = new java.util.HashMap<>();
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
