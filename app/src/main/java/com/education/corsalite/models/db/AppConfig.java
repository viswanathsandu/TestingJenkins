package com.education.corsalite.models.db;

import android.text.TextUtils;

import com.education.corsalite.models.responsemodels.BaseResponseModel;
import com.education.corsalite.utils.CustomConfig;
import com.education.corsalite.utils.L;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class AppConfig extends BaseResponseModel {

    public String idUser;
    public String idClientAppConfig;
    @SerializedName("SplashDuration")
    public String splashDuration = "2000";
    @SerializedName("CustomConfig")
    public List<CustomConfig> customConfig;


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

    public boolean isforceUpgradeEnabled() {
        return customConfig != null && customConfig.get(0) != null && isEnabled(customConfig.get(0).forceUpgrade);
    }

    public int getLatestVersionCode() {
        try {
            if (customConfig != null && customConfig.get(0) != null && !TextUtils.isEmpty(customConfig.get(0).latestVersionCode)) {
                return Integer.valueOf(customConfig.get(0).latestVersionCode);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return 0;
    }

    public String getLatestVersionName() {
        try {
            if (customConfig != null && customConfig.get(0) != null && !TextUtils.isEmpty(customConfig.get(0).latestVersionName)) {
                return customConfig.get(0).latestVersionName;
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return "";
    }

    private Boolean isEnabled(String text) {
        return (text != null && text.equalsIgnoreCase("true"));
    }
}
