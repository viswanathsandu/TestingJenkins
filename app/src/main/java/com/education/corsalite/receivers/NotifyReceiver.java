package com.education.corsalite.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.event.ScheduledTestStartEvent;
import com.education.corsalite.notifications.NotificationsUtils;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;

import de.greenrobot.event.EventBus;

public class NotifyReceiver extends BroadcastReceiver {

    private int requestCode = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            String testQuestionPaperId = intent.getExtras().getString("test_question_paper_id", "");
            boolean startExam = intent.getExtras().getBoolean("start_exam", false);
            L.info("Notify Receiver : Cancel Notification - "+testQuestionPaperId +"\t"+startExam);
            if (action != null && action.equals("CANCEL_NOTIFICATION")) {
                int id = intent.getIntExtra("id", -1);
                if (id != -1) {
                    L.info("Cancelling notification : " + id);
                    NotificationsUtils.cancelNotification(context, id);
                }
            } else if (TextUtils.isEmpty(testQuestionPaperId)) {
                Bundle extras = intent.getExtras();
                String title = extras.getString("title", "Corsalite");
                String subTitle = extras.getString("sub_title", "Thank you");
                int id = extras.getInt("id", 0);
                NotificationsUtils.NotifyUser(context, id, title, subTitle, getScheduledExamActivityIntent(context, testQuestionPaperId));
            } else if(startExam){
                ScheduledTestStartEvent event = new ScheduledTestStartEvent();
                event.testQuestionPaperId = testQuestionPaperId;
                EventBus.getDefault().post(event);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private PendingIntent getScheduledExamActivityIntent(Context context, String examTemplateId) {
        if (examTemplateId.equals("")) {
            return null;
        }
        Intent intent = new Intent(context, ExamEngineActivity.class);
        intent.putExtra(Constants.TEST_TITLE, "Scheduled Test");
        intent.putExtra("test_question_paper_id", examTemplateId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, requestCode++, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
