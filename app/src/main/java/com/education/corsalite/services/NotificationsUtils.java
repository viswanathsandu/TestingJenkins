package com.education.corsalite.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.education.corsalite.R;

public class NotificationsUtils {

    private static final String TAG = NotificationsUtils.class.getSimpleName();

    public static void NotifyUser(Context mContext, int id, String title, String content, PendingIntent intent) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notifyUser = new NotificationCompat.Builder(mContext)
                .setAutoCancel(true)
                .setContentIntent(intent)
                .setContentTitle(title )
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("Scheduled Test")
                .build();

        notificationManager.notify(id, notifyUser);
    }
}
