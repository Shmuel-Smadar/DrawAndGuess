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

@Service
public class GameService {
    private final ChatService chatService;
    private final ParticipantService participantService;
    private final RoomService roomService;
    private final MessageService messageService;
    private final WordService wordService;
    private final DrawingService drawingService;
    private final HintService hintService;
    private final RoundService roundService;
    private final ScoringService scoringService;

    public GameService(
            ChatService chatService,
            ParticipantService participantService,
            RoomService roomService,
            MessageService messageService,
            WordService wordService,
            DrawingService drawingService,
            HintService hintService,
            RoundService roundService,
            ScoringService scoringService
    ) {
        this.chatService = chatService;
        this.participantService = participantService;
        this.roomService = roomService;
        this.messageService = messageService;
        this.wordService = wordService;
        this.drawingService = drawingService;
        this.hintService = hintService;
        this.roundService = roundService;
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
            ChatMessage msg = messageService.systemMessage(
                    MessageType.ROUND_STARTED,
                    participantService.findParticipantBySessionId(sessionId).getUsername(),
                    String.valueOf(game.getRoundCount() + 1),
                    String.valueOf(game.getTotalRounds())
            );
            chatService.sendChatMessage(roomId, msg);
            hintService.startHintProgression(roomId, game, () -> roundService.handleNoGuess(roomId, game));
        }
    }

    public void correctGuess(String roomId, String guess, String sessionId) {
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
            if (game.isGameOver()) {
                roundService.endGameAndStartNew(roomId, game, Constants.GAME_ENDED_MSG);
            } else {
                ChatMessage msg = messageService.systemMessage(MessageType.WORD_GUESSED, username);
                chatService.sendChatMessage(roomId, msg);
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
}
