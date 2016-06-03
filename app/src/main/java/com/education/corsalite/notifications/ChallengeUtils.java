package com.education.corsalite.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.education.corsalite.activities.ChallengeActivity;
import com.education.corsalite.models.responsemodels.ChallengeUser;
import com.education.corsalite.models.socket.response.ChallengeTestRequestEvent;
import com.education.corsalite.models.socket.response.ChallengeTestUpdateEvent;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by vissu on 4/23/16.
 */
public class ChallengeUtils {

    private int requestCode = 0;
    private static ChallengeUtils instance;
    private Context context;

    List<ChallengeUser> challengeUsers;
    public boolean challengeStarted = false;
    public boolean challengeCompleted = false;

    public static ChallengeUtils get(Context context) {
        if(instance == null) {
            instance = new ChallengeUtils();
        }
        instance.context = context;
        return instance;
    }

    public void showChallengeRequestNotification(ChallengeTestRequestEvent event) {
        try {
            Bundle extras = new Bundle();
            String title = extras.getString("title", "Challenge Request");
            String subTitle = extras.getString("sub_title", "Challenger : " + event.challengerName);
            Toast.makeText(context, "Notification : " + title, Toast.LENGTH_SHORT).show();
            NotificationsUtils.NotifyUser(context, Integer.valueOf(event.challengeTestParentId), title, subTitle, getChallengeRequestIntent(event));
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private PendingIntent getChallengeRequestIntent(ChallengeTestRequestEvent event) {
        if(event != null && TextUtils.isEmpty(event.challengeTestParentId)) {
            return null;
        }
        Intent intent = new Intent(context, ChallengeActivity.class);
        intent.putExtra("type", "REQUEST");
        intent.putExtra("challenge_test_request_json", new Gson().toJson(event));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, requestCode++, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public void showChallengeUpdateNotification(ChallengeTestUpdateEvent event) {
        try {
            Bundle extras = new Bundle();
            String title = extras.getString("title", "Challenge");
            String subTitle = extras.getString("sub_title", event.challengerName + " has " + event.challengerStatus);
            Toast.makeText(context, "Notification : " + title, Toast.LENGTH_SHORT).show();
            NotificationsUtils.NotifyUser(context, Integer.valueOf(event.challengeTestParentId), title, subTitle, getChallengeUpdateIntent(event));
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void clearChallengeNotifications(ChallengeTestUpdateEvent event) {
        try {
            NotificationsUtils.cancelNotification(context, Integer.valueOf(event.challengeTestParentId));
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private PendingIntent getChallengeUpdateIntent(ChallengeTestUpdateEvent event) {
        if(event != null && (TextUtils.isEmpty(event.challengeTestParentId) || TextUtils.isEmpty(event.challengerName))) {
            return null;
        }
        Intent intent = new Intent(context, ChallengeActivity.class);
        intent.putExtra("type", "UPDATE");
        intent.putExtra("challenge_test_update_json", new Gson().toJson(event));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, requestCode++, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

}
