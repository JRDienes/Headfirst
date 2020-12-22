package com.example.headfirstv2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point); // gets screen size to plug in below

        gameView = new GameView(this, point.x, point.y);

        setContentView(gameView); // displays view on screen
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause(); // call the pause function
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume(); // call the resume function from master class
    }
}
