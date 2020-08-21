package com.andressamachado.wordsearchgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WordsDAO {

    public List<Word> wordList() {
        List<Word> words = new ArrayList<>( Arrays.asList( new Word("swift"), new Word("kotlin"), new Word("objectivec"), new Word("variable"), new Word("java"),new Word("mobile")));
        Collections.shuffle(words);
        return words;
    }
}