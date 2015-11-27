package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.ExamHistoryAdapter;
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
public class ExamHistoryActivity extends AbstractBaseActivity {

    @Bind(R.id.rv_exam_history)RecyclerView recyclerView;
    @Bind(R.id.progress_bar_tab)ProgressBar mProgressBar;
    @Bind(R.id.tv_failure_text)TextView mTextView;
    @Bind(R.id.headerLayout)LinearLayout mHeaderLayout;
    LinearLayoutManager mLayoutManager;
    private static final int MAX_ROW_COUNT = 10;
    private boolean mLoading = true;
    private int currentPage = 0;

    ExamHistoryAdapter examHistoryAdapter;
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int totalItem = mLayoutManager.getItemCount();
                    int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                    if (!mLoading && lastVisibleItem == totalItem - 1) {
                        mLoading = true;
                        currentPage++;
                        // Scrolled to bottom. Do something here.
                        getExamHistory((currentPage * MAX_ROW_COUNT) + 1, MAX_ROW_COUNT);
                    }
                }
            }
        });

        getExamHistory(1, MAX_ROW_COUNT);
    }


    private void getExamHistory(final int startIndex, final int endIndex){
        ApiManager.getInstance(this).getExamHistory(LoginUserCache.getInstance().loginResponse.studentId, String.valueOf(startIndex), String.valueOf(endIndex), new ApiCallback<List<ExamHistory>>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                mProgressBar.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
                mLoading = false;
            }

            @Override
            public void success(List<ExamHistory> examHistories, Response response) {
                if(examHistories == null){
                    return;
                }
                mLoading = false;
                super.success(examHistories, response);
                mProgressBar.setVisibility(View.GONE);
                mHeaderLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                if(examHistoryAdapter == null){
                    examHistoryAdapter = new ExamHistoryAdapter(examHistories, getLayoutInflater());
                    recyclerView.setAdapter(examHistoryAdapter);
                }else{
                    examHistoryAdapter.addAll(examHistories);
                    examHistoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }


}
