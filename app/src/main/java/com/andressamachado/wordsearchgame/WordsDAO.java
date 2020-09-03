package com.andressamachado.wordsearchgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*********************************************************************************************
 * This class is responsible for holding the list of words to be placed in the grid to be found
 * by the user, and return this list in a different order every time there is a new game
 *
 * @author Andressa Machado
 * @version 1.0
 * @since 2020/09/02
 ********************************************************************************************/
public class WordsDAO {

    public List<Word> wordList() {
        List<Word> words = new ArrayList<>( Arrays.asList( new Word("swift"), new Word("kotlin"), new Word("objectivec"), new Word("variable"), new Word("java"),new Word("mobile")));
        Collections.shuffle(words);
        return words;
    }
}