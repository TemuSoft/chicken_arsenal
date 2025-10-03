package com.ChickenArsenal.CA2508;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {
    private LinearLayout layout_vertical;
    private ImageView home;
    private TextView coin;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
    private Intent intent;
    private String lang;
    private LayoutInflater inflate;
    private int available_coin;
    private int[] eggs = new int[]{
            R.drawable.egg_0, R.drawable.egg_1, R.drawable.egg_2,
            R.drawable.egg_3, R.drawable.egg_4, R.drawable.egg_5,
    };
    private int active_egg;

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
        available_coin = sharedPreferences.getInt("available_coin", 0);
        active_egg = sharedPreferences.getInt("active_egg", 0);

        setContentView(R.layout.activity_shop);


        layout_vertical = findViewById(R.id.layout_vertical);
        inflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        home = findViewById(R.id.home);
        coin = findViewById(R.id.coin);

        coin.setText(available_coin + "");

        home.setOnClickListener(view -> {
            Player.button(soundMute);
            finish();
        });

        update_level_UI();

    }

    private void update_level_UI() {
        layout_vertical.removeAllViews();
        ArrayList<Boolean> bought_status = new ArrayList<>();
        bought_status.add(true);
        for (int i = 1; i < 6; i++)
            bought_status.add(sharedPreferences.getBoolean("bought_status_" + i, false));

        int[] coins = new int[]{0, 5, 10, 20, 30, 40};

        int counter = 0;
        for (int i = 0; i < 2; i++) {
            View horizontal = inflate.inflate(R.layout.horizontal, null);
            LinearLayout layout_horizontal = (LinearLayout) horizontal.findViewById(R.id.horizontal);
            layout_horizontal.removeAllViews();

            for (int j = 0; j < 3; j++) {
                View shop_card = inflate.inflate(R.layout.single_shop, null);

                LinearLayout layout_card = shop_card.findViewById(R.id.layout_card);
                LinearLayout layout_egg = shop_card.findViewById(R.id.layout_egg);
                ImageView egg = shop_card.findViewById(R.id.egg);
                TextView title = shop_card.findViewById(R.id.title);
                TextView required_coin = shop_card.findViewById(R.id.required_coin);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                layout_card.setLayoutParams(params);

                required_coin.setText(coins[counter] + "");

                int finalCounter = counter;
                if (active_egg == counter) {
                    layout_egg.setBackgroundResource(R.drawable.shop_green);
                    title.setText(getResources().getString(R.string.selcted));
                } else if (bought_status.get(counter)) {
                    layout_egg.setBackgroundResource(R.drawable.shop_yellow);
                    title.setText(getResources().getString(R.string.bought));

                    layout_egg.setOnClickListener(View -> {
                        Player.button(soundMute);

                        active_egg = finalCounter;
                        editor.putInt("active_egg", active_egg);
                        editor.apply();

                        update_level_UI();
                    });
                } else {
                    layout_egg.setBackgroundResource(R.drawable.shop_lock);
                    if (coins[counter] <= available_coin) {
                        title.setText(getResources().getString(R.string.can_buy));
                        layout_egg.setOnClickListener(View -> {
                            Player.button(soundMute);

                            available_coin -= coins[finalCounter];
                            active_egg = finalCounter;
                            editor.putBoolean("bought_status_" + finalCounter, true);
                            editor.putInt("active_egg", active_egg);
                            editor.putInt("available_coin", available_coin);
                            editor.apply();

                            update_level_UI();
                        });
                    } else
                        title.setText(getResources().getString(R.string.can_not_buy));
                }

                egg.setImageResource(eggs[counter]);

                layout_horizontal.addView(shop_card);
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