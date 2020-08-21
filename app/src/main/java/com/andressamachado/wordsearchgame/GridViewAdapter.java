package com.andressamachado.wordsearchgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andressamachado.wordsearchgame.R;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private char[] letter;

    public GridViewAdapter(Context context, char[] letter) {
        this.context = context;
        this.letter = letter;
    }

    @Override
    public int getCount() {
        return letter.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null){
            convertView = inflater.inflate(R.layout.letter_row, null);
        }

        TextView textView = convertView.findViewById(R.id.text_view);

        textView.setText(""+letter[position]);

        return convertView;
    }
}