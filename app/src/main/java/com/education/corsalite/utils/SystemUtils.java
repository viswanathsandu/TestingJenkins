package com.education.corsalite.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;

/**
 * Created by vissu on 11/27/15.
 */
public class SystemUtils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        boolean status = activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();
        Crashlytics.setString("network", status ? "online" : "offline");
        return status;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getUniqueID(Context context) {
        try {
            String myAndroidDeviceId = "";
            // TODO : imei is not being received when phone app is crashed
            myAndroidDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            L.info("Device Id : " + myAndroidDeviceId);
            return myAndroidDeviceId;
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return null;
        }
    }
}
