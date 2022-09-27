package com.example.carbumper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {

    TextView yourscore;
    SharedPreferences sharedPreferencesScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // remove notification bar
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // setting the orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //initializing the textViews
        yourscore = (TextView) findViewById(R.id.yourscore);

        sharedPreferencesScore  = getSharedPreferences("SHAR_PREF_SCORE_NAME", Context.MODE_PRIVATE);

        //setting the values to the textViews
        yourscore.setText(""+sharedPreferencesScore.getInt("score",0));

        // getting the button and setting onClick listener to playAgain
        findViewById(R.id.playAgain).setOnClickListener(v -> startActivity(new Intent(GameOver.this, GameActivity.class)));

        // getting the button and setting onClick listener to play
        findViewById(R.id.exit).setOnClickListener(v -> startActivity(new Intent(GameOver.this, MainActivity.class)));
    }
}