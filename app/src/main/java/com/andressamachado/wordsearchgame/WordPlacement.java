package com.andressamachado.wordsearchgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*********************************************************************************************
 * This class is responsible for building the grid, placing words vertically, horizontally, and
 * diagonally.
 *
 * @author Andressa Machado
 * @version 1.0
 * @since 2020/09/02
 ********************************************************************************************/
public class WordPlacement {
    /**Holds the maximum index value for the 10x10 grid*/
    private final static int INDEX_GRID_MAX_VALUE = 99;
    /**Holds the number of indexes in a row to insure there is space to place a word horizontally*/
    private final static int ROW_SIZE = 10;

    /**Array to hold the letters randomly generated to populate the main grid of the application*/
    private char[] grid = new LettersDAO().randomLettersList();
    /**Array to hold the 0-99 generated indexes in random order. Prevents using randomly generated
     * numbers on the go that can cause repetitions and might not cover the whole grid*/
    private int[] gridIndexes =  new int[100];
    /**Holds the indexes of placed words to check for conflicts when placing new words*/
    private List<Integer> usedIndexesGlobal = new ArrayList<>();
    /**Array to hold the words to be placed in the grid*/
    private List<Word> words = new WordsDAO().wordList();
    /**Array to hold words placed in the grid*/
    private List<Word> usedWords = new ArrayList<>();
    /**Controls the indexes from 0 to 99*/
    private int indexCounter = 0;

    /**
     * Method to build the grid
     *
     * @return Array containing values that will be shown in the grid
     */
    public char[] getCompleteGrid() {
        createGrid();
        placeWordsHorizontally(0,2);
        placeWordsVertically(2,2);
        placeWordsDiagonally(4,2);
        return grid;
    }

    /**
     * Method to generate a list with 0-99 then randomizes it, so we can place the words in
     * different positions every time there is a new game using those indexes.
     */
    private void createGrid(){
        for (int i = 0; i <= INDEX_GRID_MAX_VALUE; i++) {
            gridIndexes[i] = i;
        }

        randomizeGrid();
    }

    /**
     * Method to randomize elements in the list.
     */
    private void randomizeGrid() {
        int index;
        int temp;
        Random r = new Random();
        r.nextInt();

        for (int i = 0; i < gridIndexes.length; i++) {
            index = i + r.nextInt(gridIndexes.length - i);

            temp = gridIndexes[i];
            gridIndexes[i] = gridIndexes[index];
            gridIndexes[index] = temp;
        }
    }

    /**
     * Method to return an index to be used to place a letter.
     *
     * @param indexCounter int value representing the indexes of the grid
     * @return an int value that was saved in the gridIndexes to be used as the index attempt to place
     * word.
     */
    private int getRandomIndexValue(int indexCounter){
        return gridIndexes[indexCounter];
    }

