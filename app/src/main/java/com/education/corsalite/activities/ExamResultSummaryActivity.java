package com.education.corsalite.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.databinding.ActivityExamResultSummaryBinding;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamResultSummaryResponse;
import com.education.corsalite.utils.SystemUtils;

import butterknife.ButterKnife;
import retrofit.client.Response;

public class ExamResultSummaryActivity extends AbstractBaseActivity {

    ActivityExamResultSummaryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_exam_result_summary, null);
        frameLayout.addView(myView);
        binding = ActivityExamResultSummaryBinding.inflate(inflater, myView, false);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String testAnswerPaperIds = getIntent().getExtras().getString("test_answer_papaer_ids");
        fetchExamSummaryData(testAnswerPaperIds);
    }

    private void fetchExamSummaryData(String testAnswerPaperIds) {
        if(SystemUtils.isNetworkConnected(this)) {
            ApiManager.getInstance(this).getExamResultSummaryData(testAnswerPaperIds, LoginUserCache.getInstance().getStudentId(),
                    new ApiCallback<ExamResultSummaryResponse>(ExamResultSummaryActivity.this) {
                        @Override
                        public void failure(CorsaliteError error) {
                            super.failure(error);
                            showToast("Failed to fetch exam result summary");
                        }

                        @Override
                        public void success(ExamResultSummaryResponse examResultSummaryResponse, Response response) {
                            super.success(examResultSummaryResponse, response);
                            showToast("Received the exam result summary data");
                            binding.setExam(examResultSummaryResponse.details);
                        }
                    });
        } else {
            // TODO : load results from local data
        }
    }
}
