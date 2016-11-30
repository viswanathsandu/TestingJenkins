package com.education.corsalite.models.responsemodels;

import android.net.Uri;
import android.text.TextUtils;

import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.models.db.AppConfig;
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
    private String updateUrl;

    public boolean isDeviceAffinityEnabled() {
        return !TextUtils.isEmpty(deviceAffinity) && deviceAffinity.equalsIgnoreCase("Y");
    }

    public boolean isForceUpgradeEnabled() {
        return !TextUtils.isEmpty(forceUpgrade) && forceUpgrade.equalsIgnoreCase("Y");
    }

    public boolean isUpdateAvailable() {
        return !TextUtils.isEmpty(updateMethod) && (updateMethod.equalsIgnoreCase("Play Store") || updateMethod.equalsIgnoreCase("Update URL"));
    }

    public boolean isAppFromUnknownSources() {
        return  isUpdateAvailable() && updateMethod.equalsIgnoreCase("Update URL") && !TextUtils.isEmpty(updateUrl);
    }

    public boolean isAppFromPlayStore() {
        return  isUpdateAvailable() && updateMethod.equalsIgnoreCase("Play Store");
    }

    public String getAppVersionName() {
        try {
            if(isAppFromPlayStore()) {
                AppConfig config = AbstractBaseActivity.appConfig;
                if(config != null) {
                    return String.valueOf(config.getLatestVersionName());
                }
            } else if(isAppFromUnknownSources() && !TextUtils.isEmpty(getUpdateUrl())) {
                Uri uri = Uri.parse(getUpdateUrl());
                String apkFileName = new String(uri.getLastPathSegment());
                L.info("Apk File Name :" + apkFileName);
                // apk file name will be in the format of "corsalite_1.1.5.6_101506-staging-debug.apk"
                String[] data = apkFileName.split("_");
                String versionName = data[1]; //corsalite_1.1.6_101600-staging-debug.apk
                return versionName;
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public String getAppVersionNumber() {
        try {
            if(isAppFromPlayStore()) {
                AppConfig config = AbstractBaseActivity.appConfig;
                if(config != null && config.getLatestVersionCode() != 0) {
                    return String.valueOf(config.getLatestVersionCode());
                }
            } else if(isAppFromUnknownSources() && !TextUtils.isEmpty(getUpdateUrl())) {
                if (!TextUtils.isEmpty(getUpdateUrl())) {
                    Uri uri = Uri.parse(getUpdateUrl());
                    String apkFileName = new String(uri.getLastPathSegment());
                    // apk file name will be in the format of "corsalite_1.1.5.6_101506-staging-debug.apk"
                    String[] data = apkFileName.split("-");
                    String[] versionDetails = data[0].split("_");
                    String versionNumber = versionDetails[versionDetails.length - 1];
                    return versionNumber;
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }
}
