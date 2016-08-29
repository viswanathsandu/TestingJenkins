package com.education.corsalite.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.education.corsalite.R;
import com.education.corsalite.activities.CrashHandlerActivity;
import com.education.corsalite.utils.DbUtils;
import com.education.corsalite.utils.L;
import com.google.firebase.crash.FirebaseCrash;
import com.localytics.android.LocalyticsActivityLifecycleCallbacks;
import com.orm.SugarContext;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by vissu on 9/18/15.
 */
public class CorsaliteApplication extends Application {

    public CorsaliteApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initSugarDb();
        Fabric.with(this, new Crashlytics());
        registerActivityLifecycleCallbacks(
                new LocalyticsActivityLifecycleCallbacks(this));
        registerForCrashes();
    }

    private void initSugarDb() {
        DbUtils.get(getApplicationContext()).loadDatabaseFromBackup();
        SugarContext.init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        DbUtils.get(getApplicationContext()).backupDatabase();
        super.onTerminate();
        SugarContext.terminate();
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
        FirebaseCrash.report(e);
        Intent intent = new Intent(getApplicationContext(), CrashHandlerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);
        System.exit(1);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        loadFonts();
        MultiDex.install(this);
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

}
