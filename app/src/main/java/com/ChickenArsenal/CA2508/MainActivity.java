package com.ChickenArsenal.CA2508;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ImageView shop, sound, music;
    private Button play;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
    private Intent intent;
    private String lang;

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

        setContentView(R.layout.activity_main);

        Player.all_screens(this, R.raw.all_screens);
        Player.button(this, R.raw.button);

        shop = findViewById(R.id.shop);
        sound = findViewById(R.id.sound);
        music = findViewById(R.id.music);
        play = findViewById(R.id.play);

        shop.setOnClickListener(view -> {
            Player.button(soundMute);
            intent = new Intent(MainActivity.this, ShopActivity.class);
            startActivity(intent);
        });

        play.setOnClickListener(view -> {
            Player.button(soundMute);
            intent = new Intent(MainActivity.this, LevelActivity.class);
            startActivity(intent);
        });

        sound.setOnClickListener(View -> {
            Player.button(soundMute);

            soundMute = !soundMute;
            if (soundMute) {
                Player.button.pause();
            } else {
                Player.button.start();
            }

            editor.putBoolean("soundMute", soundMute);
            editor.apply();

            update_UI();
        });

        music.setOnClickListener(View -> {
            Player.button(soundMute);

            isMute = !isMute;
            if (isMute) {
                Player.all_screens.pause();
            } else {
                Player.all_screens.start();
            }

            editor.putBoolean("isMute", isMute);
            editor.apply();

            update_UI();
        });
    }

    private void update_UI() {
        sound.setAlpha(0.3F);
        music.setAlpha(0.3F);

        if (!soundMute)
            sound.setAlpha(1F);

        if (!isMute)
            music.setAlpha(1F);
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