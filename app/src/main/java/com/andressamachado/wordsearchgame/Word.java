package com.andressamachado.wordsearchgame;

/*********************************************************************************************
 * This class is responsible for creating a Word object.
 *
 * @author Andressa Machado
 * @version 1.0
 * @since 2020/09/02
 ********************************************************************************************/
public class Word {
    //word itself
    private String word;
    //if the word was found by the user
    private boolean isFound;
    //initial grid position where the word was placed. This value, with the help of the endGridPosition,
    // is used to check if the user found the word in the grid
    private int startGridPosition;
    //final grid position where the word was placed. This value, with the help of the startGridPosition,
    // is used to check if the user found the word in the grid
    private int endGridPosition;

    public Word(String word) {
        this.word = word;
        this.isFound = false;
    }

    public String getContent() {
        return word;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean found) {
        this.isFound = found;
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