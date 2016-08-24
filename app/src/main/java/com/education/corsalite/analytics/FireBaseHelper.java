package com.education.corsalite.analytics;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by vissu on 8/24/16.
 */

public class FireBaseHelper {

    private static FirebaseAnalytics firebase;

    public static void initFireBase(Context context) {
        firebase = FirebaseAnalytics.getInstance(context);
    }

    public static void setUsername(String username) {
        if(firebase == null) return;
        firebase.setUserId(username);
    }

    public static void logEvent(String tag) {
        if(firebase == null) return;
        Bundle bundle = new Bundle();
        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public static void logScreen(String screenName) {
        if(firebase == null) return;
        Bundle bundle = new Bundle();
        bundle.putString(Param.SCREEN_NAME, screenName);
        firebase.logEvent(Event.SCREEN, bundle);
    }

    public static void log(String logType, String tag, String message) {
        if(firebase == null) return;
        log(logType, tag, message, null);
    }

    public static void log(String logType, String tag, String message, Throwable throwable) {
        if(firebase == null) return;
        Bundle bundle = new Bundle();
        bundle.putString(Param.LOG_TYPE, logType);
        bundle.putString(Param.LOG_NAME, tag);
        bundle.putString(Param.LOG_MESSAGE, message);
        if(throwable != null) {
            bundle.putString(Param.LOG_STACK_TRACE, Log.getStackTraceString(throwable));
        }
        firebase.logEvent(Event.LOGGER, bundle);
    }

    public static class Param {
        public static final String SCREEN_NAME = "screen_name";
        public static final String LOG_NAME = "log_tag";
        public static final String LOG_TYPE = "log_type";
        public static final String LOG_MESSAGE = "log_message";
        public static final String LOG_STACK_TRACE = "log_stack_trace";
        protected Param() {}
    }

    public static class Event {
        public static final String SCREEN = "screen";
        public static final String LOGGER = "logger";
        protected Event() {}
    }
}
