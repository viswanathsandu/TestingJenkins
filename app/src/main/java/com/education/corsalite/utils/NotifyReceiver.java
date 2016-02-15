package com.education.corsalite.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.education.corsalite.services.NotifyScheduledTest;

public class NotifyReceiver extends BroadcastReceiver {
    public NotifyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String examName = intent.getExtras().getString("Exam Name");
        String examId = intent.getExtras().getString("Exam Id");
        NotifyScheduledTest.NotifyUser(context, examName, examId);
    }
}
