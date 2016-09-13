package com.education.corsalite.models.responsemodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/13/16.
 */

public class ClientEntityAppConfig extends BaseResponseModel {

    public String idEntity;
    public String idUser;
    @SerializedName("DeviceAffinity")
    private String deviceAffinity;
    @SerializedName("DeviceID")
    public String deviceId;
    @SerializedName("ForceUpdate")
    private String forceUpgrade;
    @SerializedName("UpdateMethod")
    private String updateMethod;
    @SerializedName("UpdateURL")
    private String updateUrl;

    public boolean isDeviceAffinityEnabled() {
        return !TextUtils.isEmpty(deviceAffinity) && deviceAffinity.equalsIgnoreCase("Y");
    }

    public boolean isForceUpgradeEnabled() {
        return !TextUtils.isEmpty(forceUpgrade) && deviceAffinity.equalsIgnoreCase("Y");
    }
}
