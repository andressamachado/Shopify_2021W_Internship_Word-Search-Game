package com.andressamachado.wordsearchgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/*********************************************************************************************
 * This class is responsible for inflate the words_row XML layout and set the appropriate text
 * to the word_to_find element of that layout. This element goes to the GridView at the bottom
 * where the words to be searched for are placed.
 *
 * @author Andressa Machado
 * @version 1.0
 * @since 2020/09/02
 ********************************************************************************************/
public class WordGroupAdapter extends BaseAdapter {
    /** context of current state of the application/object*/
    private Context context;
    /** used to instantiate the contents of layout XML files into their corresponding View objects*/
    private LayoutInflater inflater;
    private List<Word> words;

    public WordGroupAdapter(Context context, List<Word> words) {
        this.context = context;
        this.words = words;
    }

    @Override
    public int getCount() {
        return words.size();
    }

    @Override
    public Word getItem(int position) {
        return words.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Initializes the inflater
        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // Sets inflated view to the words_row XML layout (element where the words placed in the grid
        // will be displayed so the user can see the words they needs to look for)
        if(convertView == null){
            convertView = inflater.inflate(R.layout.words_row, null);
        }

        // finds the XML element
        TextView textView = convertView.findViewById(R.id.word_to_find);
        // Sets the word to be searched to the XML element
        textView.setText(words.get(position).getContent());

        return convertView;
    }
}