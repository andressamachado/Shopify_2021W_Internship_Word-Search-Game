package com.andressamachado.wordsearchgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements  View.OnTouchListener {
    private static final String TAG = "MAIN ACTIVITY";
    private GridView lettersGripPanel;
    private GridView wordsContainer;
    private Toolbar toolbar;
    private TextView toolbarCounter;

    private Chronometer toolbarChronometer;
    private boolean running;

    private WordPlacement wpd;
    private int wordsCounter;
    private int initialSwipePosition;
    private int finalSwipePosition;
    private float cellWidth;
    private float cellHeight;

    private enum MoveDirection {
        HORIZONTAL,
        NONE,
        VERTICAL
    }
    private MoveDirection direction;
    private float moveX;
    private float moveY;
    private float initialX;
    private float initialY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        wpd = new WordPlacement();
        wpd.getUsedWordsList();

        wordsCounter = 0;

        lettersGripPanel = findViewById(R.id.letters_grid_panel);
        wordsContainer = findViewById(R.id.words_grid_panel);
        toolbar = (Toolbar) findViewById(R.id.application_toolbar);
        toolbarCounter = (TextView) findViewById(R.id.toolbarCounter);
        toolbarChronometer = (Chronometer) findViewById(R.id.toolbarTimer);

        GridViewAdapter adapter = new GridViewAdapter(MainActivity.this, wpd.getCompleteGrid(), this);
        lettersGripPanel.setAdapter(adapter);

        WordGroupAdapter wordsAdapter = new WordGroupAdapter(MainActivity.this, wpd.getUsedWordsList());
        wordsContainer.setAdapter(wordsAdapter);

        //Sets the toolbar title to empty string to not display the name of the project there as it
        //does not have space to display everything we have to display.
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbarCounter.setText(wordsCounter + "/" + wpd.getUsedWordsList().size());

        cellWidth = getResources().getDisplayMetrics().widthPixels / 10.0f;
        ViewGroup.LayoutParams p = lettersGripPanel.getLayoutParams();
        p.height =  getResources().getDisplayMetrics().widthPixels;
        lettersGripPanel.setLayoutParams(p);

        direction = MoveDirection.NONE;
        startChronometer(toolbarChronometer);
    }

    public void startChronometer(View v){
        if (!running){
            toolbarChronometer.start();
            running = true;
        }
    }

    public void pauseChronometer(View v){
        if (running){
            toolbarChronometer.stop();
            running = false;
        }
    }

    public void resetChronometer(View v){

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        cellHeight = lettersGripPanel.getHeight() / 10.0f;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                ViewParent parent = v.getParent();

                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }

                initialSwipePosition = (int) v.getTag();

                initialX = event.getX();
                initialY = event.getY();

                lettersGripPanel.getChildAt(initialSwipePosition).setBackgroundColor(getResources().getColor(R.color.wordBeingSelectedBg));

                Log.e(TAG, ((TextView) v).getText().toString() + " ");
                Log.e("x: ", (initialX + " "));
                Log.e("Y: ", (initialY + " "));
            break;

            case MotionEvent.ACTION_MOVE:
                parent = v.getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                float currentX = event.getX();
                float currentY = event.getY();

                moveX = currentX - initialX;
                moveY = currentY - initialY;

                int currentPosition;

                if(direction == MoveDirection.NONE || direction == MoveDirection.HORIZONTAL) {
                    //moveX positive means user is moving their finger to the right
                    if (moveX > 0 && moveX > cellWidth) {

                        //finding current cell position
                        currentPosition = initialSwipePosition + Math.round(moveX / cellWidth);
                        lettersGripPanel.getChildAt(currentPosition).setBackgroundColor(getResources().getColor(R.color.wordBeingSelectedBg));

                        //horizontal move to the right
                        Log.d(TAG, "moveY: "+moveX);
                        direction = MoveDirection.HORIZONTAL;

                    } else if (moveX < 0 &&  (-1) * moveX > cellWidth){
                        //horizontal move to the left

                        //finding current cell position
                        currentPosition = initialSwipePosition +  Math.round(moveX / cellWidth);
                        lettersGripPanel.getChildAt(currentPosition).setBackgroundColor(getResources().getColor(R.color.wordBeingSelectedBg));
                        direction = MoveDirection.HORIZONTAL;

                    }
                }

                if (direction == MoveDirection.NONE || direction == MoveDirection.VERTICAL) {

                    if (moveY > 0 && moveY > cellHeight) {
                        //finding current cell position
                        currentPosition = initialSwipePosition + Math.round(moveY / cellHeight) * 10;
                        lettersGripPanel.getChildAt(currentPosition).setBackgroundColor(getResources().getColor(R.color.wordBeingSelectedBg));

                        Log.d(TAG, "moveY: "+moveY);
                        direction = MoveDirection.VERTICAL;
                        Log.d(TAG, "down " );

                    } else if (moveY < 0 && (-1) * moveY > cellHeight){
                        //finding current cell position
                        currentPosition = initialSwipePosition + Math.round(moveY / cellHeight) * 10;
                        lettersGripPanel.getChildAt(currentPosition).setBackgroundColor(getResources().getColor(R.color.wordBeingSelectedBg));

                        Log.d(TAG, "moveY: "+moveY);
                        Log.d(TAG, "up: " );
                        direction = MoveDirection.VERTICAL;
                    }
                }

            break;

            case MotionEvent.ACTION_UP:
                boolean isFound = false;

                Log.d(TAG, "initial" + initialSwipePosition);
                Log.d(TAG, "final" + finalSwipePosition);
                Log.d(TAG, "finalSwipePosition: " + finalSwipePosition);

