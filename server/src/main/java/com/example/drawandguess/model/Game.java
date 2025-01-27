package com.example.drawandguess.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Random;
import java.util.Arrays;
import java.util.HashSet;

public class Game {
    private final List<String> participantSessionIds = new ArrayList<>();
    private int currentDrawerIndex = -1;
    private String chosenWord;
    private StringBuilder currentHintBuilder = new StringBuilder();
    private boolean isFirstHint;
    private final List<String> wordPool = List.of(
            "Cat", "Computer", "Pizza", "Bicycle", "Tree", "Car", "House", "Sun", "Moon", "Banana"
    );
    private List<Integer> revealOrder = new ArrayList<>();
    private Set<Integer> revealedClues = new HashSet<>();
    private String currentHint = "";
    private Map<String, Integer> scores = new HashMap<>();

    public List<Integer> getRevealOrder() {
        return revealOrder;
    }

    public void setRevealOrder(List<Integer> revealOrder) {
        this.revealOrder = revealOrder;
    }

    public Set<Integer> getRevealedClues() {
        return revealedClues;
    }

    public void setRevealedClues(Set<Integer> revealedClues) {
        this.revealedClues = revealedClues;
    }

    public String getCurrentHint() {
        return currentHint;
    }

    public void setCurrentHint(String currentHint) {
        this.currentHint = currentHint;
    }

    public void addParticipant(String sessionId) {
        if (!participantSessionIds.contains(sessionId)) {
            participantSessionIds.add(sessionId);
            if (currentDrawerIndex < 0) currentDrawerIndex = 0;
            scores.put(sessionId, 0);
        }
    }

    public void removeParticipant(String sessionId) {
        int idx = participantSessionIds.indexOf(sessionId);
        if (idx != -1) {
            participantSessionIds.remove(idx);
            scores.remove(sessionId);
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
        initializeHint();
    }
    public boolean isCorrectGuess(String guess) {
        return chosenWord != null && chosenWord.equalsIgnoreCase(guess);
    }
    public void nextRound() {
        chosenWord = null;
        moveToNextDrawer();
        revealOrder = new ArrayList<>();
        revealedClues = new HashSet<>();
        currentHint = "";
    }
    public WordOptions getRandomWords() {
        Random r = new Random();
        return new WordOptions(
                wordPool.get(r.nextInt(wordPool.size())),
                wordPool.get(r.nextInt(wordPool.size())),
                wordPool.get(r.nextInt(wordPool.size()))
        );
    }

    public boolean hasMoreHints() {
        return !revealOrder.isEmpty();
    }
    private void initializeHint() {
        int length = chosenWord.length();
        currentHintBuilder.setLength(0);
        currentHintBuilder.append("_".repeat(length));
        currentHint = currentHintBuilder.toString();
        List<Integer> clues = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            clues.add(i);
        }
        Collections.shuffle(clues);
        revealOrder = clues;
        revealedClues.clear();
        isFirstHint = true;
    }

    public String nextHint() {
        if (isFirstHint || revealOrder.isEmpty()) {
            isFirstHint = false;
            return currentHint;
        }
        int nextIndex = revealOrder.remove(0);
        revealedClues.add(nextIndex);
        currentHintBuilder.setCharAt(nextIndex, chosenWord.charAt(nextIndex));
        currentHint = currentHintBuilder.toString();
        return currentHint;
    }

    public String getChosenWord() {
        return this.chosenWord;
    }

    public void addScore(String sessionId, int amount) {
        scores.put(sessionId, scores.getOrDefault(sessionId, 0) + amount);
    }

    public int getScore(String sessionId) {
        return scores.getOrDefault(sessionId, 0);
    }
}
