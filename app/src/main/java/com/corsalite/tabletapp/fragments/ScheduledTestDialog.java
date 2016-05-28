package com.corsalite.tabletapp.fragments;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.activities.AbstractBaseActivity;
import com.corsalite.tabletapp.activities.ExamEngineActivity;
import com.corsalite.tabletapp.adapters.ScheduledTestsListAdapter;
import com.corsalite.tabletapp.api.ApiCallback;
import com.corsalite.tabletapp.api.ApiManager;
import com.corsalite.tabletapp.cache.LoginUserCache;
import com.corsalite.tabletapp.models.db.ScheduledTestList;
import com.corsalite.tabletapp.models.db.ScheduledTestsArray;
import com.corsalite.tabletapp.models.responsemodels.CorsaliteError;
import com.corsalite.tabletapp.services.TestDownloadService;
import com.corsalite.tabletapp.utils.Constants;
import com.corsalite.tabletapp.utils.Data;
import com.corsalite.tabletapp.utils.L;
import com.corsalite.tabletapp.receivers.NotifyReceiver;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Viswanath on 17/10/15.
 */
public class ScheduledTestDialog extends DialogFragment implements ScheduledTestsListAdapter.IScheduledTestSelectedListener {

    @Bind(R.id.tv_title)TextView tvTitle;
    @Bind(R.id.mocktests_recyclerview)RecyclerView rvMockTestList;

    private ScheduledTestList mScheduledTestList;
    private Dialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mocktest_list, container, false);
        ButterKnife.bind(this, v);
        loadScheduledTests();
        tvTitle.setText("Scheduled Exams");
        return v;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void showScheduledTests() {
        final LinearLayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvMockTestList.setLayoutManager(layoutManager);
        ScheduledTestsListAdapter scheduledTestsListAdapter = new ScheduledTestsListAdapter(mScheduledTestList, getActivity().getLayoutInflater());
        scheduledTestsListAdapter.setScheduledTestSelectedListener(this);
        rvMockTestList.setAdapter(scheduledTestsListAdapter);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            for (int i = 0; i < mScheduledTestList.MockTest.size(); i++) {
                Date scheduledTestTime = df.parse(mScheduledTestList.MockTest.get(i).startTime);
                examAdvancedNotification(mScheduledTestList.MockTest.get(i).testQuestionPaperId,
                                        mScheduledTestList.MockTest.get(i).examName,
                                        scheduledTestTime);
                examStartedNotification(mScheduledTestList.MockTest.get(i).testQuestionPaperId,
                                        mScheduledTestList.MockTest.get(i).examName,
                                        scheduledTestTime);
            }
        } catch (ParseException e) {
            L.error(e.getMessage(), e);
        }
    }

    private void examAdvancedNotification(String examId, String examName, Date scheduledTime) {
        long delay = 0;
        if(System.currentTimeMillis() < scheduledTime.getTime()) {
            delay = scheduledTime.getTime() - System.currentTimeMillis();
        }
        Intent broadCastIntent = new Intent(this.getActivity(), NotifyReceiver.class);
        broadCastIntent.putExtra("title", examName);
        broadCastIntent.putExtra("sub_title", "Exam starts at "+new SimpleDateFormat("hh:mm a").format(scheduledTime));
        broadCastIntent.putExtra("id", Data.getInt(examId));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getActivity(), (int)System.currentTimeMillis(),
                                broadCastIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)this.getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + delay,
                pendingIntent);
    }

    private void examStartedNotification(String examId, String examName, Date scheduledTime) {
        long delay = 0;
        if(System.currentTimeMillis() < scheduledTime.getTime()) {
            delay = scheduledTime.getTime() - System.currentTimeMillis();
        }
        Intent broadCastIntent = new Intent(this.getActivity(), NotifyReceiver.class);
        broadCastIntent.putExtra("title", examName);
        broadCastIntent.putExtra("sub_title", "Exam started at "+new SimpleDateFormat("hh:mm a").format(scheduledTime));
        broadCastIntent.putExtra("test_question_paper_id", examId);
        broadCastIntent.putExtra("id", Data.getInt(examId)+10);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getActivity(), (int)System.currentTimeMillis(),
                                broadCastIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)this.getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                SystemClock.elapsedRealtime() + delay,
                                pendingIntent);
    }

    @Override
    public void onScheduledTestSelected(int position) {
        dismiss();
        startScheduleTest(mScheduledTestList.MockTest.get(position));
    }

    private void startScheduleTest(ScheduledTestsArray exam) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long startTimeInMillis = df.parse(exam.startTime).getTime();
            if (startTimeInMillis < System.currentTimeMillis() + 1000*60) {
                Intent intent = new Intent(getActivity(), ExamEngineActivity.class);
                intent.putExtra(Constants.TEST_TITLE, "Scheduled Test");
                intent.putExtra("test_question_paper_id", exam.testQuestionPaperId);
                startActivity(intent);
                return;
            }
        } catch (ParseException e) {
            L.error(e.getMessage(), e);
        }
        Toast.makeText(getActivity(), "Please access the test during scheduled time", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSchedledDownload(int position) {
        ScheduledTestsArray exam = mScheduledTestList.MockTest.get(position);
        Intent intent = new Intent(getActivity(), TestDownloadService.class);
        intent.putExtra("testQuestionPaperId",exam.testQuestionPaperId);
        String scheduleTestStr = new Gson().toJson(exam);
        intent.putExtra("selectedScheduledTest",scheduleTestStr);
        getActivity().startService(intent);
        Toast.makeText(getActivity(), "Downloading Scheduled test paper in background", Toast.LENGTH_SHORT).show();
    }

    private void loadScheduledTests() {
        showProgress();
        ApiManager.getInstance(getActivity()).getScheduledTestsList(
                LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<ScheduledTestList>(getActivity()) {

                    @Override
                    public void success(ScheduledTestList scheduledTests, Response response) {
                        super.success(scheduledTests, response);
                        dialog.dismiss();
                        if (scheduledTests != null && scheduledTests.MockTest != null && !scheduledTests.MockTest.isEmpty()) {
                            mScheduledTestList = scheduledTests;
                            showScheduledTests();
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        ((AbstractBaseActivity) getActivity()).showToast("No scheduled tests available");
                        dialog.dismiss();
                        dismiss();
                    }
                });
    }

    public void showProgress(){
        ProgressBar pbar = new ProgressBar(getActivity());
        pbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(pbar);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
