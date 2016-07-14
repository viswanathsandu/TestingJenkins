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
    @SerializedName("EnableStudyCenter")
    public String enableStudyCenter = "true";
    @SerializedName("EnableAnalytics")
    public String enableAnalytics = "false";
    @SerializedName("EnableSmartClass")
    public String enableSmartClass = "true";
    @SerializedName("EnableMyProfile")
    public String enableMyProfile = "true";
    @SerializedName("EnableOffline")
    public String enableOffline = "true";
    @SerializedName("EnableUsageAnalysis")
    public String enableUsageAnalysis = "true";
    @SerializedName("EnableChallengeTest")
    public String enableChallangeTest = "true";
    @SerializedName("EnableLogout")
    public String enableLogout = "true";
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

    public boolean isStudyCenterEnabled() {
        return isEnabled(enableStudyCenter);
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

    public boolean isVirtualCurrencyEnabled() {
        return customConfig != null && customConfig.get(0) != null && isEnabled(customConfig.get(0).enableVirtualCurrency);
    }

    public boolean isCurriculumEnabled() {
        return customConfig != null && customConfig.get(0) != null && isEnabled(customConfig.get(0).enableCurriculum);
    }

    public boolean isScheduledTestsEnabled() {
        return customConfig != null && customConfig.get(0) != null && isEnabled(customConfig.get(0).enableScheduleTests);
    }

    public boolean isMockTestsEnabled() {
        return customConfig != null && customConfig.get(0) != null && isEnabled(customConfig.get(0).enableMockTests);
    }

    public boolean isExamHistoryEnabled() {
        return customConfig != null && customConfig.get(0) != null && isEnabled(customConfig.get(0).enableExamHistory);
    }

    public boolean isForumEnabled() {
        return customConfig != null && customConfig.get(0) != null && isEnabled(customConfig.get(0).enableForum);
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

    private Boolean isEnabled(String text) {
        return (text != null && text.equalsIgnoreCase("true"));
    }
}
