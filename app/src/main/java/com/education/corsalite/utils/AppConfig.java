package com.education.corsalite.utils;

import android.content.Context;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.google.gson.Gson;

import retrofit.client.Response;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class AppConfig {

    private static AppConfig instance;

    public String baseUrl;
    public String stageUrl;
    public String productionUrl;
    public String stageSocketUrl;
    public String productionSocketUrl;
    public Boolean enableProduction;
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


    public static void loadAppConfigFromService(Context context, String idUser){
        ApiManager.getInstance(context).getAppConfig(idUser, new ApiCallback<String>(context) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                System.out.println("--> Response=" + error.message);
                }

            @Override
            public void success(String s, Response response) {
                super.success(s, response);
//                instance = new Gson().fromJson(s, AppConfig.class);
                System.out.println("--> Response="+response.getStatus());
            }
        });

    }



    public static AppConfig getInstance() {
        return instance;
    }

    public void enableProduction() {
        enableProduction = true;
    }

    public void disableProduction() {
        enableProduction = false;
    }
}
