package com.education.corsalite.utils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class CustomConfig {

    @SerializedName("EnableVirtualCurrency")
    public String enableVirtualCurrency = "false";
    @SerializedName("EnableCurriculum")
    public String enableCurriculum = "true";
    @SerializedName("EnableForum")
    public String enableForum = "true";
    @SerializedName("EnableScheduleTests")
    public String enableScheduleTests = "true";
    @SerializedName("EnableMockTests")
    public String enableMockTests = "true";
    @SerializedName("EnableExamHistory")
    public String enableExamHistory = "true";
    @SerializedName("ForceUpgrade")
    public String forceUpgrade = "false";
    @SerializedName("LatestVersionCode")
    public String latestVersionCode;
    @SerializedName("LatestVersionName")
    public String latestVersionName;
}
