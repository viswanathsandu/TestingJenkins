package com.education.corsalite.utils;

import android.content.Context;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import retrofit.client.Response;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class AppConfig {

    private static AppConfig instance;
    @SerializedName("BaseUrl")
    private String baseUrl;
    public String stageUrl;
    public String productionUrl;
    public String stageSocketUrl;
    public String productionSocketUrl;
    public Boolean enableProduction;
    @SerializedName("SplashDuration")
    public String splashDuration;
    @SerializedName("EnableStudyCenter")
    public String enableStudyCenter;
    @SerializedName("EnableAnalytics")
    public String enableAnalytics;
    @SerializedName("EnableSmartClass")
    public String enableSmartClass;
    @SerializedName("EnableMyProfile")
    public String enableMyProfile;
    @SerializedName("EnableOffline")
    public String enableOffline;
    @SerializedName("EnableLogout")
    public String enableLogout;
    @SerializedName("EnableUsageAnalysis")
    public String enableUsageanalysis;
    @SerializedName("EnableChallengeTest")
    public String enableChallangeTest;
    public Boolean enableForum;
    public String idClientAppConfig;
    public String idUser;

    public static void loadAppconfig(Context context) {
        String jsonResponse = FileUtils.loadJSONFromAsset(context.getAssets(), "config.json");
        instance = new Gson().fromJson(jsonResponse, AppConfig.class);
    }


    public static void loadAppConfigFromService(Context context, String idUser){
        ApiManager.getInstance(context).getAppConfig(idUser, new ApiCallback<AppConfig>(context) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                }

            @Override
            public void success(AppConfig appConfig, Response response) {
                super.success(appConfig, response);
                instance = appConfig;
                System.out.println("VALIDATING RESPONSE="+instance);
            }
        });

    }


  public Integer getSplashDuration(){
        if(splashDuration!=null){
            return Integer.valueOf(splashDuration);
        }else{
            return 5000;
        }
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
