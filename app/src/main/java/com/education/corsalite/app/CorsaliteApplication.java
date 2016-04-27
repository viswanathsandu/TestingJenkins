package com.education.corsalite.app;

import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;
import com.education.corsalite.R;
import com.education.corsalite.activities.SplashActivity;
import com.education.corsalite.utils.L;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.localytics.android.LocalyticsActivityLifecycleCallbacks;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by vissu on 9/18/15.
 */
public class CorsaliteApplication extends com.orm.SugarApp {

    private HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public CorsaliteApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        registerActivityLifecycleCallbacks(
                new LocalyticsActivityLifecycleCallbacks(this));
        registerForCrashes();
    }

    private void registerForCrashes() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });
    }

    private void handleUncaughtException(Thread thread, Throwable e) {
        L.error(e.getMessage(), e);
        Crashlytics.logException(e);
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);
        System.exit(1);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        loadFonts();
    }

    private void loadFonts() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.roboto_regular))
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    public enum TrackerName {
        APP_TRACKER // Tracker used only in this app.
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        try {
            if (!mTrackers.containsKey(trackerId)) {
                //analytics.setLocalDispatchPeriod(15);
                Tracker t = GoogleAnalytics.getInstance(this).newTracker(R.xml.app_tracker);
                mTrackers.put(trackerId, t);
            }
            return mTrackers.get(trackerId);
        } catch (Exception e) {
            return null;
        }
    }
}
