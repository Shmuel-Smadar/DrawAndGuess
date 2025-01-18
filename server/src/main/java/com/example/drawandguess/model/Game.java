package com.example.drawandguess.model;

import java.util.*;
import com.example.drawandguess.model.Participant;
import com.example.drawandguess.model.WordOptions;

public class Game {
    private final List<String> participantSessionIds = new ArrayList<>();
    private int currentDrawerIndex = -1;
    private String chosenWord;
    private final List<String> wordPool = List.of(
            "Cat","Computer","Pizza","Bicycle","Tree","Car","House","Sun","Moon","Banana"
    );

    public void addParticipant(String sessionId) {
        if (!participantSessionIds.contains(sessionId)) {
            participantSessionIds.add(sessionId);
            if (currentDrawerIndex < 0) currentDrawerIndex = 0;
        }
    }
    public void removeParticipant(String sessionId) {
        int idx = participantSessionIds.indexOf(sessionId);
        if (idx != -1) {
            participantSessionIds.remove(idx);
            if (idx == currentDrawerIndex) {
                if (participantSessionIds.isEmpty()) {
                    currentDrawerIndex = -1;
                } else {
                    currentDrawerIndex %= participantSessionIds.size();
                }
            } else if (idx < currentDrawerIndex) {
                currentDrawerIndex--;
            }
        }
    }
    public List<String> getParticipantSessionIds() {
        return participantSessionIds;
    }
    public String getCurrentDrawer() {
        if (currentDrawerIndex >= 0 && currentDrawerIndex < participantSessionIds.size()) {
            return participantSessionIds.get(currentDrawerIndex);
        }
        return null;
    }
    public boolean isDrawer(String sessionId) {
        return sessionId.equals(getCurrentDrawer());
    }
    public void moveToNextDrawer() {
        if (!participantSessionIds.isEmpty()) {
            currentDrawerIndex = (currentDrawerIndex + 1) % participantSessionIds.size();
        }
    }
    public void setChosenWord(String chosenWord) {
        this.chosenWord = chosenWord;
    }
    public boolean isCorrectGuess(String guess) {
        return chosenWord != null && chosenWord.equalsIgnoreCase(guess);
    }
    public void nextRound() {
        chosenWord = null;
        moveToNextDrawer();
    }
    public WordOptions getRandomWords() {
        Random r = new Random();
        return new WordOptions(
                wordPool.get(r.nextInt(wordPool.size())),
                wordPool.get(r.nextInt(wordPool.size())),
                wordPool.get(r.nextInt(wordPool.size()))
        );
    }
}