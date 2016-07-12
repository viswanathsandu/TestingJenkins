package com.education.corsalite.utils;

import android.content.Context;
import android.text.TextUtils;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class AppConfig {

    private static AppConfig instance;
    @SerializedName("SplashDuration")
    public String splashDuration = "2000";
    public String enableVirtualCurrency = "false";
    @SerializedName("EnableStudyCenter")
    public String enableStudyCenter = "true";
    @SerializedName("EnableAnalytics")
    public String enableAnalytics = "false";
    public String enableCurriculum = "true";
    @SerializedName("EnableSmartClass")
    public String enableSmartClass = "true";
    @SerializedName("EnableMyProfile")
    public String enableMyProfile = "true";
    @SerializedName("EnableOffline")
    public String enableOffline = "true";
    @SerializedName("EnableLogout")
    public String enableLogout = "true";
    @SerializedName("EnableChallengeTest")
    public String enableChallangeTest = "true";
    public String enableForum = "true";
    public String enableScheduleTests = "true";
    public String enablemockTests = "true";
    public String enableExamHistory = "true";
    public Boolean forceUpgrade = false;
    public Integer latestVersionCode = 0;
    public String idClientAppConfig;
    public String idUser;

    public static void loadAppConfig(Context context) {
        String jsonResponse = FileUtils.loadJSONFromAsset(context.getAssets(), "config.json");
        instance = new Gson().fromJson(jsonResponse, AppConfig.class);
        EventBus.getDefault().post(instance);
    }

    public static void loadAppConfigFromService(Context context, String idUser) {
        ApiManager.getInstance(context).getAppConfig(idUser, new ApiCallback<AppConfig>(context) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
            }

            @Override
            public void success(AppConfig appConfig, Response response) {
                super.success(appConfig, response);
                instance = appConfig;
                EventBus.getDefault().post(appConfig);
            }
        });
    }

    public Integer getSplashDuration() {
        int defaultTime = 2000;
        try {
            if (!TextUtils.isEmpty(splashDuration)) {
                return Integer.valueOf(splashDuration);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return defaultTime;
    }

    public static AppConfig getInstance() {
        if(instance == null) {
            return new AppConfig();
        }
        return instance;
    }

    public boolean isVirtualCurrencyEnabled() {
        return isEnabled(enableVirtualCurrency);
    }

    public boolean isStudyCenterEnabled() {
        return isEnabled(enableStudyCenter);
    }

    public boolean isCurriculumEnabled() {
        return isEnabled(enableCurriculum);
    }

    public boolean isAnalyticsEnabled() {
        return isEnabled(enableAnalytics);
    }


    public boolean isSmartClassEnabled() {
        return isEnabled(enableSmartClass);
    }

    public boolean isMyProfileEnabled() {
        return isEnabled(enableMyProfile);
    }

    public boolean isOfflineEnabled() {
        return isEnabled(enableOffline);
    }

    public boolean isLogoutEnabled() {
        return isEnabled(enableLogout);
    }

    public boolean isChallengeTestEnabled() {
        return isEnabled(enableChallangeTest);
    }

    public boolean isScheduledTestsEnabled() {
        return isEnabled(enableScheduleTests);
    }

    public boolean isMockTestsEnabled() {
        return isEnabled(enablemockTests);
    }

    public boolean isExamHistoryEnabled() {
        return isEnabled(enableExamHistory);
    }

    public boolean isForumEnabled() {
        return isEnabled(enableForum);
    }

    private Boolean isEnabled(String text) {
        return (text != null && text.equalsIgnoreCase("true"));
    }
}
