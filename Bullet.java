package com.example.headfirstv2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.headfirstv2.GameView.screenRatioX;
import static com.example.headfirstv2.GameView.screenRatioY;

public class Bullet {

    public int speed = 20;
    int x, y, width, height, bulletCounter = 2;
    Bitmap bullet1, bullet2, bullet3, bullet4;

    Bullet (Resources res){
        bullet1 = BitmapFactory.decodeResource(res, R.drawable.bird1);
        bullet2 = BitmapFactory.decodeResource(res, R.drawable.bird2);
        bullet3 = BitmapFactory.decodeResource(res, R.drawable.bird3);
        bullet4 = BitmapFactory.decodeResource(res, R.drawable.bird4);

        width = bullet1.getWidth();
        height = bullet1.getHeight();

        width /= 20;
        height /= 20;

        width *= (int) screenRatioX;
        height *= (int) screenRatioY;

        bullet1 = Bitmap.createScaledBitmap(bullet1, width, height, false);
        bullet2 = Bitmap.createScaledBitmap(bullet2, width, height, false);
        bullet3 = Bitmap.createScaledBitmap(bullet3, width, height, false);
        bullet4 = Bitmap.createScaledBitmap(bullet4, width, height, false);

        x = (int) (1440*screenRatioX/2 - width);
        y = (int) (2560*screenRatioY);
    }
    Bitmap getBullet () {
        if (bulletCounter == 1) {
            bulletCounter++;
            return bullet1;
        }
        if (bulletCounter == 2){
            bulletCounter++;
            return bullet2;
        }
        if (bulletCounter == 3){
            bulletCounter++;
            return bullet3;
        }

        bulletCounter = 1;

        return bullet4;
    }
    Rect getCollisionShape(){

        return new Rect(x,y, x+width/2, y+height/2);

    }

}
