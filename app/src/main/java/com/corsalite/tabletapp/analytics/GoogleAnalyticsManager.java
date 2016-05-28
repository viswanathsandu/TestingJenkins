package com.corsalite.tabletapp.analytics;

import android.app.Activity;
import android.content.Context;

import com.corsalite.tabletapp.app.CorsaliteApplication;
import com.corsalite.tabletapp.utils.SystemUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Madhuri on 15-12-2015.
 */
public class GoogleAnalyticsManager {

    public static void sendOpenScreenEvent(Activity activity, Context context, String screenName){
        if(SystemUtils.isNetworkConnected(context)){
            try{
                Tracker tracker = ((CorsaliteApplication) activity.getApplication()).getTracker(CorsaliteApplication.TrackerName.APP_TRACKER);
                tracker.setScreenName(screenName);
                tracker.send(new HitBuilders.AppViewBuilder().build());
            }catch(Exception e){
            }
        }
    }
}
