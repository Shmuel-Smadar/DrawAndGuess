package com.example.drawandguess.model;

import java.util.*;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.WordOptions;

public class Game {
    private final String roomId;
    private final String roomName;
    private final List<Participant> participants = new ArrayList<>();
    private String chosenWord;
    private int drawerIndex;
    private final List<String> wordPool = Arrays.asList("Cat","Computer","Pizza","Bicycle","Tree","Car","House","Sun","Moon","Banana");

    public Game(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void join(String sessionId, String nickname) {
        if (nickname == null) {
            nickname = "Player";
        }
        if (!hasSession(sessionId)) {
            participants.add(new Participant(sessionId, nickname, false));
        }
        if (participants.size() == 1) {
            participants.get(0).setDrawer(true);
        }
    }

    public void leave(String sessionId) {
        Iterator<Participant> it = participants.iterator();
        while (it.hasNext()) {
            Participant p = it.next();
            if (p.getSocketID().equals(sessionId)) {
                it.remove();
                break;
            }
        }
        if (drawerIndex >= participants.size()) {
            drawerIndex = 0;
        }
        if (!participants.isEmpty()) {
            for (Participant p : participants) {
                p.setDrawer(false);
            }
            participants.get(drawerIndex).setDrawer(true);
        }
    }

    public boolean hasSession(String sessionId) {
        for (Participant p : participants) {
            if (p.getSocketID().equals(sessionId)) {
                return true;
            }
        }
        return false;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public boolean isDrawer(String sessionId) {
        for (Participant p : participants) {
            if (p.isDrawer() && p.getSocketID().equals(sessionId)) {
                return true;
            }
        }
        return false;
    }

    public WordOptions getRandomWords() {
        Random r = new Random();
        int i1 = r.nextInt(wordPool.size());
        int i2 = r.nextInt(wordPool.size());
        int i3 = r.nextInt(wordPool.size());
        return new WordOptions(wordPool.get(i1), wordPool.get(i2), wordPool.get(i3));
    }

    public void setChosenWord(String w) {
        chosenWord = w;
    }

    public boolean isCorrectGuess(String guess) {
        return chosenWord != null && chosenWord.equalsIgnoreCase(guess);
    }

    public void nextRound() {
        if (!participants.isEmpty()) {
            drawerIndex = (drawerIndex + 1) % participants.size();
            chosenWord = null;
            for (Participant p : participants) {
                p.setDrawer(false);
            }
            participants.get(drawerIndex).setDrawer(true);
        }
    }
}
