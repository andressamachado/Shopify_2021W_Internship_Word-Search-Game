package com.andressamachado.wordsearchgame;

import java.util.Random;

/*********************************************************************************************
 * This class is responsible for randomly generate a char to populate the main grid of the
 * application.
 *
 * @author Andressa Machado
 * @version 1.0
 * @since 2020/09/02
 ********************************************************************************************/
public class LettersDAO {

    /**
     * Method to generate a random char to populate the application`s main grid
     *
     * @return a char generated randomly
     */
    private char getRandomChar() {
        Random r = new Random();
        //Starting from the letter A in the ASCII table, generates a random number between 0-25 to get
        //alphabet letters.
        return (char)(r.nextInt(25) + 'A');
    }

    /**
     * Method to populate the array that will be used to fill the application`s main grid
     *
     * @return an array containing every letter to populate the main grid
     */
    public char[] randomLettersList(){
        //The application grid contains 100 indexes (10 x 10)
        int GRID_SIZE = 100;
        //Array to hold the letters
        char[] letters = new char[GRID_SIZE];

        //populates the array
        for (int i = 0; i < GRID_SIZE; i++) {
            letters[i] = getRandomChar();
        }

        return letters;
    }
}
