package com.andressamachado.wordsearchgame;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements  View.OnTouchListener {
    private static final String TAG = "MAIN ACTIVITY";
    private GestureDetector gestureDetector;
    private GridView lettersGripPanel;
    private GridView wordsContainer;
    WordPlacement wpd;

    private int initialSwipePosition;
    private int finalSwipePosition;

    private float initialX;
    private float initialY;
    private float cellWidth;
    private boolean horizontalMove;
    float moveX;
    float moveY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gestureDetector = new GestureDetector(MainActivity.this, new MyGestureListener());
        lettersGripPanel = findViewById(R.id.letters_grid_panel);
//        lettersGripPanel.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);
//                return false;
//            }
//        });

        wpd = new WordPlacement();
        wpd.getUsedWordsList();

        GridViewAdapter adapter = new GridViewAdapter(MainActivity.this, wpd.getCompleteGrid(), this);
        lettersGripPanel.setAdapter(adapter);

        wordsContainer = findViewById(R.id.words_grid_panel);

        WordGroupAdapter wordsAdapter = new WordGroupAdapter(MainActivity.this, wpd.getUsedWordsList());
        wordsContainer.setAdapter(wordsAdapter);

        cellWidth = getResources().getDisplayMetrics().widthPixels / 10;
        Log.d(TAG, "WIDTH: " + cellWidth);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialSwipePosition = (int) v.getTag();

                initialX = event.getX();
                initialY = event.getY();

                Log.e(TAG, ((TextView) v).getText().toString() + " ");
                Log.e("x: ", (initialX + " "));
                Log.e("Y: ", (initialY + " "));
                Log.e("x: ", (event.getX() + " "));
                Log.e("Y: ", (event.getY() + " "));
                break;

            case MotionEvent.ACTION_MOVE:
                float currentX = event.getX();
                float currentY = event.getY();

                 moveX = currentX - initialX;
                 moveY = currentY - initialY;

                int currentPosition;

                //moveX positive means user is moving their finger to the right
                if (moveX > 0) {
                    boolean newCell;

                    //finding current cell position
                    currentPosition = initialSwipePosition + Math.round(moveX / cellWidth);

                    //horizontal move to the right
                    horizontalMove = true;

                } else {
                    //horizontal move to the left
                    horizontalMove = true;

                }
                break;
            case MotionEvent.ACTION_UP:
                //Checking if word was found

                finalSwipePosition = initialSwipePosition + Math.round(moveX / cellWidth);

                Log.d(TAG, "finalSwipePosition: " + finalSwipePosition);

                List<Word> usedWordsList =  wpd.getUsedWordsList();

                for(int i = 0; i < usedWordsList.size(); i++){

                    if(usedWordsList.get(i).getStartGridPosition() == initialSwipePosition && usedWordsList.get(i).getEndGridPosition() == finalSwipePosition){
                        usedWordsList.get(i).setFound(true);
                        break;
                    }

                }
                Log.d(TAG, "onTouch: a" );

                break;

            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return true;
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        private static final String TAG = "Main Activity" ;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }



        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                float diffY = event2.getY() - event1.getY();
                float diffX = event2.getX() - event1.getX();

            for(int _numChildren = lettersGripPanel.getCount() - 1; _numChildren > 0 ;--_numChildren){
                LinearLayout _child = (LinearLayout) lettersGripPanel.getChildAt(_numChildren);

                int textViews =  _child.getChildCount();

                TextView firstText = (TextView) _child.getChildAt(0);
                // Log.d(TAG, "found " + firstText.getText().toString());
                if (_child instanceof LinearLayout) {
                    continue;
                }

                Rect _bounds = new Rect();
                _child.getHitRect(_bounds);
                if (_bounds.contains((int)event1.getX(), (int)event1.getY())){
                    Log.d(TAG, "found ");
                   //  Log.d(TAG, ((TextView) _child).getText().toString());
                    break;
                }
                // In View = true!!!
            }

            Log.d(TAG, "event1.getX(): "+ event1.getX());
            Log.d(TAG, "event1.gety(): "+ event1.getY());

            Log.d(TAG, "event2.getX(): "+ event2.getX());
            Log.d(TAG, "event2.gety(): "+ event2.getY());

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                }
                return true;
        }

        private void onSwipeLeft() {
            Toast.makeText(getApplicationContext(), "onSwipeLeft", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onSwipeLeft ");
        }

        private void onSwipeRight() {
            Toast.makeText(getApplicationContext(), "onSwipeRight ", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onSwipeRight");
        }

        private void onSwipeTop() {
            Toast.makeText(getApplicationContext(), "onSwipeTop", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onSwipeTop");
        }

        private void onSwipeBottom() {
            Toast.makeText(getApplicationContext(), "onSwipeBottom", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onSwipeBottom");
        }

    }
}