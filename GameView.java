package com.example.headfirstv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY, score=0;
    public static float screenRatioX, screenRatioY; // make screen sizes compatible for all devices : 1440 x 2560 default on virtual device
    private Flight flight;
    private Random random;
    private Paint paint;
    private SoundPool soundPool;
    private SharedPreferences prefs;
    public Bullet[] bullets;
    private Background background1, background2;
    private GameActivity activity;
    public int counter = 0;
    public int numofbirds = 3;
    private int sound;
    public float speedf = 0;

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);
        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        }else
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound=soundPool.load(activity, R.raw.shoot,1);

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1440f / screenX;
        screenRatioY = 2560f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        flight = new Flight(this, screenX, getResources());

        background2.y = screenY; // placed just beyond screen ends in y axis

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.BLACK);
        bullets = new Bullet[numofbirds];
        random = new Random();


        for (int i = 0;i < numofbirds; i++) {

            Bullet bullet = new Bullet(getResources());
            bullets[i] = bullet;
        }
    }

    @Override
    public void run() {
        while (isPlaying) {

            update();
            draw();
            sleep();
            counter += 1;
            //System.out.println(counter/10);
            numofbirds = 1+counter*17/2000; // every 2 s add one bird
            score = counter/10;
            System.out.println(numofbirds);

        }
    }
    private void update(){

        background1.y -= 10*screenRatioY;
        background2.y -= 10*screenRatioY;

        if (background1.y + background1.background.getHeight() < 0 ){
            background1.y = screenY;
        }
        if (background2.y + background2.background.getHeight() < 0 ){
            background2.y = screenY;
        }
        if (flight.isGoingLeft){
            flight.x -= 30 * screenRatioX;
        }
        if (flight.isGoingRight) {
            flight.x += 30 * screenRatioX;
        }

        if (flight.x < 0){
            flight.x = 0; // does not let dude go too far off the screen
        }
        if (flight.x > screenX){
            flight.x = screenX;
        }

        if (flight.x > screenX - flight.width)
            flight.x = screenX  - flight.width;

        for (Bullet bullet : bullets) {
            speedf += 0.2;
            bullet.speed = (int) (speedf);
            bullet.y -= bullet.speed;

            if (bullet.y + bullet.height < 0){

                /*
                int bound = (int) (70 * screenRatioY);

                bullet.speed = random.nextInt(bound);

                if (bullet.speed < 10 * screenRatioY) {
                    bullet.speed = (int) (30 * screenRatioY);
                }
                */

                bullet.y = screenY+5+random.nextInt(screenY);
                bullet.x = random.nextInt(screenX-bullet.width);
            }

            if (Rect.intersects(bullet.getCollisionShape(), flight.getCollisionShape())){
                //System.out.println("Game over.");
                isGameOver = true;
                return;

            }
        }
    }

    private void draw(){
        if (getHolder().getSurface().isValid()){ // ensures our getsurface obj is successfully initiated
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            if (isGameOver){
                isPlaying = false;
                canvas.drawBitmap(flight.getDead(),flight.x,flight.y, paint);
                saveIfHighScore();
                waitBeforeExiting();
                getHolder().unlockCanvasAndPost(canvas);
                return;
            }
            canvas.drawText("Score: "+score+"", screenX/2f - 250,164*screenRatioY, paint);
            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);
            for (int i = 0; i < 1; i++) {
                //random = new Random();

                for (Bullet bullet : bullets)
                    canvas.drawBitmap(bullet.getBullet(), bullet.x, bullet.y, paint);

                    //canvas.drawBitmap(bullet.getBullet(), random.nextInt(350), bullet.y, paint);
            }

            getHolder().unlockCanvasAndPost(canvas);

        }
    }

    private void waitBeforeExiting() {
        try {
            Thread.sleep(100);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveIfHighScore() {
        if (prefs.getInt("highscore",0) < score){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();

        }
    }

    private void sleep(){
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause(){

        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (!prefs.getBoolean("isMute", false))
                    soundPool.play(sound,1,1,0,0,1);

                if (event.getX() < screenX / 2) {
                    flight.isGoingLeft = true;
                }
                if (event.getX() > screenX / 2){
                    flight.isGoingRight = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                flight.isGoingLeft = false;
                flight.isGoingRight = false;
                break;
        }
        return true;
    }
}
