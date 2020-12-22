package com.example.headfirstv2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.headfirstv2.GameView.screenRatioX;
import static com.example.headfirstv2.GameView.screenRatioY;

public class Flight {
    boolean isGoingRight = false;
    boolean isGoingLeft = false;
    int x, y, width, height, wingCounter = 0;
    Bitmap flight1, flight2, dead;
    private GameView gameView;

    Flight(GameView gameView, int screenX, Resources res){

        this.gameView = gameView;
        flight1 = BitmapFactory.decodeResource(res, R.drawable.fly1);
        flight2 = BitmapFactory.decodeResource(res, R.drawable.fly2);
        dead = BitmapFactory.decodeResource(res, R.drawable.dead);

        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 6;
        height /= 6;

        width *= (int) screenRatioX;
        height *= (int) screenRatioY;

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false);
        dead = Bitmap.createScaledBitmap(dead, width, height, false);

        x = screenX / 2 - flight1.getWidth()/2;
        y = (int) (256 * screenRatioY);
    }

    Bitmap getFlight () {

        if(wingCounter == 0) {
            wingCounter++;
            return flight1;
        }
        wingCounter--;

        return flight2;
    }

    Rect getCollisionShape(){

        return new Rect(x,y, x+width/2, y+height/2);

    }
    Bitmap getDead(){
        return dead;
    }
}
