package com.education.corsalite.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

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

    public static String getImeiNumber(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return  telephonyManager.getDeviceId();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return null;
        }
    }
}
