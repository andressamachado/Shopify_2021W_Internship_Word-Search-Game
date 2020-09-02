package com.andressamachado.wordsearchgame;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  View.OnTouchListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MAIN ACTIVITY";
    private GridView lettersGripPanel;
    private GridView wordsContainer;
    private Toolbar toolbar;
    private TextView toolbarCounter;
    private DrawerLayout drawer;
    private Chronometer toolbarChronometer;
    private NavigationView navigationView;

    private enum MoveDirection {
        NONE,
        HORIZONTAL,
        VERTICAL,
        DIAGONAL
    }
    private WordPlacement wpd;

    private boolean running;
    private float cellWidth;
    private float cellHeight;
    private double cellDiagonal;
    private int initialSwipePosition;
    private int finalSwipePosition;
    private int wordsCounter;

    private ArrayList<Integer> usedIndexes;
    private MoveDirection direction;
    private double diagonalDistance;
    private float moveX;
    private float moveY;
    private float initialX;
    private float initialY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        lettersGripPanel = (GridView) findViewById(R.id.letters_grid_panel);
        wordsContainer = (GridView) findViewById(R.id.words_grid_panel);
        toolbar = (Toolbar) findViewById(R.id.application_toolbar);
        toolbarCounter = (TextView) findViewById(R.id.toolbarCounter);
        toolbarChronometer = (Chronometer) findViewById(R.id.toolbarTimer);
        navigationView = findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        startGame();
    }

    public boolean onNavigationItemSelected( MenuItem item) {
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId()) {
            case R.id.drawer_github:
                //Sets the intent to be passed as argument to the startActivity method
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/andressamachado"));
                //Goes to the API web page
                startActivity(browserIntent);
                break;
            case R.id.drawer_linkedin:
                //Sets the intent to be passed as argument to the startActivity method
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/andressa-machado-59705792/"));
                //Goes to the API web page
                startActivity(browserIntent);
                break;
        }

        //Close the navigation drawer
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    private void startGame() {
        wpd = new WordPlacement();
        wpd.getUsedWordsList();
        wordsCounter = 0;
        usedIndexes = new ArrayList<>();

        resetSwipeRelatedVariables();
        setAdapters();
        initializeToolbar();
        setGridDimensions();
    }

    private void setAdapters() {
        GridViewAdapter adapter = new GridViewAdapter(MainActivity.this, wpd.getCompleteGrid(), this);
        lettersGripPanel.setAdapter(adapter);

        WordGroupAdapter wordsAdapter = new WordGroupAdapter(MainActivity.this, wpd.getUsedWordsList());
        wordsContainer.setAdapter(wordsAdapter);
    }

    private void initializeToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbarCounter.setText(wordsCounter + "/" + wpd.getUsedWordsList().size());

        initializeNavigationDrawer();

        startChronometer(toolbarChronometer);
    }

    private void initializeNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setGridDimensions() {
        int w, h;

        w = getResources().getDisplayMetrics().widthPixels;
        h = getResources().getDisplayMetrics().heightPixels;

        if (w > h){
            //Get width of grid and sets it to the height to make it a perfect square and facilitate swiping
            cellWidth = getResources().getDisplayMetrics().heightPixels / 10.0f;

//            ViewGroup.LayoutParams p = lettersGripPanel.getLayoutParams();
//            p.width =  getResources().getDisplayMetrics().heightPixels;
//            lettersGripPanel.setLayoutParams(p);

            Log.e(TAG, "DEITADAAAAH");
        } else {

            //Get width of grid and sets it to the height to make it a perfect square and facilitate swiping
            cellWidth = getResources().getDisplayMetrics().widthPixels / 10.0f;

            ViewGroup.LayoutParams p = lettersGripPanel.getLayoutParams();
            p.height =  getResources().getDisplayMetrics().widthPixels;
            lettersGripPanel.setLayoutParams(p);

            Log.e(TAG, "LEVANTADAAAAH");
        }

    }

    public void startChronometer(View v){
        if (!running){
            toolbarChronometer.start();
            running = true;
        }
    }

    public void resetChronometer(View v){
        toolbarChronometer.setBase(SystemClock.elapsedRealtime());
        running = false;
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
                finalSwipePosition = (int) v.getTag();

                initialX = event.getX();
                initialY = event.getY();

                paintCell(R.drawable.word_being_selected_background, initialSwipePosition, false);

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


                if (direction == MoveDirection.NONE || direction == MoveDirection.DIAGONAL){
                    if ((Math.abs(moveX) > 0 && Math.abs(moveY) > 0) && (Math.abs(moveX/moveY) > 0.75 && Math.abs(moveX/moveY) < 1.25)){
                        Log.d(TAG, "DIAGONAL");
                        //quando andou eh maior que a distancia entre os dois centros de celula na diagonal
                        //cellwidth * sqrt(2)
                        diagonalDistance = Math.sqrt(Math.pow(moveX, 2) + (Math.pow(moveY, 2)));
                        cellDiagonal = cellWidth * Math.sqrt(2);

                        if (diagonalDistance > cellDiagonal){
                            Log.d(TAG, "DIAGONAL ANDADA ");
                            int diagonalMoveOffset = 0;

                            direction = MoveDirection.DIAGONAL;

                            if (moveX > 0 && moveY > 0){
                                diagonalMoveOffset = 11;
                            }

                            if (moveX > 0 && moveY < 0){
                               diagonalMoveOffset = -9;
                            }

                            if (moveX < 0 && moveY > 0){
                                diagonalMoveOffset = 9;
                            }

                            if (moveX < 0 && moveY < 0){
                                diagonalMoveOffset = -11;
                            }

                            currentPosition = initialSwipePosition + diagonalMoveOffset * Math.round((float) (diagonalDistance / cellDiagonal));
                            paintCell(R.drawable.word_being_selected_background, currentPosition, false);
                        }
                    }
                }

                if (direction == MoveDirection.NONE || direction == MoveDirection.HORIZONTAL) {
                    //moveX positive means user is moving their finger to the right
                    if (moveX > 0 && moveX > cellWidth) {

                        //finding current cell position
                        currentPosition = initialSwipePosition + Math.round(moveX / cellWidth);
                        paintCell(R.drawable.word_being_selected_background, currentPosition, false);
                        //lettersGripPanel.getChildAt(currentPosition).setBackgroundColor(getResources().getColor(R.color.wordBeingSelectedBg));

                        //horizontal move to the right
                        Log.d(TAG, "moveY: "+moveX);
                        direction = MoveDirection.HORIZONTAL;

                    } else if (moveX < 0 &&  (-1) * moveX > cellWidth){
                        //horizontal move to the left

                        //finding current cell position
                        currentPosition = initialSwipePosition +  Math.round(moveX / cellWidth);
                        paintCell(R.drawable.word_being_selected_background, currentPosition, false);
                        //lettersGripPanel.getChildAt(currentPosition).setBackgroundColor(getResources().getColor(R.color.wordBeingSelectedBg));
                        direction = MoveDirection.HORIZONTAL;
                    }
                }

                if (direction == MoveDirection.NONE || direction == MoveDirection.VERTICAL) {

                    if (moveY > 0 && moveY > cellHeight) {
                        //finding current cell position
                        currentPosition = initialSwipePosition + Math.round(moveY / cellHeight) * 10;
                        paintCell(R.drawable.word_being_selected_background, currentPosition, false);
                        //lettersGripPanel.getChildAt(currentPosition).setBackgroundColor(getResources().getColor(R.color.wordBeingSelectedBg));

                        Log.d(TAG, "moveY: "+moveY);
                        direction = MoveDirection.VERTICAL;
                        Log.d(TAG, "down " );

                    } else if (moveY < 0 && (-1) * moveY > cellHeight){
                        //finding current cell position
                        currentPosition = initialSwipePosition + Math.round(moveY / cellHeight) * 10;
                        paintCell(R.drawable.word_being_selected_background, currentPosition, false);
                        //lettersGripPanel.getChildAt(currentPosition).setBackgroundColor(getResources().getColor(R.color.wordBeingSelectedBg));

                        Log.d(TAG, "moveY: "+moveY);
                        Log.d(TAG, "up: " );
                        direction = MoveDirection.VERTICAL;
                    }
                }
            break;

            case MotionEvent.ACTION_UP:
                boolean isFound = false;
                int startPosition;
                int endPosition;
                int color;

                Log.d(TAG, "initial" + initialSwipePosition);
                Log.d(TAG, "final" + finalSwipePosition);
                Log.d(TAG, "finalSwipePosition: " + finalSwipePosition);

                List<Word> usedWordsList =  wpd.getUsedWordsList();

                if (direction == MoveDirection.DIAGONAL) {
                    int diagonalMoveOffset = 0;

                    //1
                    if (moveX > 0 && moveY < 0){
                        diagonalMoveOffset = -9;
                    }
                    //2
                    if (moveX < 0 && moveY > 0){
                        diagonalMoveOffset = 9;
                    }
                    //3
                    if (moveX > 0 && moveY > 0){
                       diagonalMoveOffset = 11;
                    }
                    //4
                    if (moveX < 0 && moveY < 0){
                        diagonalMoveOffset = -11;
                    }

                    finalSwipePosition = initialSwipePosition + diagonalMoveOffset * Math.round((float) (diagonalDistance / cellDiagonal));

                    startPosition = (moveX < 0 && moveY > 0) || (moveX > 0 && moveY > 0) ? initialSwipePosition : finalSwipePosition;
                    endPosition = (moveX < 0 && moveY > 0) || (moveX > 0 && moveY > 0) ? finalSwipePosition : initialSwipePosition;

                    for(int i = 0; i < usedWordsList.size(); i++){
                        if(usedWordsList.get(i).getStartGridPosition() == startPosition && usedWordsList.get(i).getEndGridPosition() == endPosition){
                            usedWordsList.get(i).setFound(true);
                            isFound = true;
                            wordsCounter++;
                            break;
                        }
                    }

                    color = isFound ? R.drawable.word_diagonally_found_background : R.drawable.word_not_found_background;
                    //1
                    if (moveX > 0 && moveY < 0) {
                        for (int i = initialSwipePosition; i >= finalSwipePosition; i-=9){
                            paintCell(color, i, isFound);
                        }
                    }
                    //2
                    if (moveX < 0 && moveY > 0) {
                        for (int i = initialSwipePosition; i <= finalSwipePosition; i+=9){
                            paintCell(color, i, isFound);
                        }
                    }
                    //3
                    if (moveX > 0 && moveY > 0) {
                        for (int i = initialSwipePosition; i <= finalSwipePosition; i+=11){
                            paintCell(color, i,isFound);
                        }
                    }
                    //4
                    if (moveX < 0 && moveY < 0) {
                        for (int i = initialSwipePosition; i >= finalSwipePosition; i-=11){
                            paintCell(color, i, isFound);
                        }
                    }
                }

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

                    color = isFound ? R.drawable.word_horizontally_found_background : R.drawable.word_not_found_background;

                    if (moveX > 0) {
                        for (int i = initialSwipePosition; i <= finalSwipePosition; i++){
                            paintCell(color, i, isFound);
                        }
                    }
                    if (moveX < 0) {
                        for (int i = finalSwipePosition; i <= initialSwipePosition; i++){
                            paintCell(color, i, isFound);
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

                    color = isFound ? R.drawable.word_vertically_found_background : R.drawable.word_not_found_background;

                    if (moveY > 0) {
                        for (int i = initialSwipePosition; i <= finalSwipePosition; i+=10){
                            paintCell(color, i, isFound);
                        }
                    }

                    if (moveY < 0){
                        for (int i = finalSwipePosition; i <= initialSwipePosition; i+=10){
                            paintCell(color, i, isFound);
                        }
                    }
                }

                if (initialSwipePosition == finalSwipePosition){
                    paintCell(R.drawable.word_not_found_background, initialSwipePosition, false);
                    return true;
                }

                resetSwipeRelatedVariables();

                toolbarCounter.setText(wordsCounter + "/" + usedWordsList.size());

                if (wordsCounter == wpd.getUsedWordsList().size()){
                    buildAndDisplayAlertDialog();
                    toolbarChronometer.stop();
                    return true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "on cancel" );
            break;
        }
        return true;
    }

    private void resetSwipeRelatedVariables() {
        initialSwipePosition = -1;
        finalSwipePosition = -1;
        initialX = -1;
        initialY = -1;
        moveX = -1;
        moveY = -1;

        direction = MoveDirection.NONE;
    }

    private void paintCell(int color, int gridPosition, boolean isFound) {
        if (usedIndexes.contains(gridPosition)){
            return;
        }
        lettersGripPanel.getChildAt(gridPosition).setBackground(getResources().getDrawable(color));

        if(isFound) {
            usedIndexes.add(gridPosition);
        }
    }

    private void buildAndDisplayAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogBg);
        //Inflate the custom dialog to the view
        final View customAlertView = getLayoutInflater().inflate(R.layout.custom_dialog, null, false);

        //sets title
        TextView title = customAlertView.findViewById(R.id.alert_title);
        title.setText(R.string.game_final_title);

        //sets message
        TextView instructions = customAlertView.findViewById(R.id.alert_subtitle);
        instructions.setText(R.string.game_final_subtitle);

        //sets positive button to close the dialog
        builder.setView(customAlertView);
        final AlertDialog alertDialog = builder.show();

        Button newGameBtn = (Button) customAlertView.findViewById(R.id.new_game_btn);

        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChronometer(toolbarChronometer);
                startGame();
                alertDialog.dismiss();
            }
        });
    }
}