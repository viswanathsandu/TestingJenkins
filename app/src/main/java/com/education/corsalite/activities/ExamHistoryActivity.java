package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.ExamHistoryAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.ExamHistory;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;
import com.education.corsalite.utils.TimeUtils;
import com.education.corsalite.utils.WebUrls;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 09/11/15.
 */
public class ExamHistoryActivity extends AbstractBaseActivity implements ExamHistoryAdapter.SetOnExamHistoryClick {

    @Bind(R.id.rv_exam_history)
    RecyclerView recyclerView;
    @Bind(R.id.progress_bar_tab)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_failure_text)
    TextView mTextView;
    private LinearLayoutManager mLayoutManager;
    private static final int MAX_ROW_COUNT = 10;
    private boolean mLoading = true;
    private int currentPage = 0;
    private ExamHistoryAdapter examHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_exam_history, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        setToolbarForExamHistory();
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onEvent(Course course) {
        super.onEvent(course);
        getExamHistory();
    }

    private void getExamHistory() {
        mProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        ApiManager.getInstance(this).getExamHistory(getSelectedCourseId(), LoginUserCache.getInstance().getStudentId(),
                null, null,
                new ApiCallback<List<ExamHistory>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        mProgressBar.setVisibility(View.GONE);
                        mTextView.setVisibility(View.VISIBLE);
                        mLoading = false;
                    }

                    @Override
                    public void success(List<ExamHistory> examHistories, Response response) {
                        mLoading = false;
                        super.success(examHistories, response);
                        mProgressBar.setVisibility(View.GONE);
                        if (examHistories == null || examHistories.isEmpty()) {
                            mTextView.setVisibility(View.VISIBLE);
                            return;
                        }
                        recyclerView.setVisibility(View.VISIBLE);

                        if (examHistoryAdapter == null) {
                            examHistoryAdapter = new ExamHistoryAdapter(examHistories, getLayoutInflater(), ExamHistoryActivity.this);
                            recyclerView.setAdapter(examHistoryAdapter);
                        } else {
                            examHistoryAdapter.addAll(examHistories);
                            examHistoryAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        ExamHistory examHistory = (ExamHistory) examHistoryAdapter.getItem(position);
        if (SystemUtils.isNetworkConnected(this)) {
            if ((!TextUtils.isEmpty(examHistory.status) && examHistory.status.equalsIgnoreCase("Not Started"))
                    || (!TextUtils.isEmpty(examHistory.totalScore) && examHistory.status.equalsIgnoreCase("Suspended"))) {
                if (!TextUtils.isEmpty(examHistory.dueDate) && TimeUtils.currentTimeInMillis() >= TimeUtils.getMillisFromDate(examHistory.dueDate)) {
                    showToast("Test could not be started as the due date is exceeded");
                    return;
                }
                Intent intent = new Intent(ExamHistoryActivity.this, TestInstructionsActivity.class);
                intent.putExtra("exam_name", examHistory.examName);
                intent.putExtra("test_question_paper_id", examHistory.idTestQuestionPaper);
                intent.putExtra("test_answer_paper_id", examHistory.idTestAnswerPaper);
                intent.putExtra("test_status", "Suspended");
                startActivity(intent);
                return;
            } else {
                // TODO : testing purpose
                Intent intent = new Intent(this, ExamResultSummaryActivity.class);
                L.info("URL : " + WebUrls.getExamResultsSummaryUrl() + examHistory.idTestAnswerPaper);
                intent.putExtra("test_answer_papaer_ids", examHistory.idTestAnswerPaper);
                startActivity(intent);
/*
                Intent intent = new Intent(this, WebviewActivity.class);
                L.info("URL : " + WebUrls.getExamResultsSummaryUrl() + examHistory.idTestAnswerPaper);
                intent.putExtra(LoginActivity.URL, WebUrls.getExamResultsSummaryUrl() + examHistory.idTestAnswerPaper);
                intent.putExtra(LoginActivity.TITLE, getString(R.string.results));
                startActivity(intent);
                */
            }
        }
    }
}
