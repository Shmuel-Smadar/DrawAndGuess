package com.example.drawandguess.model;

import static com.example.drawandguess.config.GameConstants.TOTAL_ROUNDS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private final List<String> participantSessionIds = new ArrayList<>();
    private int currentDrawerIndex = -1;
    private String chosenWordEnglish;
    private String chosenWordHebrew;
    private final Hint hintManager = new Hint();
    private Map<String, Integer> scores = new HashMap<>();
    private int roundCount = 0;
    private boolean gameOver = false;

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
        hintManager.initialize(chosenWordEnglish);
    }

    public boolean isCorrectGuess(String guess) {
        return (chosenWordEnglish != null && chosenWordEnglish.equalsIgnoreCase(guess)) ||
                (chosenWordHebrew != null && chosenWordHebrew.equalsIgnoreCase(guess));
    }

    public void nextRound() {
        roundCount++;
        if (roundCount >= TOTAL_ROUNDS) gameOver = true;
        resetRound();
        moveToNextDrawer();
    }

    public void resetRound() {
        this.chosenWordEnglish = null;
        this.chosenWordHebrew = null;
        hintManager.reset();
    }

    public void resetGame() {
        roundCount = 0;
        gameOver = false;
        chosenWordEnglish = null;
        chosenWordHebrew = null;
        moveToNextDrawer();
        scores.clear();
    }

    public int getRoundCount() {
        return roundCount;
    }

    public boolean hasMoreHints() {
        return hintManager.hasMoreHints();
    }

    public String nextHint() {
        return hintManager.nextHint();
    }

    public String getCurrentHint() {
        return hintManager.getCurrentHint();
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
        return hintManager.getHintsUsed();
    }
}
