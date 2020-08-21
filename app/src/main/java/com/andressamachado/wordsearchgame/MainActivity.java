package com.andressamachado.wordsearchgame;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private static final String TAG = "Swipe Position";
    private float x1, x2, y1, y2;
    private static int MIN_DISTANCE = 150;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       this.gestureDetector = new GestureDetector(MainActivity.this, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {

            //Starting to swipe time gesture
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;

            //Ending time swipe gesture
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                //Getting value for horizontal swipe
                float valueX = x2 - x1;

                //Getting value for vertical swipe
                float valueY = y2 - y1;

                if (Math.abs(valueX) > MIN_DISTANCE) {
                    //detect left to right swipe
                    if (x2 > x1) {
                        Toast.makeText(getApplicationContext(), "Right was swiped", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onTouchEvent: RIGHT SWIPE");
                    } else {
                        //detect right to left swipe
                        Toast.makeText(getApplicationContext(), "Left was swiped", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onTouchEvent: LEFT SWIPE");
                    }
                } else if (Math.abs(valueY) > MIN_DISTANCE) {
                    //detect top to bottom swipe
                    if (y2 > y1) {
                        Toast.makeText(getApplicationContext(), "Bottom swipe", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onTouchEvent: BOTTOM SWIPE");
                    } else {
                        //detect bottom to top swipe
                        Toast.makeText(getApplicationContext(), "top swipe", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onTouchEvent: TOP SWIPE");
                    }
                }
            }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
