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
    int total_eggs, remain_eggs, total_danger_type, danger_amount;

    int active_egg;
    Bitmap cloud, sun, ground, egg, container, basket, danger_0, danger_1, danger_2;
    int c_w, c_h, s_w_h, g_w, g_h, e_w, e_h, basket_w, basket_h, con_w, con_h;
    int dan_w_0, dan_h_0, dan_w_1, dan_h_1, dan_w_2, dan_h_2;
    int s_y, ground_y, floor_y, con_y, basket_y;
    int danger_init_x, gap, game_scree_last_x;
    int move_left = -1;
    int in_col_clouds = 3, removed_cloud;

    ArrayList<Bitmap> clouds = new ArrayList<>();
    ArrayList<Bitmap> dangers = new ArrayList<>();
    ArrayList<ArrayList<Integer>> danger_data = new ArrayList<>();
    ArrayList<ArrayList<Integer>> cloud_data = new ArrayList<>();
    ArrayList<Integer> sun_data = new ArrayList<>();
    ArrayList<Integer> basket_data = new ArrayList<>();
    ArrayList<Integer> container_data = new ArrayList<>();
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

        total_eggs = playLevel / 3 + 2;
        remain_eggs = 0;

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
        basket_w = basket.getWidth();
        basket_h = basket.getHeight();

        dan_w_0 = danger_0.getWidth();
        dan_h_0 = danger_0.getHeight();
        dan_w_1 = danger_1.getWidth();
        dan_h_1 = danger_1.getHeight();
        dan_w_2 = danger_2.getWidth();
        dan_h_2 = danger_2.getHeight();

        if (s_w_h > c_w) s_w_h = c_w;
        if (s_w_h > c_h) s_w_h = c_h;

        ground_y = screenY - g_h;
        floor_y = ground_y + g_h * 7 / 16;

        con_y = floor_y - con_h;
        gap = con_w / 2;

        cloud = Bitmap.createScaledBitmap(cloud, c_w, c_h, false);
        sun = Bitmap.createScaledBitmap(sun, s_w_h, s_w_h, false);
        ground = Bitmap.createScaledBitmap(ground, g_w, g_h, false);
        egg = Bitmap.createScaledBitmap(egg, e_w, e_h, false);
        container = Bitmap.createScaledBitmap(container, con_w, con_h, false);
        basket = Bitmap.createScaledBitmap(basket, basket_w, basket_h, false);
        danger_0 = Bitmap.createScaledBitmap(danger_0, dan_w_0, dan_h_0, false);
        danger_1 = Bitmap.createScaledBitmap(danger_1, dan_w_1, dan_h_1, false);
        danger_2 = Bitmap.createScaledBitmap(danger_2, dan_w_2, dan_h_2, false);

        for (int i = 0; i < 7; i++) {
            Bitmap cl = Bitmap.createScaledBitmap(cloud, (c_w * (1 + i)) / 7, (c_h * (1 + i)) / 7, false);
            clouds.add(cl);
        }

        dangers.add(danger_0);
        dangers.add(danger_1);
        dangers.add(danger_2);

        setSpeed();

        add_danger_data();
    }

    private void add_danger_data() {
        total_danger_type = (playLevel < 10) ? 2 : 3;
        danger_amount = Math.min(playLevel + 7, 15);

        if (danger_init_x == 0) {
            container_data.add(screenX / 2 - con_w / 2);
            danger_init_x = container_data.get(0) + con_w + screenX / 3 + random.nextInt(screenX / 3);
        } else {
            danger_init_x += screenX;
            container_data.add(danger_init_x);
            danger_init_x += con_w + screenX / 3 + random.nextInt(screenX / 3);
        }

        int[] ww = new int[]{dan_w_0, dan_w_1, dan_w_2};
        int[] hh = new int[]{dan_h_0, dan_h_1, dan_h_2};
        int[][] overlap_h = new int[][]{
                {47, 177},
                {40, 126},
                {25, 221},
        };

        boolean basket_added = false;
        while (danger_amount > 1) {
            int index = random.nextInt(3);
            int max;
            if (index == 0) max = 3;
            else if (index == 1) max = 5;
            else max = 2;
            int count = random.nextInt(max) + 1;
            if (count > danger_amount) count = danger_amount;

            danger_amount -= count;

            int gap_h = (overlap_h[index][0] * hh[index]) / overlap_h[index][1];

            ArrayList<Integer> data = new ArrayList<>();
            data.add(danger_init_x);
            data.add(floor_y - hh[index] * count + gap_h * (count - 1));
            data.add(count);
            data.add(index);
            data.add(hh[index] - gap_h);
            danger_data.add(data);

            danger_init_x += ww[index] + gap * 2;

            if (random.nextInt(3) == 1 && !basket_added) {
                basket_data.add(danger_init_x);
                basket_y = floor_y - basket_h;
                danger_init_x += basket_w + gap * 2;
                basket_added = true;
            }
        }

        if (!basket_added) {
            basket_data.add(danger_init_x);
            basket_y = floor_y - basket_h;
            danger_init_x += basket_w + gap * 2;
        }
        game_scree_last_x = danger_init_x;
        add_ground_data(true);
        add_cloud_data(true);
    }

    private void add_ground_data(boolean multiple) {
        int x = ground_data.isEmpty() ? 0 : ground_data.get(ground_data.size() - 1) + g_w;

        int gap = 24 * g_w / 419;
        x -= gap;

        if (multiple) {
            while (x + g_w < game_scree_last_x) {
                ground_data.add(x);
                x += g_w - gap * 2;
            }
        } else {
            ground_data.add(x);
        }
    }

    private void add_cloud_data(boolean multiple) {
        int last_x = cloud_data.isEmpty() ? 0 : cloud_data.get(cloud_data.size() - 1).get(4) + c_w;

        if (multiple) {
            int sun_x = 0;
            int sun_y = 0;
            int amount = screenX * 3 / 2;
            for (int i = 0; i < amount; i++) {
                int last_y = 0;
                for (int j = 0; j < 3; j++) {
                    int index = random.nextInt(clouds.size());
                    int w = clouds.get(index).getWidth();
                    int h = clouds.get(index).getHeight();
                    int x = last_x + random.nextInt(c_w - w + 1);
                    int y = last_y + random.nextInt(c_h - h + 1);

                    ArrayList<Integer> data = new ArrayList<>();
                    data.add(x);
                    data.add(y);
                    data.add(w);
                    data.add(h);
                    data.add(last_x + c_w * 3 / 2);
                    data.add(index);
                    last_y += c_h * 3 / 2;

                    if (i == 1 && j == 1) {
                        sun_x = last_x + c_w / 2 - s_w_h / 2;
                        sun_y = last_y + c_h / 2 - s_w_h / 2;
                        continue;
                    }

                    if (random.nextInt(5) != 1)
                        cloud_data.add(data);
                }
                last_x += c_w * 3 / 2;
            }

            sun_data.add(sun_x);
            s_y = sun_y;
        } else {
            int last_y = 0;
            for (int j = 0; j < 3; j++) {
                int index = random.nextInt(clouds.size());
                int w = clouds.get(index).getWidth();
                int h = clouds.get(index).getHeight();
                int x = last_x + (c_w - w) / 2;
                int y = last_y + (c_h - h) / 2;

                ArrayList<Integer> data = new ArrayList<>();
                data.add(x);
                data.add(y);
                data.add(w);
                data.add(h);
                data.add(last_x + c_w * 3 / 2);
                data.add(index);
                last_y += c_h * 3 / 2;

                cloud_data.add(data);
            }
        }
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.TRANSPARENT);

        for (int i = 0; i < ground_data.size(); i++) {
            int x = ground_data.get(i);
            if (x + g_w < 0 || x > screenX) continue;

            canvas.drawBitmap(ground, x, ground_y, paint);
        }

        for (int i = 0; i < cloud_data.size(); i++) {
            int x = cloud_data.get(i).get(0);
            int y = cloud_data.get(i).get(1);
            int w = cloud_data.get(i).get(2);
            int h = cloud_data.get(i).get(3);
            int index = cloud_data.get(i).get(5);
            if (x + w < 0 || x > screenX) continue;

            canvas.drawBitmap(clouds.get(index), x, y, paint);
        }

        for (int i = 0; i < sun_data.size(); i++) {
            int x = sun_data.get(i);
            if (x + s_w_h < 0 || x > screenX) continue;
            canvas.drawBitmap(sun, x, s_y, paint);
        }

        for (int i = 0; i < basket_data.size(); i++) {
            int x = basket_data.get(i);
            if (x + basket_w < 0 || x > screenX) continue;
            canvas.drawBitmap(basket, x, basket_y, paint);
        }

        for (int i = 0; i < container_data.size(); i++) {
            int x = container_data.get(i);
            if (x + con_w < 0 || x > screenX) continue;
            canvas.drawBitmap(container, x, con_y, paint);
        }

        int[] ww = new int[]{dan_w_0, dan_w_1, dan_w_2};
        int[] hh = new int[]{dan_h_0, dan_h_1, dan_h_2};
        for (int i = 0; i < danger_data.size(); i++) {
            int x = danger_data.get(i).get(0);
            int y = danger_data.get(i).get(1);
            int count = danger_data.get(i).get(2);
            int index = danger_data.get(i).get(3);
            int height = danger_data.get(i).get(4);

            if (x + ww[index] < 0 || x > screenX) continue;

            y = floor_y - hh[index];

            for (int j = count - 1; j >= 0; j--) {
                canvas.drawBitmap(dangers.get(index), x, y, paint);
                y -= height;
            }
        }
    }

    private void setSpeed() {
        xSpeed = screenX / 80;
        ySpeed = screenY / 80;
    }

    public void update() {
        move_bitmaps();
        remove_left_off_screen();

        invalidate();
    }

    private void remove_left_off_screen() {
        int[] ww = new int[]{dan_w_0, dan_w_1, dan_w_2};
        for (int i = 0; i < danger_data.size(); i++) {
            int index = danger_data.get(i).get(3);

            if (danger_data.get(i).get(0) + ww[index] < 0) {
                danger_data.remove(i);
                break;
            }
        }

        if (!danger_data.isEmpty()) {
            int s = danger_data.size() - 1;
            if (danger_data.get(s).get(0) + ww[danger_data.get(s).get(3)] < screenX)
                add_danger_data();
        }

        for (int i = 0; i < ground_data.size(); i++) {
            if (ground_data.get(i) + g_w < 0) {
                ground_data.remove(i);
                add_ground_data(false);
                break;
            }
        }

        for (int i = 0; i < cloud_data.size(); i++) {
            if (cloud_data.get(i).get(0) + cloud_data.get(i).get(2) < 0) {
                cloud_data.remove(i);
                removed_cloud++;
                if (removed_cloud == in_col_clouds) {
                    add_cloud_data(false);
                    removed_cloud = 0;
                }
                break;
            }
        }


        for (int i = 0; i < sun_data.size(); i++) {
            if (sun_data.get(i) + s_w_h < 0) {
                sun_data.remove(i);
                break;
            }
        }

        for (int i = 0; i < basket_data.size(); i++) {
            if (basket_data.get(i) + basket_w < 0) {
                basket_data.remove(i);
                break;
            }
        }

        for (int i = 0; i < container_data.size(); i++) {
            if (container_data.get(i) + con_w < 0) {
                container_data.remove(i);
                break;
            }
        }
    }

    private void move_bitmaps() {
        danger_init_x += xSpeed * move_left;
        game_scree_last_x += xSpeed * move_left;


        for (int i = 0; i < danger_data.size(); i++) {
            int x = danger_data.get(i).get(0);
            danger_data.get(i).set(0, x + xSpeed * move_left);
        }

        for (int i = 0; i < cloud_data.size(); i++) {
            int x = cloud_data.get(i).get(0);
            cloud_data.get(i).set(0, x + xSpeed * move_left);
        }

        container_data.replaceAll(integer -> integer + xSpeed * move_left);
        basket_data.replaceAll(integer -> integer + xSpeed * move_left);
        sun_data.replaceAll(integer -> integer + xSpeed * move_left);
        ground_data.replaceAll(integer -> integer + xSpeed * move_left);
    }
}