//                finalSwipePosition = initialSwipePosition + Math.round(moveX / cellWidth);
//
//                if (initialSwipePosition == finalSwipePosition){
//                    lettersGripPanel.getChildAt(initialSwipePosition).setBackgroundColor(Color.WHITE);
//                    return true;
//                }

                List<Word> usedWordsList =  wpd.getUsedWordsList();

                int startPosition;
                int endPosition;
                int color;

                if (direction == MoveDirection.HORIZONTAL) {
                    //Checking if word was found
                    finalSwipePosition = initialSwipePosition + Math.round(moveX / cellWidth);

                    startPosition = moveX > 0 ? initialSwipePosition : finalSwipePosition;
                    endPosition = moveX > 0 ? finalSwipePosition : initialSwipePosition;

                    for(int i = 0; i < usedWordsList.size(); i++){
                        if(usedWordsList.get(i).getStartGridPosition() == startPosition && usedWordsList.get(i).getEndGridPosition() == endPosition){
                            usedWordsList.get(i).setFound(true);
                            isFound = true;
                            wordsCounter++;
                            break;
                        }
                    }

                    color = isFound ? getResources().getColor(R.color.wordFoundBg) : Color.WHITE;

                    if (moveX > 0) {
                        for (int i = initialSwipePosition; i <= finalSwipePosition; i++){
                            lettersGripPanel.getChildAt(i).setBackgroundColor(color);
                        }
                    }
                    if (moveX < 0) {
                        for (int i = finalSwipePosition; i <= initialSwipePosition; i++){
                            lettersGripPanel.getChildAt(i).setBackgroundColor(color);
                        }
                    }
                }

                if (direction == MoveDirection.VERTICAL){
                    //Checking if word was found
                    finalSwipePosition = initialSwipePosition + Math.round(moveY / cellWidth) * 10;

                    startPosition = moveY > 0 ? initialSwipePosition : finalSwipePosition;
                    endPosition = moveY > 0 ? finalSwipePosition : initialSwipePosition;

                    for(int i = 0; i < usedWordsList.size(); i++){
                        if(usedWordsList.get(i).getStartGridPosition() == startPosition && usedWordsList.get(i).getEndGridPosition() == endPosition){
                            usedWordsList.get(i).setFound(true);
                            isFound = true;
                            wordsCounter++;
                            break;
                        }
                    }

                    color = isFound ? getResources().getColor(R.color.wordFoundBg) : Color.WHITE;

                    if (moveY > 0) {
                        for (int i = initialSwipePosition; i <= finalSwipePosition; i+=10){
                            lettersGripPanel.getChildAt(i).setBackgroundColor(color);
                        }
                    }

                    if (moveY < 0){
                        for (int i = finalSwipePosition; i <= initialSwipePosition; i+=10){
                            lettersGripPanel.getChildAt(i).setBackgroundColor(color);
                        }
                    }
                }

                initialSwipePosition = -1;
                finalSwipePosition = -1;
                initialX = -1;
                initialY = -1;
                moveX = -1;
                moveY = -1;

                toolbarCounter.setText(wordsCounter + "/" + usedWordsList.size());
                direction = MoveDirection.NONE;
            break;

            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "on cancel" );
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