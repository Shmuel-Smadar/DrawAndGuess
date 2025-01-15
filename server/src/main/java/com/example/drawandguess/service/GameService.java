// src/main/java/com/example/drawandguess/service/GameService.java
package com.example.drawandguess.service;

import com.example.drawandguess.model.Game;
import com.example.drawandguess.model.Room;
import com.example.drawandguess.model.WordOptions;
import com.example.drawandguess.model.ChatMessage;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final ChatService chatService;
    private final NicknameRegistration nicknameRegistration;

    public GameService(ChatService chatService, NicknameRegistration nicknameRegistration) {
        this.chatService = chatService;
        this.nicknameRegistration = nicknameRegistration;
    }

    public void createRoom(String roomName) {
        String roomId = UUID.randomUUID().toString();
        games.put(roomId, new Game(roomId, roomName));
    }

    public Collection<Room> getRooms() {
        List<Room> result = new ArrayList<>();
        for (Game g : games.values()) {
            result.add(new Room(g.getRoomId(), g.getRoomName()));
        }
        return result;
    }

    public void joinRoom(String sessionId, String roomId) {
        Game game = games.get(roomId);
        if (game != null) {
            String nickname = nicknameRegistration.findNickname(sessionId);
            game.join(sessionId, nickname);
        }
    }

    public void leaveRoom(String sessionId) {
        for (Game g : games.values()) {
            if (g.hasSession(sessionId)) {
                g.leave(sessionId);
                break;
            }
        }
    }

    public WordOptions requestWords(String roomId, String sessionId) {
        Game game = games.get(roomId);
        if (game != null && game.isDrawer(sessionId)) {
            return game.getRandomWords();
        }
        return new WordOptions();
    }

    public void chooseWord(String roomId, String sessionId, String chosenWord) {
        Game game = games.get(roomId);
        if (game != null && game.isDrawer(sessionId)) {
            game.setChosenWord(chosenWord);
            ChatMessage m = new ChatMessage();
            m.setSender("system");
            m.setText("A new round has started. The drawer is " + nicknameRegistration.findNickname(sessionId) + ".");
            m.setType("system");
            chatService.sendChatMessage(roomId, m);
        }
    }

    public void correctGuess(String roomId, String guess, String sessionId) {
        Game g = games.get(roomId);
        if (g != null && g.isCorrectGuess(guess)) {
            ChatMessage m = new ChatMessage();
            m.setSender("system");
            m.setText("A player guessed the word correctly! Starting next round.");
            m.setType("system");
            chatService.sendChatMessage(roomId, m);
            g.nextRound();
        }
    }

    public Map<String, Game> getGames() {
        return games;
    }
}
