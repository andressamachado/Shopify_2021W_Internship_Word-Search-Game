package com.andressamachado.wordsearchgame;

public class Word {
    private String word;
    private boolean found;

    public Word(String word) {
        this.word = word;
        this.found = false;
    }

    public String getContent() {
        return word;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }
}