    /**
     * Method to place words horizontally.
     *
     * @param startPosition From the array of words to be used in the game, the position where the
     *                      first word to be placed is located.
     * @param amountOfWords How many words from the array of words to be used in the game should be place
     *                      in horizontal.
     */
    private void placeWordsHorizontally(int startPosition, int amountOfWords) {
        // to hold positions used after placing a word
        List<Integer> usedIndexes = new ArrayList<>();
        // holds the index of the word in its array
        int wordIndex = startPosition;
        // index to be used at the moment of place the word
        int indexToBeUsed;
        int wordSize;
        Word wordTobePlaced;

        // For the amount of words to be placed in this orientation, place them in the grid
        for (int i = 0; i < amountOfWords; ) {
            wordTobePlaced = words.get(wordIndex);
            indexToBeUsed = getRandomIndexValue(indexCounter++);
            wordSize = wordTobePlaced.getContent().length();

            //Passed through every single position in the array and could not place the word.
            //could be for not having enough space or conflict when crossing other words.
            if (indexCounter > INDEX_GRID_MAX_VALUE){
                break;
            }

            // Word do not fit in that row
            if (ROW_SIZE - (indexToBeUsed % ROW_SIZE) < wordSize) {
                continue;
            }

            boolean containIndex= false;
            boolean letterDoesntMatch = false;

            //Loop to check if it will be possible to place the word
            for (int positionInWord = 0; positionInWord < wordSize ; positionInWord++) {
                // Checks if it will conflict with another word horizontally placed
                containIndex = usedIndexes.contains(indexToBeUsed + positionInWord);

                // If the word cross another, check if the intersection letter is the same for both
                // words. I used [indexToBeUsed + positionInWord] because at this point we cannot
                // change the variable and we need to check for the whole word for conflicts.
                letterDoesntMatch = (usedIndexesGlobal.contains(indexToBeUsed + positionInWord)) &&
                        (wordTobePlaced.getContent().charAt(positionInWord) != grid[indexToBeUsed + positionInWord]);

                // cases are true, can not place the word
                if (containIndex||letterDoesntMatch){
                    break;
                }
            }

            // cases are true, can not place the word. Try another position.
            if (containIndex||letterDoesntMatch){
                continue;
            }

            //at this point the position found can be used, holds the initial grid position where the
            //word started to be placed, so whe can check if the user found it later.
            wordTobePlaced.setStartGridPosition(indexToBeUsed);

            //Places word in the grid
            for (int indexPosition = 0; indexPosition < wordSize; indexPosition++) {
                grid[indexToBeUsed] = wordTobePlaced.getContent().toUpperCase().charAt(indexPosition);
                usedIndexes.add(indexToBeUsed);
                usedIndexesGlobal.add(indexToBeUsed++);
            }

            // holds the final position where the word was placed, so we can check if the user found
            //it later.
            wordTobePlaced.setEndGridPosition(indexToBeUsed - 1);

            usedWords.add(wordTobePlaced);
            wordIndex++;
            indexCounter = 0;
            i++;
        }
    }

    /**
     * Method to place words vertically.
     *
     * @param startPosition From the array of words to be used in the game, the position where the
     *                      first word to be placed is located.
     * @param amountOfWords How many words from the array of words to be used in the game should be
     *                      place in horizontal.
     */
    private void placeWordsVertically(int startPosition, int amountOfWords){
        // to hold positions used after placing a word
        List<Integer> usedIndexes = new ArrayList<>();
        // holds the index of the word in its array
        int wordIndex = startPosition;
        // index to be used at the moment of place the word
        int indexToBeUsed;
        int wordSize;
        Word wordTobePlaced;
        indexCounter = 0;

        // For the amount of words to be placed in this orientation, place them in the grid
        for (int i = 0; i < amountOfWords; ) {
            wordTobePlaced = words.get(wordIndex);
            indexToBeUsed = getRandomIndexValue(indexCounter++);
            wordSize = wordTobePlaced.getContent().length();

            //Passed through every single position in the array and could not place the word.
            //could be for not having enough space or conflict when crossing other words.
            if (indexCounter > INDEX_GRID_MAX_VALUE){
                break;
            }

            // if there is a word already placed at this position, try another position
            if (usedIndexes.contains(indexToBeUsed)) {
                continue;
            }

            // word do not fit in that column
            if (ROW_SIZE - (indexToBeUsed / ROW_SIZE) < wordSize) {
                continue;
            }

            boolean containIndex= false;
            int gridIndex = 0;
            boolean letterDoesntMatch = false;

            //Checks if the word could be placed in the vertical
            for (int positionInWord = 0; positionInWord < wordSize ; positionInWord++, gridIndex += 10) {

                // Checks if the actual indexToBeUsed was already used
                containIndex = usedIndexes.contains(indexToBeUsed + gridIndex);
                // if the word cross another, check if the intersection letter is the same for both
                // words.
                letterDoesntMatch = (usedIndexesGlobal.contains(indexToBeUsed + gridIndex)) &&
                        (wordTobePlaced.getContent().charAt(positionInWord) != grid[indexToBeUsed + gridIndex]);

                // If true for both cases, goes out of the loop
                if (containIndex || letterDoesntMatch){
                    break;
                }
            }

            // If true for both cases, try another position
            if (containIndex || letterDoesntMatch){
                continue;
            }

            //at this point the position found can be used, holds the initial grid position where the
            //word started to be placed, so whe can check if the user found it later.
            wordTobePlaced.setStartGridPosition(indexToBeUsed);

            for (int indexPosition = 0; indexPosition < wordSize; indexPosition++) {
                grid[indexToBeUsed] = wordTobePlaced.getContent().toUpperCase().charAt(indexPosition);
                usedIndexes.add(indexToBeUsed);
                usedIndexesGlobal.add(indexToBeUsed);
                indexToBeUsed += 10;
            }

            // holds the final position where the word was placed, so we can check if the user found
            //it later.
            wordTobePlaced.setEndGridPosition(indexToBeUsed - 10);

            usedWords.add(wordTobePlaced);
            wordIndex++;
            indexCounter = 0;
            i++;
        }
    }

