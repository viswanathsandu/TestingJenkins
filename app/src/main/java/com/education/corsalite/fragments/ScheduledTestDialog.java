package com.education.corsalite.fragments;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.adapters.ScheduledTestsListAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.event.ScheduledTestStartEvent;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.db.ScheduledTestList;
import com.education.corsalite.models.db.ScheduledTestsArray;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.services.TestDownloadService;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
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
        try {
            if (mScheduledTestList != null) {
                final LinearLayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                rvMockTestList.setLayoutManager(layoutManager);
                ScheduledTestsListAdapter scheduledTestsListAdapter = new ScheduledTestsListAdapter(mScheduledTestList, getActivity().getLayoutInflater());
                scheduledTestsListAdapter.setScheduledTestSelectedListener(this);
                rvMockTestList.setAdapter(scheduledTestsListAdapter);
                try {
                    ((AbstractBaseActivity) getActivity()).scheduleNotificationsForScheduledTests(mScheduledTestList);
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    @Override
    public void onScheduledTestSelected(int position) {
        dismiss();
        startScheduleTest(mScheduledTestList.MockTest.get(position));
    }

    private void startScheduleTest(ScheduledTestsArray exam) {
        try {
            ScheduledTestStartEvent event = new ScheduledTestStartEvent();
            event.testQuestionPaperId = exam.testQuestionPaperId;
            EventBus.getDefault().post(event);
            return;
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        Toast.makeText(getActivity(), "Please access the test during scheduled time", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSchedledDownload(int position) {
        try {
            if(SystemUtils.isServiceRunning(getActivity(), TestDownloadService.class)) {
                EventBus.getDefault().post(new com.education.corsalite.event.Toast("Currently downloads are in progress. Please try again"));
                return;
            }
            ScheduledTestsArray exam = mScheduledTestList.MockTest.get(position);
            Intent intent = new Intent(getActivity(), TestDownloadService.class);
            intent.putExtra("testQuestionPaperId", exam.testQuestionPaperId);
            intent.putExtra("studentId", LoginUserCache.getInstance().getStudentId());
            intent.putExtra("selectedScheduledTest", Gson.get().toJson(exam));
            getActivity().startService(intent);
            Toast.makeText(getActivity(), "Downloading Scheduled test paper in background", Toast.LENGTH_SHORT).show();
            dismiss();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void loadScheduledTests() {
        showProgress();
        ApiManager.getInstance(getActivity()).getScheduledTestsList(
            LoginUserCache.getInstance().getStudentId(),
            new ApiCallback<ScheduledTestList>(getActivity()) {

                @Override
                public void success(ScheduledTestList scheduledTests, Response response) {
                    super.success(scheduledTests, response);
                    if(getActivity() != null) {
                        dialog.dismiss();
                        if (scheduledTests != null && scheduledTests.MockTest != null && !scheduledTests.MockTest.isEmpty()) {
                            ApiCacheHolder.getInstance().setScheduleTestsResponse(scheduledTests);
                            SugarDbManager.get(getActivity()).saveReqRes(ApiCacheHolder.getInstance().scheduleTests);
                            mScheduledTestList = scheduledTests;
                            showScheduledTests();
                        }
                    }
                }

                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    try {
                        dialog.dismiss();
                        dismiss();
                        ((AbstractBaseActivity) getActivity()).showToast("No scheduled tests available");
                    } catch (Exception e) {
                        L.error(e.getMessage(), e);
                    }
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
