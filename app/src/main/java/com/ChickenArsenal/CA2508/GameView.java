package com.ChickenArsenal.CA2508;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    private SharedPreferences sharedPreferences;
    private int screenX, screenY;
    private Resources resources;
    private Random random;
    boolean isPlaying = true, game_over, game_won;
    int duration = 1000;
    long game_over_time, game_won_time = 0;

    int score;
    private int xSpeed, ySpeed;
    private Context context;
    int lastLevelActive, playLevel;
    int total_eggs, remain_eggs;

    int active_egg;
    Bitmap cloud, sun, ground, egg, container, basket, danger_0, danger_1, danger_2;
    int c_w, c_h, s_w_h, g_w, g_h, e_w, e_h, b_w, b_h, con_w, con_h;
    int dan_w_0, dan_h_0, dan_w_1, dan_h_1, dan_w_2, dan_h_2;
    int s_x, s_y;
    int game_scree_w;

    ArrayList<ArrayList<Integer>> cloud_data = new ArrayList<>();
    ArrayList<Integer> ground_data = new ArrayList<>();

    public GameView(Context mContext, int scX, int scY, Resources res, int level_amount) {
        super(mContext);
        screenX = scX;
        screenY = scY;
        resources = res;
        context = mContext;
        random = new Random();

        sharedPreferences = context.getSharedPreferences("hienArsen12o8", context.MODE_PRIVATE);
        lastLevelActive = sharedPreferences.getInt("lastLevelActive", 1);
        playLevel = sharedPreferences.getInt("playLevel", 1);
        active_egg = sharedPreferences.getInt("active_egg", 0);

        total_eggs = 5;
        remain_eggs = 2;

        int e = context.getResources().getIdentifier("egg_" + active_egg, "drawable", context.getPackageName());
        cloud = BitmapFactory.decodeResource(res, R.drawable.cloud);
        sun = BitmapFactory.decodeResource(res, R.drawable.sun);
        ground = BitmapFactory.decodeResource(res, R.drawable.ground);
        egg = BitmapFactory.decodeResource(res, e);
        container = BitmapFactory.decodeResource(res, R.drawable.container);
        basket = BitmapFactory.decodeResource(res, R.drawable.basket);

        danger_0 = BitmapFactory.decodeResource(res, R.drawable.danger_0);
        danger_1 = BitmapFactory.decodeResource(res, R.drawable.danger_1);
        danger_2 = BitmapFactory.decodeResource(res, R.drawable.danger_2);

        c_w = cloud.getWidth();
        c_h = cloud.getHeight();
        s_w_h = sun.getWidth();
        g_w = ground.getWidth();
        g_h = ground.getHeight();
        e_w = egg.getWidth();
        e_h = egg.getHeight();
        con_w = container.getWidth();
        con_h = container.getHeight();
        b_w = basket.getWidth();
        b_h = basket.getHeight();

        dan_w_0 = danger_0.getWidth();
        dan_h_0 = danger_0.getHeight();
        dan_w_1 = danger_1.getWidth();
        dan_h_1 = danger_1.getHeight();
        dan_w_2 = danger_2.getWidth();
        dan_h_2 = danger_2.getHeight();

        if (s_w_h > c_w)
            s_w_h = c_w;
        if (s_w_h > c_h)
            s_w_h = c_h;

        game_scree_w = screenY * 2;

        cloud = Bitmap.createScaledBitmap(cloud, c_w, c_h, false);
        sun = Bitmap.createScaledBitmap(sun, s_w_h, s_w_h, false);
        ground = Bitmap.createScaledBitmap(ground, g_w, g_h, false);
        egg = Bitmap.createScaledBitmap(egg, e_w, e_h, false);
        container = Bitmap.createScaledBitmap(container, con_w, con_h, false);
        basket = Bitmap.createScaledBitmap(basket, b_w, b_h, false);
        danger_0 = Bitmap.createScaledBitmap(danger_0, dan_w_0, dan_w_0, false);
        danger_1 = Bitmap.createScaledBitmap(danger_0, dan_w_1, dan_w_1, false);
        danger_2 = Bitmap.createScaledBitmap(danger_0, dan_w_2, dan_w_2, false);

        setSpeed();
        add_cloud_data();
        add_ground_data();
    }

    private void add_ground_data() {
        int gap = 24 * g_w / 419;
        int x = -gap;
        while (x + g_w < game_scree_w) {
            ground_data.add(x);

            x += g_w - gap * 2;
        }
    }

    private void add_cloud_data() {
        int amount = game_scree_w / c_w;
        int last_x = 0;
        for (int i = 0; i < amount; i++) {
            int last_y = 0;
            for (int j = 0; j < 4; j++) {
                int w = random.nextInt(c_w * 5 / 6) + c_w / 6;
                int h = w * c_h / c_w;
                int x = last_x + (c_w - w) / 2;
                int y = last_y + (c_h - h) / 2;

                ArrayList<Integer> data = new ArrayList<>();
                data.add(x);
                data.add(y);
                data.add(w);
                data.add(h);
                data.add(0);
                last_y += c_h;

                if (i == 1 && j == 1)
                    continue;

                cloud_data.add(data);
            }
            last_x += c_w;
        }

        s_x = screenX / 2 - s_w_h / 2;
        s_y = c_h + 2;
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.TRANSPARENT);

        for (int i = 0; i < ground_data.size(); i++) {
            int x = ground_data.get(i);
            canvas.drawBitmap(ground, x, screenY - g_h, paint);
        }

        for (int i = 0; i < cloud_data.size(); i++) {
            int x = cloud_data.get(i).get(0);
            int y = cloud_data.get(i).get(1);
            int w = cloud_data.get(i).get(2);
            int h = cloud_data.get(i).get(3);
            if (x + w < 0 || x > screenX)
                continue;

            Bitmap cl = Bitmap.createScaledBitmap(cloud, w, h, false);
            canvas.drawBitmap(cl, x, y, paint);
        }
        if (s_x + s_w_h > 0 && s_x < screenX)
            canvas.drawBitmap(sun, s_x, s_y, paint);
    }

    private void setSpeed() {
        xSpeed = screenX / 80;
        ySpeed = screenY / 80;
    }

    public void update() {

        invalidate();
    }
}