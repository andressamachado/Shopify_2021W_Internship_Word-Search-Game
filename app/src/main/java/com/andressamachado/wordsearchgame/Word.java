package com.andressamachado.wordsearchgame;

public class Word {
    private String word;
    private boolean found;
    private int startGridPosition;
    private int endGridPosition;

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

    public void setStartGridPosition(int startGridPosition) {
        this.startGridPosition = startGridPosition;
    }

    public void setEndGridPosition(int endGridPosition) {
        this.endGridPosition = endGridPosition;
    }

    public int getStartGridPosition(){
        return startGridPosition;
    }

    public int getEndGridPosition(){
        return endGridPosition;
    }
}