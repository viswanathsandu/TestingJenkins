package com.education.corsalite.utils;

import android.content.Context;

import com.google.gson.Gson;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class AppConfig {

    private static AppConfig instance;

    public String baseUrl;
    public Integer splashDuration;
    public Boolean enableStudyCenter;
    public Boolean enableAnalytics;
    public Boolean enableSmartClass;
    public Boolean enableMyProfile;
    public Boolean enableOffline;
    public Boolean enableLogout;
    public Boolean enableUsageanalysis;
    public Boolean enableChallangeTest;
    public Boolean enableForum;

    public static void loadAppconfig(Context context) {
        String jsonResponse = FileUtils.loadJSONFromAsset(context.getAssets(), "config.json");
        instance = new Gson().fromJson(jsonResponse, AppConfig.class);
    }

    public static AppConfig getInstance() {
        return instance;
    }
}
