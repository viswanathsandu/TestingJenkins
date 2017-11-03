package com.education.corsalite.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.education.corsalite.event.TimeChangedEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 10/10/16.
 */

public class TimeChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_TIME_CHANGED)
                || action.equalsIgnoreCase(Intent.ACTION_TIMEZONE_CHANGED)) {
            EventBus.getDefault().post(new TimeChangedEvent());
        }
    }
}
