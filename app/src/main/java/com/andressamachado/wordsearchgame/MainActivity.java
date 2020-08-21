package com.andressamachado.wordsearchgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    private GestureDetector gestureDetector;
    GridView lettersGripPanel;
    GridView wordsContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gestureDetector = new GestureDetector(MainActivity.this, new MyGestureListener());
        lettersGripPanel = findViewById(R.id.letters_grid_panel);
        lettersGripPanel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });

        WordPlacement wpd = new WordPlacement();

        GridViewAdapter adapter = new GridViewAdapter(MainActivity.this, wpd.getCompleteGrid());
        lettersGripPanel.setAdapter(adapter);

        wordsContainer = findViewById(R.id.words_grid_panel);

        WordGroupAdapter wordsAdapter = new WordGroupAdapter(MainActivity.this, wpd.getUsedWordsList());
        wordsContainer.setAdapter(wordsAdapter);
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