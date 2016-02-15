package com.education.corsalite.fragments;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.adapters.MockTestsListAdapter;
import com.education.corsalite.adapters.ScheduledTestsListAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.MockTest;
import com.education.corsalite.models.ScheduledTestList;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ScheduledTest;
import com.education.corsalite.services.NotifyScheduledTest;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.NotifyReceiver;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        tvTitle.setText("Scheduled Tests");
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
//
            for (int i = 0; i < mScheduledTestList.MockTest.size(); i++) {
                Date scheduledTestTime = df.parse(mScheduledTestList.MockTest.get(i).startTime);
                Intent broadCastIntent = new Intent(this.getActivity(), NotifyReceiver.class);
                broadCastIntent.putExtra("Exam Name", mScheduledTestList.MockTest.get(i).examName);
                broadCastIntent.putExtra("Exam Id", mScheduledTestList.MockTest.get(i).testQuestionPaperId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getActivity(), 0, broadCastIntent, 0);

                AlarmManager alarmManager = (AlarmManager)this.getActivity().getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC, scheduledTestTime.getTime() - (10 * 60 * 1000), pendingIntent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onScheduledTestSelected(int position) {
        dismiss();
        /*Intent intent = new Intent(getActivity(), ExamEngineActivity.class);
        intent.putExtra(Constants.TEST_TITLE, "Scheduled Test");
        intent.putExtra("mock_tests_object", new Gson().toJson(mMockTestList.get(position)));
        startActivity(intent);*/
    }

    private void loadScheduledTests() {
        ApiManager.getInstance(getActivity()).getScheduledTestsList(
                LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<ScheduledTestList>(getActivity()) {

                    @Override
                    public void success(ScheduledTestList scheduledTests, Response response) {
                        super.success(scheduledTests, response);
                        if (scheduledTests != null && scheduledTests.MockTest != null && !scheduledTests.MockTest.isEmpty()) {
                            mScheduledTestList = scheduledTests;
                            showScheduledTests();
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        ((AbstractBaseActivity) getActivity()).showToast("No scheduled tests available");
                    }
                });
    }
}
