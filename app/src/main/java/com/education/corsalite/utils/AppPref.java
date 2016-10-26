package com.education.corsalite.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.db.OfflineContent;


/**
 * Created by vissu on 11/29/15.
 */
public class AppPref {

    private Context context;
    private static AppPref instance = null;
    private static String PREFS_NAME = "CORSALITE_PREFS";
    private static String OFFLINE_PREFS_NAME = "CORSALITE_PREFS";

    private final String KEY_USER_ID = "user_id";
    private final String KEY_STUDENT_ID = "student_id";
    private final String KEY_VIRTUAL_CURRENCY_ID = "virtual_currency";

    public static AppPref get(Context context) {
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

    public void saveObj(String id, OfflineContent content) {
        try {
            String json = Gson.get().toJson(content);
            SharedPreferences settings = context.getSharedPreferences(OFFLINE_PREFS_NAME, Context.MODE_PRIVATE);
            settings.edit().putString(id, json).commit();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public String getValue(String key) {
        try {
            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            return settings.getString(key, null);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return null;
        }
    }

    public void remove(String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.apply();
        editor.commit();
    }

    public void setUserId(String userID) {
        save(KEY_USER_ID, userID);
    }

    public String getUserId() {
        return getValue(KEY_USER_ID);
    }

    public void clearUserId() {
        remove(KEY_USER_ID);
    }

    public void setVirtualCurrency(String currency) {
        save(KEY_VIRTUAL_CURRENCY_ID, currency);
    }

    public String getVirtualCurrency() {
        return getValue(KEY_VIRTUAL_CURRENCY_ID);
    }

    public void clearVirtualCurrency() {
        remove(KEY_VIRTUAL_CURRENCY_ID);
    }
}