    /**
     * Method to place words diagonally.
     *
     * @param startPosition From the array of words to be used in the game, the position where the
     *                      first word to be placed is located.
     * @param amountOfWords How many words from the array of words to be used in the game should be
     *                      place in horizontal.
     */
    private void placeWordsDiagonally(int startPosition, int amountOfWords) {
        // to hold positions used after placing a word
        List<Integer> usedIndexes = new ArrayList<>();
        // holds the index of the word in its array
        int wordIndex = startPosition;
        // index to be used at the moment of place the word
        int indexToBeUsed = 0;
        int wordSize;
        Word wordTobePlaced;
        indexCounter=0;

        // For the amount of words to be placed in this orientation, place them in the grid
        for (int i = 0; i < amountOfWords; ) {
            wordTobePlaced = words.get(wordIndex);

            // Goes to the next word if the first one had not enough space to be placed
            if (indexCounter > INDEX_GRID_MAX_VALUE){
                wordIndex++;
                i++;
                indexCounter = 0;
                continue;
            }

            indexToBeUsed = getRandomIndexValue(indexCounter++);

            // if there is a word already placed at this position, try another position
            if (usedIndexes.contains(indexToBeUsed)) {
                continue;
            }

            wordSize = wordTobePlaced.getContent().length();

            boolean containIndex= false;
            boolean letterDoesntMatch = false;

            // word do not fit in that diagonal
            if (wordSize > ROW_SIZE - (indexToBeUsed / ROW_SIZE) || wordSize > (ROW_SIZE - (indexToBeUsed % ROW_SIZE))) {
                continue;
            }

            //Checks if the word could be placed in the diagonal
            for (int testIndex = 0; testIndex < wordSize ; testIndex++) {

                // Checks if the actual indexToBeUsed was already used
                containIndex = usedIndexes.contains(indexToBeUsed + (testIndex * 11));
                // if the word cross another, check if the intersection letter is the same for both words.
                letterDoesntMatch = (usedIndexesGlobal.contains(indexToBeUsed+  (testIndex * 11)))
                        && (wordTobePlaced.getContent().charAt(testIndex) != grid[indexToBeUsed + (testIndex * 11)]);

                // If true for both cases, goes out of the loop
                if (containIndex || letterDoesntMatch){
                    break;
                }
            }

            // If true for both cases, try another position
            if (containIndex || letterDoesntMatch){
                continue;
            }

            //at this point the position found can be used, holds the initial grid position where the
            //word started to be placed, so whe can check if the user found it later.
            wordTobePlaced.setStartGridPosition(indexToBeUsed);

            for (int indexPosition = 0; indexPosition < wordSize; indexPosition++) {
                grid[indexToBeUsed] = wordTobePlaced.getContent().toUpperCase().charAt(indexPosition);

                usedIndexes.add(indexToBeUsed);
                indexToBeUsed += 11;
                usedIndexesGlobal.add(indexToBeUsed);
            }

            // holds the final position where the word was placed, so we can check if the user found
            //it later.
            wordTobePlaced.setEndGridPosition(indexToBeUsed - 11);

            usedWords.add(wordTobePlaced);
            wordIndex++;
            indexCounter = 0;
            i++;
        }
    }

    /**
     * Method to return the list containing the words that could be placed in the grid.
     *
     * @return List with the words placed in the grid
     */
    public List<Word> getUsedWordsList() {
        return usedWords;
    }
}
