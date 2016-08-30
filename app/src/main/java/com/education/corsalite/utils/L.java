package com.education.corsalite.utils;

import android.util.Log;

import com.education.corsalite.BuildConfig;

/**
 * Created by vissu on 9/17/15.
 */
public class L {

    private static final String TAG = "CORSALITE";
    private static final String INFO = "INFO";
    private static final String DEBUG = "DEBUG";
    private static final String ERROR = "ERROR";


    private static boolean isInfoEnabled() {
        return (BuildConfig.DEBUG);
    }

    private static boolean isDebugEnabled() {
        return (BuildConfig.DEBUG);
    }

    private static boolean isErrorEnabled() {
        return BuildConfig.DEBUG;
    }

    public static void info(String message) {
        info(TAG, message);
    }

    public static void info(String message, Throwable throwable) {
        info(TAG, message, throwable);
    }

    public static void info(String tag, String message) {
        if(isInfoEnabled()) {
            Log.i(tag, message);
        }
    }

    public static void info(String tag, String message, Throwable throwable) {
        if(isInfoEnabled()) {
            Log.i(tag, message, throwable);
        }
    }

    public static void debug(String message) {
        debug(TAG, message);
    }

    public static void debug(String message, Throwable throwable) {
        debug(TAG, message, throwable);
    }

    public static void debug(String tag, String message) {
        if(isDebugEnabled()) {
            Log.d(tag, message);
        }
    }

    public static void debug(String tag, String message, Throwable throwable) {
        if (isDebugEnabled()) {
            Log.d(tag, message, throwable);
        }
    }

    public static void error(String message) {
        error(TAG, message);
    }

    public static void error(String message, Throwable throwable) {
        error(TAG, message, throwable);
    }

    public static void error(String tag, String message) {
        if(isErrorEnabled()) {
            Log.e(tag, message);
        }
    }

    public static void error(String tag, String message, Throwable throwable) {
        if(isErrorEnabled()) {
            Log.e(tag, message, throwable);
        }
    }
}
