package com.example.carbumper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
//import android.graphics.Color;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing; // boolean variable to track if the game is playing or not
    private Thread gameThread = null; // the game thread
    private Player player; // adding the player to this class

//    private Enemy[] enemies; // adding our enemy
    private Enemy enemies; // adding our enemies

    private Ambulance ambulance; // create reference to a ambulance

    // objects used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    // road background
    private int screenX, screenY;
    public static float screenRatioX, screenRatioY;
    private Background background1, background2;

    // defining a blast effect object to display
    private Boom boom;

    // adding rules
    int screenXHolder; // a screenX holder
    int countMisses; // to couont the number of misses
    boolean flag; // indicator that the enemy has just entered the game screen
    private boolean isGameOver; // indicator if the game is over

    // adding scores
    int score; // score holder
    int highScore[] = new int[4]; // highscore holder
    SharedPreferences sharedPreferences, sharedPreferencesScore; // share preferences to store high score

    // adding music
    static MediaPlayer clickSound;
    final MediaPlayer killEnemySound;
    final MediaPlayer gameOverSound;
    static MediaPlayer backgroundSound;

    //Class constructor
    public GameView(Context context, int screenX, int screenY) {
        super(context);
        // initializing player object
        player = new Player(context, screenX, screenY);

        // initializing drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        background2.x = screenX;

        // initializing the rules
        this.screenXHolder = screenX;
        countMisses = 0;
        isGameOver = false;

        // single initialization enemy object array
        enemies = new Enemy(context, screenX, screenY);

        // initializing the boom
        boom = new Boom(context);

        // initializing the ambulance class
        ambulance = new Ambulance(context, screenX, screenY);

        // setting the score to 0 initially
        sharedPreferencesScore = context.getSharedPreferences("SHAR_PREF_SCORE_NAME", context.MODE_PRIVATE);
        score = sharedPreferencesScore.getInt("score", 0);
        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME", context.MODE_PRIVATE);
        // initializing the array high scores with the previous values
        highScore[0] = sharedPreferences.getInt("score1", 0);
        highScore[1] = sharedPreferences.getInt("score2",0);
        highScore[2] = sharedPreferences.getInt("score3",0);
        highScore[3] = sharedPreferences.getInt("score4",0);

        // initializing music
        clickSound = MediaPlayer.create(context, R.raw.click);
        killEnemySound = MediaPlayer.create(context, R.raw.enemyhit);
        gameOverSound = MediaPlayer.create(context, R.raw.gameover);
        backgroundSound = MediaPlayer.create(context, R.raw.background);

        // starting the game music
        backgroundSound.start();
    }

    // stop the music on exit
    public static void stopMusic() {
        backgroundSound.stop();
    }

    @Override
    public void run() {
        score = 0;
        while (playing) {

            update(); // to update the frame
            draw(); // to draw the frame
            control(); // to control
        }
    }

    // we will update the coordinate of our characters
    private void update() {

        // updating player position
        player.update();

        // setting boom outside the screen
        boom.setX(-250);
        boom.setY(-250);

        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }

        // setting the flag true when the enemy just enter the screen
        if (enemies.getX() == screenXHolder) {
            flag = true;
        }

        enemies.update(player.getSpeed());
        // if collision occurs to the player
        if (Rect.intersects(player.getDetectCollision(), enemies.getDetectCollision())) {

            // incrementing the score as the player hit enemy
            score++;

            // displaying the blast at that location
            boom.setX(enemies.getX());
            boom.setY(enemies.getY());

            // play the sound when the player hit the enemy
            killEnemySound.start();
            enemies.setX(-300);
        }
        else { // the condition where player misses the enemy
            // if the enemy has just entered
            if (flag) {

                // if players x coordinate is more than the enemies x coordinate
                if (player.getDetectCollision().exactCenterX() >= enemies.getDetectCollision().exactCenterX()){

                    // setting the flag to false
                    flag = false;

                    // assigning the scores to the high score integer array
                    for(int i=0;i<4;i++){
                        if(highScore[i]<score){

                            final int finalI = i;
                            highScore[i] = score;
                            break;
                        }
                    }

                    SharedPreferences.Editor s = sharedPreferencesScore.edit();
                    s.putInt("score", score);
                    s.apply();

                    //storing the scores through shared Preferences
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    for(int i=0;i<4;i++){
                        int j = i+1;
                        e.putInt("score"+j,highScore[i]);
                    }
                    e.apply();
                }
            }


        }

        // updating the ambulance ships coordinates
        ambulance.update(player.getSpeed());

        // check for a collision between player and ambulance
        // if collision occurs with the player
        if (Rect.intersects(player.getDetectCollision(), ambulance.getDetectCollision())) {

            // displaying the blast at that location
            boom.setX(ambulance.getX());
            boom.setY(ambulance.getY());

            // setting playing false to stop the game
            playing = false;
            // setting the isGameOver true as the game is over
            isGameOver = true;

            // stopping the background music
            backgroundSound.stop();
            // play the game over sound
            gameOverSound.start();

            Intent intent = new Intent(getContext(), GameOver.class);
            getContext().startActivity(intent);

            // assigning the scores to the highscore integer array
            for(int i=0;i<4;i++){
                if(highScore[i]<score){

                    final int finalI = i;
                    highScore[i] = score;
                    break;
                }
            }

            SharedPreferences.Editor s = sharedPreferencesScore.edit();
            s.putInt("score", score);
            s.apply();

            //storing the scores through shared Preferences
            SharedPreferences.Editor e = sharedPreferences.edit();
            for(int i=0;i<4;i++){
                int j = i+1;
                e.putInt("score"+j,highScore[i]);
            }
            e.apply();

            score = 0;
        }
    }

    // we will draw the characters to the canvas
    private void draw() {

        // checking if surface is valid
        if (surfaceHolder.getSurface().isValid()) {

            canvas = surfaceHolder.lockCanvas(); // locking the canvas
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            // drawing the player
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);

            // draw the enemy
            canvas.drawBitmap(enemies.getBitmap(), enemies.getX(), enemies.getY(), paint);

            // draw blast image
            canvas.drawBitmap(boom.getBitmap(), boom.getX(), boom.getY(), paint);

            // draw ambulance
            canvas.drawBitmap(ambulance.getBitmap(), ambulance.getX(), ambulance.getY(), paint);

            // draw the score in the screen
            paint.setTextSize(50);
            paint.setColor(Color.WHITE);
            canvas.drawText("Score: " + score, 100, 50, paint);

//             draw game Over when the game is over
            if (isGameOver) {

                Intent intent = new Intent(getContext(), GameOver.class);
                getContext().startActivity(intent);
            }

            // unlocking the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    // will control the frames per seconds drawn
    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // pause the game, we are stopping the gameThread here
    public void pause() {
        // when the game is paused
        // setting the variable to false
        playing = false;
        try {
            // stopping the thread
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    // resume the game, here we are starting the gameThread
    public void resume() {
        // when the game is resumed
        // starting the thread again
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                // when the user presses on the screen
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                // when the user releases the screen
                player.setBoosting();
                break;
        }
        return true;
    }
}
