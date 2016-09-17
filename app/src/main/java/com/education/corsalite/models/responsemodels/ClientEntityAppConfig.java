package com.education.corsalite.models.responsemodels;

import android.net.Uri;
import android.text.TextUtils;

import com.education.corsalite.utils.L;
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
    public String updateUrl;

    public boolean isDeviceAffinityEnabled() {
        return !TextUtils.isEmpty(deviceAffinity) && deviceAffinity.equalsIgnoreCase("Y");
    }

    public boolean isForceUpgradeEnabled() {
        return !TextUtils.isEmpty(forceUpgrade) && deviceAffinity.equalsIgnoreCase("Y");
    }

    public boolean isUpdateAvailable() {
        return !TextUtils.isEmpty(updateMethod);
    }

    public boolean isAppFromUnknownSources() {
        return  isUpdateAvailable() && updateMethod.equalsIgnoreCase("Update URL") && !TextUtils.isEmpty(updateUrl);
    }

    public String getAppVersion() {
        try {
            if (!TextUtils.isEmpty(updateUrl)) {
                Uri uri = Uri.parse(updateUrl);
                String apkFileName = uri.getLastPathSegment();
                // apk file name will be in the format of "corsalite_1.1.5.6_101506-staging-debug.apk"
                String versionNumber = apkFileName.substring(apkFileName.lastIndexOf("_")+1, apkFileName.indexOf("-"));
                return versionNumber;
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public String getAppVersionName() {
        try {
            if (!TextUtils.isEmpty(updateUrl)) {
                Uri uri = Uri.parse(updateUrl);
                String apkFileName = uri.getLastPathSegment();
                // apk file name will be in the format of "corsalite_1.1.5.6_101506-staging-debug.apk"
                String versionNumber = apkFileName.substring(apkFileName.indexOf("_")+1, apkFileName.lastIndexOf("_"));
                return versionNumber;
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }
}
