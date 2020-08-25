package com.andressamachado.wordsearchgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordPlacement {
    private final static int INDEX_GRID_MAX_VALUE = 99;
    private final static int ROW_SIZE = 10;

    private char[] grid = new LettersDAO().randomLettersList();
    private int[] gridIndexes =  new int[100];
    private List<Integer> usedIndexesGlobal = new ArrayList<>();
    private List<Word> words = new WordsDAO().wordList();
    private List<Word> usedWords = new ArrayList<>();
    private int indexCounter = 0;

    public char[] getCompleteGrid() {
        grid();
        placeWordsHorizontally(0,2);
        placeWordsVertically(2,2);
        return grid;
    }

    private void grid(){
        for (int i = 0; i <= INDEX_GRID_MAX_VALUE; i++) {
            gridIndexes[i] = i;
        }

        randomize();
    }

    private void randomize() {
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

    private int getRandomIndexValue(int indexCounter){
        return gridIndexes[indexCounter];
    }

    private void placeWordsHorizontally(int startPosition, int amountOfWords) {
        List<Integer> usedIndexes = new ArrayList<>();
        int wordIndex = startPosition;
        int indexToBeUsed;
        int wordSize;
        Word wordTobePlaced;

        //TODO salvar as posicoes (tag) onde a palavra esta sendo colocada no obj word

        for (int i = 0; i < amountOfWords; ) {
            wordTobePlaced = words.get(wordIndex);
            indexToBeUsed = getRandomIndexValue(indexCounter++);
            wordSize = wordTobePlaced.getContent().length();

            if (indexCounter > INDEX_GRID_MAX_VALUE){
                break;
            }

            if (ROW_SIZE - (indexToBeUsed % ROW_SIZE) < wordSize) {
                continue;
            }

            boolean containIndex= false;
            boolean letterDoesntMatch = false;

            for (int positionInWord = 0; positionInWord < wordSize ; positionInWord++) {
                containIndex = usedIndexes.contains(indexToBeUsed+positionInWord);

                letterDoesntMatch = (usedIndexesGlobal.contains(indexToBeUsed + positionInWord)) && (wordTobePlaced.getContent().charAt(positionInWord) != grid[indexToBeUsed + positionInWord]);

                if (containIndex||letterDoesntMatch){
                    break;
                }
            }

            if (containIndex||letterDoesntMatch){
                continue;
            }


            wordTobePlaced.setStartGridPosition(indexToBeUsed);

            for (int indexPosition = 0; indexPosition < wordSize; indexPosition++) {
                grid[indexToBeUsed] = wordTobePlaced.getContent().charAt(indexPosition);
                usedIndexes.add(indexToBeUsed);
                usedIndexesGlobal.add(indexToBeUsed++);

            }

            wordTobePlaced.setEndGridPosition(indexToBeUsed - 1);

            usedWords.add(wordTobePlaced);
            wordIndex++;
            indexCounter = 0;
            i++;
        }
    }

    private void placeWordsVertically(int startPosition, int amountOfWords){
        List<Integer> usedIndexes = new ArrayList<>();
        int wordIndex = startPosition;
        int indexToBeUsed;
        int wordSize;
        Word wordTobePlaced;
        indexCounter = 0;

        //for (int i = 0; i< 1;){
        for (int i = 0; i < amountOfWords; ) {
            wordTobePlaced = words.get(wordIndex);
            ///wordTobePlaced = new Word("variable");

            indexToBeUsed = getRandomIndexValue(indexCounter++);
            //indexToBeUsed = 3;
            wordSize = wordTobePlaced.getContent().length();

            if (indexCounter > INDEX_GRID_MAX_VALUE){
                break;
            }

            if (usedIndexes.contains(indexToBeUsed)) {
                continue;
            }

            if (ROW_SIZE - (indexToBeUsed / ROW_SIZE) < wordSize) {
                continue;
            }

            boolean containIndex= false;
            int gridIndex = 0;
            boolean letterDoesntMatch = false;

            for (int positionInWord = 0; positionInWord < wordSize ; positionInWord++, gridIndex += 10) {

                containIndex = usedIndexes.contains(indexToBeUsed + gridIndex);
                letterDoesntMatch = (usedIndexesGlobal.contains(indexToBeUsed + gridIndex)) && (wordTobePlaced.getContent().charAt(positionInWord) != grid[indexToBeUsed + gridIndex]);

                if (containIndex || letterDoesntMatch){
                    break;
                }
            }

            if (containIndex || letterDoesntMatch){
                continue;
            }

            wordTobePlaced.setStartGridPosition(indexToBeUsed);

            for (int indexPosition = 0; indexPosition < wordSize; indexPosition++) {
                grid[indexToBeUsed] = wordTobePlaced.getContent().charAt(indexPosition);
                usedIndexes.add(indexToBeUsed);
                usedIndexesGlobal.add(indexToBeUsed);
                indexToBeUsed += 10;
            }

            wordTobePlaced.setEndGridPosition(indexToBeUsed - 10);

            usedWords.add(wordTobePlaced);
            wordIndex++;
            indexCounter = 0;
            i++;
        }
    }

    public List<Word> getUsedWordsList() {
        return usedWords;
    }
}
