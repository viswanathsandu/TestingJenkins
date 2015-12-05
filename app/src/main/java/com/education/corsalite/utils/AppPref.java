package com.education.corsalite.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vissu on 11/29/15.
 */
public class AppPref {

    private Context context;
    private static AppPref instance = null;
    private static String PREFS_NAME = "CORSALITE_PREFS";

    public static AppPref getInstance(Context context) {
        if(instance == null) {
            instance = new AppPref();
        }
        instance.context = context;
        return instance;
    }

    public void save(String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().putString(key, value).commit();
    }

    public String getValue(String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, null);
    }

    public void remove(String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().remove(key);
    }
}
