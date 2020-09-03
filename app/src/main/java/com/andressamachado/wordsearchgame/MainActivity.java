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

/*********************************************************************************************
 * This class is responsible for the main functionality of the application. It extends
 * AppCompatActivity wto adjusts newer platform features on older devices. It implements
 * NavigationView.OnNavigationItemSelectedListener to handle events on navigation items.
 * It also implements View.OnTouchListener for the touch event that is dispatched to this view
 *
 * @author Andressa Machado
 * @version 1.0
 * @since 2020/09/02
 ********************************************************************************************/
public class MainActivity extends AppCompatActivity implements  View.OnTouchListener, NavigationView.OnNavigationItemSelectedListener {
    /** Holds the activity name for the purpose of debugging with log*/
    private static final String TAG = "MAIN ACTIVITY";

    /**XML layout elements declarations*/
    private GridView lettersGripPanel;
    private GridView wordsContainer;
    private Toolbar toolbar;
    private TextView toolbarCounter;
    private DrawerLayout drawer;
    private Chronometer toolbarChronometer;
    private NavigationView navigationView;

    /**Group of constants used to represent the directions where the user can swipe*/
    private enum MoveDirection {
        NONE,
        HORIZONTAL,
        VERTICAL,
        DIAGONAL
    }
    /**Instance of the WordPlacement that will take care of place the words in the main GridView*/
    private WordPlacement wpd;

    /**Variable used for start a new game when user find every word*/
    private boolean running;
    /**Variables to hold the calculate values for the cell dimensions*/
    private float cellWidth;
    private float cellHeight;
    private double cellDiagonal;
    /**Variables to hold the positions values for the swipe movement*/
    private int initialSwipePosition;
    private int finalSwipePosition;
    /**Holds the number of words found by the user*/
    private int wordsCounter;

