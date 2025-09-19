package com.ChickenArsenal.CA2508;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;

import java.text.DecimalFormat;
import java.util.Locale;

public class Player {

    private static SharedPreferences sharedPreferences;
	private static boolean onVibrating;
    private static Vibrator v;
    private static LayoutInflater inflate;
    private static AlertDialog.Builder builder;
    public static MediaPlayer all_screens, button, music;

    public static void all_screens(Context context, int audio) {
        all_screens = MediaPlayer.create(context, audio);
        all_screens.setLooping(true);
    }

    public static void button(Context context, int audio) {
        button = MediaPlayer.create(context, audio);
        button.setLooping(false);
    }

    public static void music(Context context, int audio) {
        music = MediaPlayer.create(context, audio);
        music.setLooping(false);
    }


    public static void StopAll() {
        try {
            all_screens.pause();
            button.pause();
            music.pause();
        } catch (Exception e) {

        }
    }

    public static void button(boolean soundMute) {
        if (!soundMute)
            button.start();
    }

    public static void changeLanguage(Activity activity, SharedPreferences.Editor editor, String lang, boolean reload) {
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(lang);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, null);

        editor.putString("lang", lang);
        editor.apply();

        if (reload) {
            Intent intent = new Intent(activity, activity.getClass());
            activity.startActivity(intent);
            activity.finish();
        }
    }
}