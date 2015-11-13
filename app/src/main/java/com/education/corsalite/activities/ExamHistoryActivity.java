package com.education.corsalite.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamHistory;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 09/11/15.
 */
public class ExamHistoryActivity extends AbstractBaseActivity{


    @Bind(R.id.rv_exam_history)RecyclerView recyclerView;
    @Bind(R.id.progress_bar_tab)ProgressBar mProgressBar;
    @Bind(R.id.tv_failure_text)TextView mTextView;
    @Bind(R.id.header_exam_history)View mHeaderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_history);
        ButterKnife.bind(this);
        setToolbarForExamHistory();
        init();
    }

    private void init(){
        ApiManager.getInstance(this).getExamHistory(LoginUserCache.getInstance().loginResponse.studentId, "1", "100", new ApiCallback<List<ExamHistory>>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                mProgressBar.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void success(List<ExamHistory> examHistories, Response response) {
                super.success(examHistories, response);
                mProgressBar.setVisibility(View.GONE);
                mHeaderView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }
}
