package com.education.corsalite.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.education.corsalite.R;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class NotificationsUtils {

    private static final String TAG = NotificationsUtils.class.getSimpleName();

    public static void NotifyUser(Context context, int id, String title, String content, PendingIntent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notifyUser = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setContentIntent(intent)
                .setContentTitle(title)
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

    public static void showVideoDownloadNotification(Context context, int id, int progress, String videoName) {
        PugNotification.with(context)
                .load()
                .identifier(id)
                .title("Downloading video")
                .message("Downloading video " + videoName)
                .smallIcon(R.drawable.ic_launcher)
                .progress()
                .value(progress, 100, false)
                .build();
    }

    public static void showContentDownloadNotification(Context context, int id, String videoName) {
        PugNotification.with(context)
                .load()
                .identifier(id)
                .title("Downloading Content")
                .smallIcon(R.drawable.ic_launcher)
                .progress()
                .value(0, 100, true)
                .build();
    }

    public static void showExerciseDownloadNotification(Context context, int id, String videoName) {
        PugNotification.with(context)
                .load()
                .identifier(id)
                .title("Downloading Exercises")
                .smallIcon(R.drawable.ic_launcher)
                .progress()
                .value(0, 100, true)
                .build();
    }

    public static void showTestDownloadNotification(Context context, int id, String videoName) {
        PugNotification.with(context)
                .load()
                .identifier(id)
                .title("Downloading Tests")
                .smallIcon(R.drawable.ic_launcher)
                .progress()
                .value(0, 100, true)
                .build();
    }

    public static void showFailureNotification(Context context, int id, String name) {
        PugNotification.with(context)
                .load()
                .identifier(id)
                .title("Download failed")
                .message("Failed to download " + name)
                .smallIcon(R.drawable.ic_launcher)
                .simple()
                .build();
    }

    public static void showSuccessNotification(Context context, int id, String name) {
        PugNotification.with(context)
                .load()
                .identifier(id)
                .title("Download Completed")
                .message("Successfull Downloaded " + name)
                .smallIcon(R.drawable.ic_launcher)
                .simple()
                .build();
    }

    public static void cancelDownloadNotification(Context context, int id) {
        PugNotification.with(context).cancel(id);
    }
}
