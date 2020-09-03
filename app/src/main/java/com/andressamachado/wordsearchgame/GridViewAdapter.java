package com.andressamachado.wordsearchgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*********************************************************************************************
 * This class is responsible for inflate the letter_row XML layout and set the appropriate letter
 * to the text_view element of that layout. This element goes to the main GridView at the center of
 * the application where the words are placed to be found.
 *
 * @author Andressa Machado
 * @version 1.0
 * @since 2020/09/02
 ********************************************************************************************/
public class GridViewAdapter extends BaseAdapter {
    /** context of current state of the application/object*/
    private Context context;
    /** used to instantiate the contents of layout XML files into their corresponding View objects*/
    private LayoutInflater inflater;
    MainActivity touchListener;
    /**Array of letters displayed in the grid*/
    private char[] lettersInGrid;

    public GridViewAdapter(Context context, char[] letter,  MainActivity touchListener) {
        this.context = context;
        this.lettersInGrid = letter;
        this.touchListener = touchListener;
    }

    @Override
    public int getCount() {
        return lettersInGrid.length;
    }

    @Override
    public Object getItem(int position) {
        return lettersInGrid[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //Initializes the inflater
        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // Inflates view to the letter_row XML layout (element where the letter will be placed
        // inside the main GridView)
        if(convertView == null){
            convertView = inflater.inflate(R.layout.letter_row, null,false);
        }

        // finds the XML element
        TextView textView = convertView.findViewById(R.id.text_view);
        // Sets the letter to the XML element placed inside the grid
        textView.setText(""+ lettersInGrid[position]);
        // Sets the listener to the view
        textView.setOnTouchListener(touchListener);
        // Sets Tag  associated with this view.
        textView.setTag(position);

        return convertView;
    }
}