package com.education.corsalite.app;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.education.corsalite.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.localytics.android.LocalyticsActivityLifecycleCallbacks;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by vissu on 9/18/15.
 */
public class CorsaliteApplication extends com.orm.SugarApp{

    public CorsaliteApplication(){
        super();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        registerActivityLifecycleCallbacks(
                new LocalyticsActivityLifecycleCallbacks(this));
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

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        try{
            if (!mTrackers.containsKey(trackerId)) {

                //analytics.setLocalDispatchPeriod(15);
                Tracker t =  GoogleAnalytics.getInstance(this).newTracker(R.xml.app_tracker);
                mTrackers.put(trackerId, t);
            }
            return mTrackers.get(trackerId);
        }catch(Exception e){
            return null;
        }
    }
}
