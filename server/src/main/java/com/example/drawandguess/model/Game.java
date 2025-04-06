package com.example.drawandguess.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.drawandguess.config.Constants.TOTAL_ROUNDS;

public class Game {
    private final List<String> participantSessionIds = new ArrayList<>();
    private int currentDrawerIndex = -1;
    private String chosenWordEnglish;
    private String chosenWordHebrew;
    private StringBuilder currentHintBuilder = new StringBuilder();
    private boolean isFirstHint;
    private List<Integer> revealOrder = new ArrayList<>();
    private Map<String, Integer> scores = new HashMap<>();
    private int roundCount = 0;
    private boolean gameOver = false;
    private final int totalRounds = TOTAL_ROUNDS;
    private int hintsUsed = 0;

    public void addParticipant(String sessionId) {
        if (!participantSessionIds.contains(sessionId)) {
            participantSessionIds.add(sessionId);
            if (currentDrawerIndex < 0) currentDrawerIndex = 0;
            scores.put(sessionId, 0);
        }
    }

    public void removeParticipant(String sessionId) {
        int sessionIdIndex = participantSessionIds.indexOf(sessionId);
        if (sessionIdIndex != -1) {
            participantSessionIds.remove(sessionIdIndex);
            scores.remove(sessionId);
            if (sessionIdIndex == currentDrawerIndex) {
                if (participantSessionIds.isEmpty()) currentDrawerIndex = -1;
                else currentDrawerIndex %= participantSessionIds.size();
            } else if (sessionIdIndex < currentDrawerIndex) currentDrawerIndex--;
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

    public void setChosenWord(String chosenWordCombined) {
        String[] parts = chosenWordCombined.split(" : ");
        this.chosenWordEnglish = parts[0];
        this.chosenWordHebrew = parts.length > 1 ? parts[1] : "";
        initializeHint();
    }

    public String getChosenWordEnglish() {
        return chosenWordEnglish;
    }

    public boolean isCorrectGuess(String guess) {
        return (chosenWordEnglish != null && chosenWordEnglish.equalsIgnoreCase(guess)) ||
                (chosenWordHebrew != null && chosenWordHebrew.equalsIgnoreCase(guess));
    }

    public void nextRound() {
        roundCount++;
        if (roundCount >= totalRounds) gameOver = true;
        resetRound();
        moveToNextDrawer();
    }

    public void resetRound() {
        this.chosenWordEnglish = null;
        this.chosenWordHebrew = null;
        this.isFirstHint = true;
        this.currentHintBuilder.setLength(0);
        this.revealOrder.clear();
        this.hintsUsed = 0;
    }

    public void resetGame() {
        roundCount = 0;
        gameOver = false;
        chosenWordEnglish = null;
        chosenWordHebrew = null;
        revealOrder.clear();
        currentHintBuilder.setLength(0);
        currentDrawerIndex = 0;
        scores.clear();
    }

    public int getRoundCount() {
        return roundCount;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public boolean hasMoreHints() {
        return !revealOrder.isEmpty();
    }

    private void initializeHint() {
        int length = chosenWordEnglish.length();
        currentHintBuilder.setLength(0);
        currentHintBuilder.append("_".repeat(length));
        List<Integer> clues = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            clues.add(i);
        }
        Collections.shuffle(clues);
        revealOrder = clues;
        isFirstHint = true;
    }

    public String nextHint() {
        if (isFirstHint || revealOrder.isEmpty()) {
            isFirstHint = false;
            return currentHintBuilder.toString();
        }
        hintsUsed++;
        int nextIndex = revealOrder.remove(0);
        currentHintBuilder.setCharAt(nextIndex, chosenWordEnglish.charAt(nextIndex));
        return currentHintBuilder.toString();
    }

    public String getCurrentHint() {
        return currentHintBuilder.toString();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void addScore(String sessionId, int amount) {
        scores.put(sessionId, scores.getOrDefault(sessionId, 0) + amount);
    }

    public int getScore(String sessionId) {
        return scores.getOrDefault(sessionId, 0);
    }

    public Map<String, Integer> getAllScores() {
        return scores;
    }

    public int getHintsUsed() {
        return hintsUsed;
    }
}