package com.example.carbumper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    // play button
    private ImageButton buttonPlay;
    // high score button
    private ImageButton buttonScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // remove notification bar
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // setting the orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // getting the button and setting onClick listener to play
        findViewById(R.id.buttonPlay).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GameActivity.class)));
        // getting the button and setting onClick listener to high score
        findViewById(R.id.buttonScore).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HighScore.class)));
    }
}