    /**Holds the values of the indexes used to paint cells that had a swipe over them */
    private ArrayList<Integer> usedIndexes;
    /**Keeps track of the movement being made by the user while swiping*/
    private MoveDirection direction;
    /**Travelled diagonal distance made by the user in while swiping */
    private double diagonalDistance;
    /**Travelled distance in the X axis */
    private float moveX;
    /**Travelled distance in the Y axis */
    private float moveY;
    /**Initial position in the X axis when swiping*/
    private float initialX;
    /**Initial position in the Y axis when swiping*/
    private float initialY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Initialization of every view used in this activity
        lettersGripPanel = (GridView) findViewById(R.id.letters_grid_panel);
        wordsContainer = (GridView) findViewById(R.id.words_grid_panel);
        toolbar = (Toolbar) findViewById(R.id.application_toolbar);
        toolbarCounter = (TextView) findViewById(R.id.toolbarCounter);
        toolbarChronometer = (Chronometer) findViewById(R.id.toolbarTimer);
        navigationView = findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        startGame();
    }

    /**
     * Method needed for the OnNavigationItemSelected interface. It is going to handle every option
     * listed at the navigation drawer. It is called when an item in the navigation menu is selected.
     *
     * @param item
     * @return
     */
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

    /**
     * Method to reset every configuration to a new game be loaded.
     */
    private void startGame() {
        //Places words in the grid to be found
        wpd = new WordPlacement();
        //Get list of the used words to populate the grid
        wpd.getUsedWordsList();
        wordsCounter = 0;
        usedIndexes = new ArrayList<>();

        resetSwipeRelatedVariables();
        setAdapters();
        initializeToolbar();
        setGridDimensions();
    }

    /**
     * Method to set adapters for the GridView where the letters are placed, and for the words_row layout
     * created to hold the word to be found.
     */
    private void setAdapters() {
        GridViewAdapter adapter = new GridViewAdapter(MainActivity.this, wpd.getCompleteGrid(), this);
        lettersGripPanel.setAdapter(adapter);

        WordGroupAdapter wordsAdapter = new WordGroupAdapter(MainActivity.this, wpd.getUsedWordsList());
        wordsContainer.setAdapter(wordsAdapter);
    }

    /**
     * Method to initialize toolbar and set configuration as the words found counter and the chronometer
     * are placed in the toolbar
     */
    private void initializeToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbarCounter.setText(wordsCounter + "/" + wpd.getUsedWordsList().size());

        //sets navigation drawer
        initializeNavigationDrawer();

        startChronometer(toolbarChronometer);
    }

    /**
     * Method to initialize and set configurations of the Navigation Drawer
     */
    private void initializeNavigationDrawer() {
        //button to open the navigation drawer from the left
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Method to set cell dimensions according to the smartphone orientation and make swiping available
     * for both orientations. Portrait and Landscape.
     */
    private void setGridDimensions() {
        int w, h;

        w = getResources().getDisplayMetrics().widthPixels;
        h = getResources().getDisplayMetrics().heightPixels;

        if (w > h){
            //Get width of grid and sets it to the height to make it a perfect square and facilitate swiping
            cellWidth = getResources().getDisplayMetrics().heightPixels / 10.0f;

            Log.e(TAG, "landscape");
        } else {

            //Get width of grid and sets it to the height to make it a perfect square and facilitate swiping
            cellWidth = getResources().getDisplayMetrics().widthPixels / 10.0f;

            ViewGroup.LayoutParams p = lettersGripPanel.getLayoutParams();
            p.height =  getResources().getDisplayMetrics().widthPixels;
            lettersGripPanel.setLayoutParams(p);

            Log.e(TAG, "portrait");
        }
    }

    /**
     * Start chronometer when user opens a new game.
     *
     * @param v current view
     */
    public void startChronometer(View v){
        if (!running){
            toolbarChronometer.start();
            running = true;
        }
    }

    /**
     * Method to restart the chronometer again if the user press the [new game] button
     *
     * @param v current view
     */
    public void resetChronometer(View v){
        toolbarChronometer.setBase(SystemClock.elapsedRealtime());
        running = false;
    }

    /**
     * Method to sets the main functionality of the game. As the application allows the user to swipe
     * over the words in the grid, the touch functionality to the activity is defined and calculated
     * here.
     *
     * @param v the view the touch event has been dispatched to.
     * @param event MotionEvent object containing full information about the event
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Calculates an approximated value for the height of the cell in the grid
        cellHeight = lettersGripPanel.getHeight() / 10.0f;

        //switch treats the functionality of finding a word according to the movement found. The 3
        //main steps of treating a movement are: ACTION_DOWN, ACTION_MOVE, and ACTION_UP.
        switch (event.getActionMasked()) {
            //user just touched the screen
            case MotionEvent.ACTION_DOWN:
                //Defines the responsibilities for a class that will be a parent of a View. This is
                // the API that a view sees when it wants to interact with its parent.
                ViewParent parent = v.getParent();

                if (parent != null) {
                    //Called when a child does not want this parent and its ancestors to intercept
                    // touch events with ViewGroup#onInterceptTouchEvent(MotionEvent).
                    parent.requestDisallowInterceptTouchEvent(true);
                }

                //sets the initial position of the finger in the screen
                initialSwipePosition = (int) v.getTag();
                //sets the final as well so we can identify if the user just clicked in any letter
                //and had not swipe
                finalSwipePosition = (int) v.getTag();

                //initial positions in both axis
                initialX = event.getX();
                initialY = event.getY();

                //change background of the view to the one when the user is selecting a word
                paintCell(R.drawable.word_being_selected_background, initialSwipePosition, false);

                Log.e(TAG, ((TextView) v).getText().toString() + " ");
                Log.e("x: ", (initialX + " "));
                Log.e("Y: ", (initialY + " "));
            break;

            //user is moving their finger
            case MotionEvent.ACTION_MOVE:
                parent = v.getParent();

                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }

                float currentX = event.getX();
                float currentY = event.getY();

                //calculates the distance traveled in both axis
                moveX = currentX - initialX;
                moveY = currentY - initialY;

                int currentPosition;

                //if there is no direction set yet, check if it is diagonal
                //DIAGONAL MOVEMENT
                if (direction == MoveDirection.NONE || direction == MoveDirection.DIAGONAL){
                    //checks if there was movement in both axis and if it was within the range to be considered diagonal
                    if ((Math.abs(moveX) > 0 && Math.abs(moveY) > 0) && (Math.abs(moveX/moveY) > 0.75 && Math.abs(moveX/moveY) < 1.25)){
                        Log.d(TAG, "DIAGONAL");

                        //Calculates the distance between two points using an application of the Pythagorean theorem
                        diagonalDistance = Math.sqrt(Math.pow(moveX, 2) + (Math.pow(moveY, 2)));
                        //Calculates the diagonal of the cell also using an application of the Pythagorean theorem
                        cellDiagonal = cellWidth * Math.sqrt(2);

                        //if the diagonal distance is larger than the diagonal of the cell, means user
                        //mode their finger longer than just one cell
                        if (diagonalDistance > cellDiagonal){
                            Log.d(TAG, "diagonal movement detected");

                            int diagonalMoveOffset = 0;

                            direction = MoveDirection.DIAGONAL;

                            //Movement: top left to bottom right
                            //walked 10 spaces in the X axis and 1 in the Y axis = 11
                            if (moveX > 0 && moveY > 0){
                                diagonalMoveOffset = 11;
                            }

                            //Movement: bottom left to top right
                            //walked -10 spaces in the X axis and 1 in the Y axis = -9
                            if (moveX > 0 && moveY < 0){
                               diagonalMoveOffset = -9;
                            }

                            //Movement: top right to bottom left
                            //walked 10 spaces in the X axis and -1 in the Y axis = 9
                            if (moveX < 0 && moveY > 0){
                                diagonalMoveOffset = 9;
                            }

                            //Movement: bottom right to top left
                            //walked -10 spaces in the X axis and -1 in the Y axis = -11
                            if (moveX < 0 && moveY < 0){
                                diagonalMoveOffset = -11;
                            }

                            //current position is the initial position + how many cells in the diagonal was passed from the diagonal
                            currentPosition = initialSwipePosition + diagonalMoveOffset * Math.round((float) (diagonalDistance / cellDiagonal));
                            paintCell(R.drawable.word_being_selected_background, currentPosition, false);
                        }
                    }
                }

                //if there is no direction set yet, check if it is Horizontal
                //HORIZONTAL MOVEMENT
                if (direction == MoveDirection.NONE || direction == MoveDirection.HORIZONTAL) {
                    //moveX positive means user is moving their finger to the right
                    if (moveX > 0 && moveX > cellWidth) {
                        //finding current cell position
                        currentPosition = initialSwipePosition + Math.round(moveX / cellWidth);
                        paintCell(R.drawable.word_being_selected_background, currentPosition, false);

                        //horizontal move to the right
                        Log.d(TAG, "moveY: "+moveX);
                        direction = MoveDirection.HORIZONTAL;

                    } else if (moveX < 0 &&  (-1) * moveX > cellWidth){//finding current cell position
                        currentPosition = initialSwipePosition +  Math.round(moveX / cellWidth);
                        paintCell(R.drawable.word_being_selected_background, currentPosition, false);
                        //lettersGripPanel.getChildAt(currentPosition).setBackgroundColor(getResources().getColor(R.color.wordBeingSelectedBg));
                        direction = MoveDirection.HORIZONTAL;
                    }
                }

                //if there is no direction set yet, check if it is Vertical
                //VERTICAL MOVEMENT
                if (direction == MoveDirection.NONE || direction == MoveDirection.VERTICAL) {
                    //if moveY is bigger than zero means the user is moving their finger in the y axis (vertical)
                    if (moveY > 0 && moveY > cellHeight) {
                        //finding current cell position
                        currentPosition = initialSwipePosition + Math.round(moveY / cellHeight) * 10;
                        paintCell(R.drawable.word_being_selected_background, currentPosition, false);

                        Log.d(TAG, "moveY: " + moveY);
                        Log.d(TAG, "down " );
                        direction = MoveDirection.VERTICAL;

                    } else if (moveY < 0 && (-1) * moveY > cellHeight){
                        //finding current cell position
                        currentPosition = initialSwipePosition + Math.round(moveY / cellHeight) * 10;
                        paintCell(R.drawable.word_being_selected_background, currentPosition, false);

                        Log.d(TAG, "moveY: "+moveY);
                        Log.d(TAG, "up " );
                        direction = MoveDirection.VERTICAL;
                    }
                }
            break;

            //user lifts his finger
            //It is here that we identify if the user found the word
            case MotionEvent.ACTION_UP:
                boolean isFound = false;
                int startPosition;
                int endPosition;
                int color;

                Log.d(TAG, "initial" + initialSwipePosition);
                Log.d(TAG, "final" + finalSwipePosition);
                Log.d(TAG, "finalSwipePosition: " + finalSwipePosition);

                List<Word> usedWordsList =  wpd.getUsedWordsList();

                //If the movement registered is diagonal
                if (direction == MoveDirection.DIAGONAL) {
                    int diagonalMoveOffset = 0;

                    //Movement: bottom left to top right
                    //walked -10 spaces in the X axis and 1 in the Y axis = -9
                    if (moveX > 0 && moveY < 0){
                        diagonalMoveOffset = -9;
                    }

                    //Movement: top right to bottom left
                    //walked 10 spaces in the X axis and -1 in the Y axis = 9
                    if (moveX < 0 && moveY > 0){
                        diagonalMoveOffset = 9;
                    }

                    //Movement: top left to bottom right
                    //walked 10 spaces in the X axis and 1 in the Y axis = 11
                    if (moveX > 0 && moveY > 0){
                       diagonalMoveOffset = 11;
                    }

                    //Movement: bottom right to top left
                    //walked -10 spaces in the X axis and -1 in the Y axis = -11
                    if (moveX < 0 && moveY < 0){
                        diagonalMoveOffset = -11;
                    }

                    //Calculates the position where the user stopped swiping
                    finalSwipePosition = initialSwipePosition + diagonalMoveOffset * Math.round((float) (diagonalDistance / cellDiagonal));

                    //Sets startPosition to the initialSwipePosition in the case where the user starts
                    //the swiping by the start of the word [first letter of the word],
                    // otherwise, sets the startPosition to the finalSwipePosition
                    startPosition = (moveX < 0 && moveY > 0) || (moveX > 0 && moveY > 0) ? initialSwipePosition : finalSwipePosition;
                    //Same as the startPosition
                    endPosition = (moveX < 0 && moveY > 0) || (moveX > 0 && moveY > 0) ? finalSwipePosition : initialSwipePosition;

                    //Checks if the the positions in the grid swiped by the user match with the ones saved
                    //in the word object
                    for(int i = 0; i < usedWordsList.size(); i++){
                        if(usedWordsList.get(i).getStartGridPosition() == startPosition && usedWordsList.get(i).getEndGridPosition() == endPosition){
                            usedWordsList.get(i).setFound(true);
                            isFound = true;
                            wordsCounter++;
                            break;
                        }
                    }

                    //If the word was set as found, get the corresponding drawable, otherwise gets the drawable
                    //for words not found.
                    color = isFound ? R.drawable.word_diagonally_found_background : R.drawable.word_not_found_background;

                    //Paint cells
                    //Movement: bottom left to top right
                    if (moveX > 0 && moveY < 0) {
                        for (int i = initialSwipePosition; i >= finalSwipePosition; i-=9){
                            paintCell(color, i, isFound);
                        }
                    }

                    //Movement: top right to bottom left
                    if (moveX < 0 && moveY > 0) {
                        for (int i = initialSwipePosition; i <= finalSwipePosition; i+=9){
                            paintCell(color, i, isFound);
                        }
                    }

                    //Movement: top left to bottom right
                    if (moveX > 0 && moveY > 0) {
                        for (int i = initialSwipePosition; i <= finalSwipePosition; i+=11){
                            paintCell(color, i,isFound);
                        }
                    }

                    //Movement: bottom right to top left
                    if (moveX < 0 && moveY < 0) {
                        for (int i = initialSwipePosition; i >= finalSwipePosition; i-=11){
                            paintCell(color, i, isFound);
                        }
                    }
                }

                //If the movement registered is horizontal
                if (direction == MoveDirection.HORIZONTAL) {
                    //Calculates the position where the user lifted their finger
                    finalSwipePosition = initialSwipePosition + Math.round(moveX / cellWidth);

                    //Sets startPosition to the initialSwipePosition in case the user started to
                    //swipe by the first letter of the word, otherwise, sets is to finalSwipePosition
                    startPosition = moveX > 0 ? initialSwipePosition : finalSwipePosition;
                    endPosition = moveX > 0 ? finalSwipePosition : initialSwipePosition;

                    //Checks if the the positions in the grid swiped by the user match with the ones saved
                    //in the word object
                    for(int i = 0; i < usedWordsList.size(); i++){
                        if(usedWordsList.get(i).getStartGridPosition() == startPosition && usedWordsList.get(i).getEndGridPosition() == endPosition){
                            usedWordsList.get(i).setFound(true);
                            isFound = true;
                            wordsCounter++;
                            break;
                        }
                    }

                    //If the word was set as found, get the corresponding drawable, otherwise gets the drawable
                    //for words not found.
                    color = isFound ? R.drawable.word_horizontally_found_background : R.drawable.word_not_found_background;

                    //Paint cells where the user swiped over
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

                //If the movement registered is vertical
                if (direction == MoveDirection.VERTICAL){
                    //Calculates the position where the user lifted their finger
                    finalSwipePosition = initialSwipePosition + Math.round(moveY / cellWidth) * 10;

                    //Sets startPosition to the initialSwipePosition in case the user started to
                    //swipe by the first letter of the word, otherwise, sets is to finalSwipePosition
                    startPosition = moveY > 0 ? initialSwipePosition : finalSwipePosition;
                    endPosition = moveY > 0 ? finalSwipePosition : initialSwipePosition;

                    //Checks if the the positions in the grid swiped by the user match with the ones saved
                    //in the word object
                    for(int i = 0; i < usedWordsList.size(); i++){
                        if(usedWordsList.get(i).getStartGridPosition() == startPosition && usedWordsList.get(i).getEndGridPosition() == endPosition){
                            usedWordsList.get(i).setFound(true);
                            isFound = true;
                            wordsCounter++;
                            break;
                        }
                    }

                    //If the word was set as found, get the corresponding drawable, otherwise gets the drawable
                    //for words not found.
                    color = isFound ? R.drawable.word_vertically_found_background : R.drawable.word_not_found_background;

                    //Paint cells where the user swiped over
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

                //If the user only clicked in a letter and not swiped, set background to white
                if (initialSwipePosition == finalSwipePosition){
                    paintCell(R.drawable.word_not_found_background, initialSwipePosition, false);
                    return true;
                }

                //Reset swipe movement variables related
                resetSwipeRelatedVariables();

                //Updates the word counter
                toolbarCounter.setText(wordsCounter + "/" + usedWordsList.size());

                //If user found every single word in the game, display the end game alert dialog
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

    /**
     * Method to reset swipe related variables when a swipe movement is finished or a new game starts
     */
    private void resetSwipeRelatedVariables() {
        //All set to -1 as 0 is a valid position
        initialSwipePosition = -1;
        finalSwipePosition = -1;
        initialX = -1;
        initialY = -1;
        moveX = -1;
        moveY = -1;

        direction = MoveDirection.NONE;
    }

    /**
     * Paint cell in different situations, as when the user is swiping over a word, or when the word
     * is found.
     *
     * @param color is a drawable depending of the orientation of the swiping [vertical, horizontal,
     *              or diagonal], and if the word was found or was a mistake.
     * @param gridPosition grid position of the view to have the background changed
     * @param isFound boolean holding the value if the word was found.
     */
    private void paintCell(int color, int gridPosition, boolean isFound) {
        //If the position passed as argument is already registered in the usedIndexes, means user
        //already found a word that contains that index.
        //It avoids that the view`s background is replaced by a white background [white background is
        //for letters not found]
        if (usedIndexes.contains(gridPosition)){
            return;
        }

        lettersGripPanel.getChildAt(gridPosition).setBackground(getResources().getDrawable(color));

        //if word is found add it to the usedIndexes ArrayList
        if(isFound) {
            usedIndexes.add(gridPosition);
        }
    }

    /**
     * Method to build and display a custom alert dialog with a message and a [new game] button when
     * the user found every single word.
     * */
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

        //sets the inflated view to the alert dialog
        builder.setView(customAlertView);
        //shows the alert dialog
        final AlertDialog alertDialog = builder.show();

        //Instantiate the [new game] button
        Button newGameBtn = (Button) customAlertView.findViewById(R.id.new_game_btn);

        //sets functionality to the [new game] button
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