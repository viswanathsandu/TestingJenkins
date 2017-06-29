package com.education.corsalite.utils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class CustomConfig {
    @SerializedName("ForceUpgrade")
    public String forceUpgrade = "false";
    @SerializedName("LatestVersionCode")
    public String latestVersionCode;
    @SerializedName("LatestVersionName")
    public String latestVersionName;
}
