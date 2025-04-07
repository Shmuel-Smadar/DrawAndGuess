package com.example.drawandguess.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hint {
    private String chosenWord;
    private StringBuilder currentHintBuilder;
    private boolean isFirstHint;
    private List<Integer> revealOrder;
    private int hintsUsed;

    public void initialize(String chosenWord) {
        this.chosenWord = chosenWord;
        int length = chosenWord.length();
        currentHintBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            currentHintBuilder.append("_");
        }
        revealOrder = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            revealOrder.add(i);
        }
        Collections.shuffle(revealOrder);
        isFirstHint = true;
        hintsUsed = 0;
    }

    public boolean hasMoreHints() {
        return !revealOrder.isEmpty();
    }

    public String nextHint() {
        if (isFirstHint || revealOrder.isEmpty()) {
            isFirstHint = false;
            return currentHintBuilder.toString();
        }
        hintsUsed++;
        int nextIndex = revealOrder.remove(0);
        currentHintBuilder.setCharAt(nextIndex, chosenWord.charAt(nextIndex));
        return currentHintBuilder.toString();
    }

    public String getCurrentHint() {
        return (currentHintBuilder == null) ? "" : currentHintBuilder.toString();
    }

    public int getHintsUsed() {
        return hintsUsed;
    }

    public void reset() {
        chosenWord = null;
        currentHintBuilder = null;
        revealOrder = null;
        isFirstHint = false;
        hintsUsed = 0;
    }
}
