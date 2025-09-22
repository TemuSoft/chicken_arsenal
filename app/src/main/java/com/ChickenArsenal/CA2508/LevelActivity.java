package com.ChickenArsenal.CA2508;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LevelActivity extends AppCompatActivity {
    private LinearLayout layout_vertical;
    private ImageView home, backward, forward;
    private Button start_game;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
    private Intent intent;
    private String lang;
    private int lastLevelActive, playLevel;
    private LayoutInflater inflate;

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
        lastLevelActive = sharedPreferences.getInt("lastLevelActive", 1);
        playLevel = sharedPreferences.getInt("playLevel", 1);

        setContentView(R.layout.activity_level);

        layout_vertical = findViewById(R.id.layout_vertical);
        inflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        home = findViewById(R.id.home);
        backward = findViewById(R.id.backward);
        forward = findViewById(R.id.forward);
        start_game = findViewById(R.id.start_game);

        home.setOnClickListener(view -> {
            Player.button(soundMute);
            finish();
        });

        backward.setOnClickListener(view -> {
            Player.button(soundMute);

            playLevel--;
            update_UI();
        });

        forward.setOnClickListener(View -> {
            Player.button(soundMute);

            playLevel++;
            update_UI();
        });

        start_game.setOnClickListener(View -> {
            Player.button(soundMute);

            editor.putInt("playLevel", playLevel);
            editor.apply();

            intent = new Intent(LevelActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });

        update_UI();

    }

    private void update_UI() {
        backward.setAlpha(1F);
        backward.setEnabled(true);

        forward.setAlpha(1F);
        forward.setEnabled(true);


        if (playLevel <= 1) {
            playLevel = 1;
            backward.setAlpha(0.3F);
            backward.setEnabled(false);
        }

        if (playLevel >= lastLevelActive) {
            playLevel = lastLevelActive;
            forward.setAlpha(0.3F);
            forward.setEnabled(false);
        }

        update_level_UI();
    }

    private void update_level_UI() {
        layout_vertical.removeAllViews();
        int start_level = playLevel / 20;
        start_level = start_level * 20 + 1;


        int counter = start_level;
        for (int i = 0; i < 4; i++) {
            View horizontal = inflate.inflate(R.layout.horizontal, null);
            LinearLayout layout_horizontal = (LinearLayout) horizontal.findViewById(R.id.horizontal);
            layout_horizontal.removeAllViews();

            for (int j = 0; j < 5; j++) {
                View level_card = inflate.inflate(R.layout.single_level, null);

                LinearLayout layout_card = level_card.findViewById(R.id.layout_card);
                LinearLayout layout_egg = level_card.findViewById(R.id.layout_egg);
                TextView level = level_card.findViewById(R.id.level);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                layout_card.setLayoutParams(params);

                if (counter <= lastLevelActive)
                    layout_egg.setBackgroundResource(R.drawable.level_yellow);
                else layout_egg.setBackgroundResource(R.drawable.level_locked);

                if (counter == playLevel)
                    layout_egg.setBackgroundResource(R.drawable.level_green);

                level.setText(counter + "");

                layout_horizontal.addView(level_card);
                counter++;
            }

            layout_vertical.addView(horizontal);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isMute)
            Player.all_screens.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMute = sharedPreferences.getBoolean("isMute", false);
        if (!isMute)
            Player.all_screens.start();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}