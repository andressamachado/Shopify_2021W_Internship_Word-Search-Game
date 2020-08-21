package com.andressamachado.wordsearchgame;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class WordGroupAdapter extends BaseAdapter {
    private Context context;
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

        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null){
            convertView = inflater.inflate(R.layout.words_row, null);
        }

        TextView textView = convertView.findViewById(R.id.wordToFind);

        textView.setText(words.get(position).getContent());

        return convertView;
    }
}