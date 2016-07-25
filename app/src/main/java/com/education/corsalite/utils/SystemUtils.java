package com.education.corsalite.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
}
