package com.example.drawandguess.service;

import com.example.drawandguess.model.ChatMessage;
import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.model.WordOptions;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final ChatService chatService;
    private final ParticipantService participantService;
    private final RoomService roomService;

    public GameService(
            ChatService chatService,
            ParticipantService participantService,
            RoomService roomService
    ) {
        this.chatService = chatService;
        this.participantService = participantService;
        this.roomService = roomService;
    }

    public WordOptions requestWords(String roomId, String sessionId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return new WordOptions();
        Game game = room.getGame();
        return game.isDrawer(sessionId) ? game.getRandomWords() : new WordOptions();
    }

    public void chooseWord(String roomId, String sessionId, String chosenWord) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Game game = room.getGame();
        if (game.isDrawer(sessionId)) {
            game.setChosenWord(chosenWord);
            ChatMessage msg = new ChatMessage();
            msg.setSender("system");
            msg.setText(
                    "A new round has started. The drawer is " +
                            participantService.findParticipantBySessionId(sessionId).getUsername() + "."
            );
            msg.setType("system");
            chatService.sendChatMessage(roomId, msg);
        }
    }

    public void correctGuess(String roomId, String guess, String sessionId) {
        Room room = roomService.getRoom(roomId);
        if (room == null) return;
        Game game = room.getGame();
        if (game.isCorrectGuess(guess)) {
            ChatMessage msg = new ChatMessage();
            msg.setSender("system");
            msg.setText("A player guessed the word correctly! Starting next round.");
            msg.setType("system");
            chatService.sendChatMessage(roomId, msg);
            game.nextRound();
            String newDrawerId = game.getCurrentDrawer();
            participantService.getAllParticipants().values().forEach(p ->
                    participantService.setDrawer(
                            p.getSessionId(),
                            p.getSessionId().equals(newDrawerId)
                    )
            );
            roomService.broadcastParticipants(roomId);
        }
    }
}