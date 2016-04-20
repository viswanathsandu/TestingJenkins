package com.education.corsalite.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.services.NotificationsUtils;
import com.education.corsalite.utils.Constants;

public class NotifyReceiver extends BroadcastReceiver {

    private int requestCode = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String title = extras.getString("title", "Corsalite");
        String subTitle = extras.getString("sub_title", "Thank you");
        String testQuestionPaperId = extras.getString("test_question_paper_id", "");
        int id = extras.getInt("id", 0);
        Toast.makeText(context, "Notification : "+title, Toast.LENGTH_SHORT).show();
        NotificationsUtils.NotifyUser(context, id, title, subTitle, getScheduledExamActivityIntent(context, testQuestionPaperId));
    }

    private PendingIntent getScheduledExamActivityIntent(Context context, String examTemplateId) {
        if(examTemplateId.equals("")) {
            return null;
        }
        Intent intent = new Intent(context, ExamEngineActivity.class);
        intent.putExtra(Constants.TEST_TITLE, "Schedule Test");
        intent.putExtra("test_question_paper_id", examTemplateId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, requestCode++, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
