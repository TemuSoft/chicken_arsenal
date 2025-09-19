package com.ChickenArsenal.CA2508;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {
    private LinearLayout layout_canvas, layout_ui, layout_blur, layout_dialog;

    private ImageView home_dialog, chicken;
    private Button status, next_again;
    private TextView egg_left;

    private ImageView home;
    private ProgressBar progressBar;
    private TextView level;

    private LayoutInflater inflate;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
    private Intent intent;
    private String lang;
    private AlertDialog.Builder builder;
    private Random random;
    private Handler handler;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(getResources().getColor(R.color.trans));
        sharedPreferences = getSharedPreferences("hienArsen12o8", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        lang = sharedPreferences.getString("lang", "");

        setContentView(R.layout.activity_game);

        builder = new AlertDialog.Builder(this);
        random = new Random();
        handler = new Handler();

        layout_canvas = findViewById(R.id.layout_canvas);
        inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout_ui = findViewById(R.id.layout_ui);
        layout_blur = findViewById(R.id.layout_blur);
        layout_dialog = findViewById(R.id.layout_dialog);

        home_dialog = findViewById(R.id.home_dialog);
        chicken = findViewById(R.id.chicken);
        status = findViewById(R.id.status);
        next_again = findViewById(R.id.next_again);
        egg_left = findViewById(R.id.egg_left);

        home = findViewById(R.id.home);
        progressBar = findViewById(R.id.progressBar);
        level = findViewById(R.id.level);

        layout_canvas.removeAllViews();
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        int w = point.x;
        int h = point.y;
        gameView = new GameView(this, w, h, getResources(), 0);
        gameView.setLayoutParams(new LinearLayout.LayoutParams(w, h));
        layout_canvas.addView(gameView);

        layout_canvas.setOnTouchListener(this);
        reloading_UI();

        layout_blur.setVisibility(GONE);
        layout_dialog.setVisibility(GONE);
        layout_ui.setVisibility(VISIBLE);
        layout_canvas.setVisibility(VISIBLE);

        level.setText(getResources().getString(R.string.level) + " " + gameView.playLevel);

        home.setOnClickListener(View -> {
            Player.button(soundMute);

            finish();
        });
    }

    private void reloading_UI() {
        Runnable r = new Runnable() {
            public void run() {
                if (gameView.isPlaying) {
                    if (gameView.game_over && gameView.game_over_time + gameView.duration < System.currentTimeMillis())
                        game_over();

                    if (gameView.game_won && gameView.game_won_time + gameView.duration < System.currentTimeMillis())
                        game_won();

                    if (!gameView.game_over && !gameView.game_won) gameView.update();

                    progressBar.setProgress(gameView.remain_eggs * 100 / gameView.total_eggs);

                    reloading_UI();
                }
            }
        };
        handler.postDelayed(r, 20);
    }

    private void game_over() {
        gameView.isPlaying = false;

        layout_ui.setVisibility(GONE);
        layout_blur.setVisibility(VISIBLE);
        layout_dialog.setVisibility(VISIBLE);

        status.setText(R.string.level_faild);
        egg_left.setText(R.string.eggs_left + " " + gameView.remain_eggs);
        chicken.setImageResource(R.drawable.chicken_stand);

        next_again.setOnClickListener(View -> {
            Player.button(soundMute);

            intent = new Intent(GameActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void game_won() {
        gameView.isPlaying = false;

        layout_ui.setVisibility(GONE);
        layout_blur.setVisibility(VISIBLE);
        layout_dialog.setVisibility(VISIBLE);

        status.setText(R.string.level_completed);
        egg_left.setText(R.string.eggs_left + " " + gameView.remain_eggs);
        chicken.setImageResource(R.drawable.chicken_fly);

        next_again.setOnClickListener(View -> {
            Player.button(soundMute);
            gameView.playLevel++;
            if (gameView.playLevel > gameView.lastLevelActive)
                gameView.lastLevelActive = gameView.playLevel;

            editor.putInt("lastLevelActive", gameView.lastLevelActive);
            editor.putInt("playLevel", gameView.playLevel);
            editor.apply();

            intent = new Intent(GameActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.isPlaying = false;
        if (!isMute) Player.all_screens.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.isPlaying = true;
        isMute = sharedPreferences.getBoolean("isMute", false);
        if (!isMute) Player.all_screens.start();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        return;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                processActionDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                processActionMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                processActionUp(x, y);
                break;
        }
        return true;
    }

    private void processActionDown(int x, int y) {

    }

    private void processActionUp(int xp, int yp) {
        Rect clicked = new Rect(xp, yp, xp, yp);

    }

    private void processActionMove(int x, int y) {

    }
}