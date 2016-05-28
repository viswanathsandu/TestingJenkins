package com.corsalite.tabletapp.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.corsalite.tabletapp.R;

public class NotificationsUtils {

    private static final String TAG = NotificationsUtils.class.getSimpleName();

    public static void NotifyUser(Context context, int id, String title, String content, PendingIntent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notifyUser = new NotificationCompat.Builder(context)
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

    public static void cancelNotification(Context context, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager manager = (NotificationManager) context.getSystemService(ns);
        manager.cancel(notifyId);
    }
}
