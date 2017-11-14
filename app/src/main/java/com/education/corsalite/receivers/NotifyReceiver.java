package com.education.corsalite.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.event.ScheduledTestStartEvent;
import com.education.corsalite.notifications.NotificationsUtils;
import com.education.corsalite.utils.L;

import br.com.goncalves.pugnotification.notification.PugNotification;
import de.greenrobot.event.EventBus;

public class NotifyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (TextUtils.isEmpty(LoginUserCache.getInstance().getStudentId())) {
                return;
            }
            String action = intent.getAction();
            String testQuestionPaperId = intent.getExtras().getString("test_question_paper_id", "");
            boolean startExam = intent.getExtras().getBoolean("start_exam", false);
            if (action != null && action.equals("CANCEL_NOTIFICATION")) {
                int id = intent.getIntExtra("id", -1);
                if (id != -1) {
                    L.info("Cancelling notification : " + id);
                    NotificationsUtils.cancelNotification(context, id);
                    PugNotification.with(context).cancel(id);
                }
            } else if (startExam) {
                ScheduledTestStartEvent event = new ScheduledTestStartEvent();
                event.testQuestionPaperId = testQuestionPaperId;
                EventBus.getDefault().post(event);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }
}
