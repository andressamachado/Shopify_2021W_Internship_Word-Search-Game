package com.andressamachado.wordsearchgame;

import java.util.Random;

public class LettersDAO {

    private char getRandomChar() {
        Random r = new Random();
        return (char)(r.nextInt(25) + 'A');
    }

    public char[] randomLettersList(){
        int GRID_SIZE = 100;
        char[] letters = new char[GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            letters[i] = getRandomChar();
        }

        return letters;
    }
}
