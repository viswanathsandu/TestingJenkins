package com.education.corsalite.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.services.NotifyScheduledTest;

public class NotifyReceiver extends BroadcastReceiver {
    public NotifyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String title = extras.getString("title", "Corsalite");
        String subTitle = extras.getString("sub_title", "Thank you");
        int id = extras.getInt("id", 0);
        NotifyScheduledTest.NotifyUser(context, id, title, subTitle, getScheduledExamActivityIntent(context, id+""));
    }

    private PendingIntent getScheduledExamActivityIntent(Context context, String examTemplateId) {
        Intent intent = new Intent(context, ExamEngineActivity.class);
        intent.putExtra(Constants.TEST_TITLE, "Schedule Test");
        intent.putExtra("exam_template_id", examTemplateId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
