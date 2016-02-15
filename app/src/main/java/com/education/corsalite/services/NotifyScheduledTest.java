package com.education.corsalite.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.education.corsalite.R;
import com.education.corsalite.activities.StudyCentreActivity;
import com.education.corsalite.utils.SystemUtils;

public class NotifyScheduledTest  {

    private static final String TAG = NotifyScheduledTest.class.getSimpleName();

    public static void NotifyUser(Context mContext, String examName, String examId) {
        Intent mainIntent = new Intent(mContext, StudyCentreActivity.class);
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notifyUser = new NotificationCompat.Builder(mContext)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(mContext, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentTitle("" + examName + " starts in 10 minutes")
                .setContentText("Exam Id " + examId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("Scheduled Test")
                .setWhen(System.currentTimeMillis())
                .build();

        notificationManager.notify(0, notifyUser);
    }
}
