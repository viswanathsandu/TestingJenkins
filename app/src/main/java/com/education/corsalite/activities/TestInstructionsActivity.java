package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamDetails;
import com.education.corsalite.models.responsemodels.QuestionPaperIndex;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.ExamUtils;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;
import com.education.corsalite.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by madhuri on 2/20/16.
 */
public class TestInstructionsActivity extends AbstractBaseActivity {

    @Bind(R.id.timer_layout)
    LinearLayout timerLayout;
    @Bind(R.id.days_layout)
    RelativeLayout daysLayout;
    @Bind(R.id.hours_layout)
    RelativeLayout hoursLayout;
    @Bind(R.id.minutes_layout)
    RelativeLayout minutesLayout;
    @Bind(R.id.secs_layout)
    RelativeLayout secondssLayout;
    @Bind(R.id.days_txt)
    TextView daysTxt;
    @Bind(R.id.hours_txt)
    TextView hoursTxt;
    @Bind(R.id.minutes_txt)
    TextView minutesTxt;
    @Bind(R.id.secs_txt)
    TextView secondsTxt;
    @Bind(R.id.webview)
    WebView instuctions;
    @Bind(R.id.bStart)
    Button btStart;
    @Bind(R.id.bCancel)
    Button btCancel;

    private LayoutInflater inflater;
    private TestPaperIndex testPaperIndex;
    private String testQuestionPaperId;
    private String testAnswerPaperId;
    private String testStatus;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_testpaperindex, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this, myView);
        setToolbarForTestIndexScreen();
        setListeners();
        getBundleData();
        fetchTestPaperIndex();
        hideDrawerIcon();
        if(getIntent().getExtras().getBoolean("is_for_information", false)) {
            btStart.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(testPaperIndex != null) {
            showData();
        }
    }

    private void setListeners() {
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToExamEngine();
            }
        });
    }

    private void navigateToExamEngine() {
        Intent intent = new Intent(TestInstructionsActivity.this, ExamEngineActivity.class);
        intent.putExtra(Constants.TEST_TITLE, getTestName());
        intent.putExtras(getIntent().getExtras());
        intent.putExtra("test_question_paper_id", testQuestionPaperId);
        intent.putExtra("test_answer_paper_id", testQuestionPaperId);
        intent.putExtra("Test_Instructions", Gson.get().toJson(testPaperIndex));
        try {
            intent.putExtra("exam_name", testPaperIndex.examDetails.get(0).examName);
            intent.putExtra("exam_template_id", testPaperIndex.examDetails.get(0).examTemplateId);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        startActivity(intent);
        finish();
    }

    private String getTestName() {
        if (testPaperIndex != null && testPaperIndex.examDetails != null && !testPaperIndex.examDetails.isEmpty()) {
            ExamDetails details = testPaperIndex.examDetails.get(0);
            if (details != null && !TextUtils.isEmpty(details.scheduledTime)) {
                return "Scheduled Test";
            } else if (fetchSections().size() > 1) {
                return "MockTest";
            } else {
                return "";
            }
        }
        return "";
    }

    private void showData() {
        setToolbarTitle(testPaperIndex.examDetails.get(0).examName);
        if (!TextUtils.isEmpty(testStatus) && testStatus.equalsIgnoreCase("Suspended")) {
            btStart.setText("Restart");
        } else {
            btStart.setText("Start");
        }
        if (testPaperIndex != null && testPaperIndex.examDetails != null && !testPaperIndex.examDetails.isEmpty()) {
            ExamDetails exam = testPaperIndex.examDetails.get(0);
            if(!TextUtils.isEmpty(exam.dueDateTime)) {
                long dueDateInMillis = TimeUtils.getMillisFromDate(exam.dueDateTime);
                if (dueDateInMillis < TimeUtils.currentTimeInMillis()) {
                    showToast("Exam time is over. Removed from offline screen");
                    new ExamUtils(this).deleteTestQuestionPaper(testQuestionPaperId);
                    finish();
                    return;
                }
            }
            if (exam != null && !TextUtils.isEmpty(exam.timeTostart)) {
                long scheduledTime = TimeUtils.getMillisFromDate(exam.scheduledTime);
                long timeToStart = scheduledTime - TimeUtils.currentTimeInMillis();
                if (timeToStart < 0) {
                    timerLayout.setVisibility(View.GONE);
                    btStart.setVisibility(View.VISIBLE);
                } else {
                    timerLayout.setVisibility(View.VISIBLE);
                    setTimer(timeToStart);
                    btStart.setVisibility(View.GONE);
                }
            }
            if(exam != null && exam.examInstucation != null) {
                String baseUrl = ApiClientService.getBaseUrl().replace("/v1", "");
                instuctions.loadDataWithBaseURL(baseUrl, testPaperIndex.examDetails.get(0).examInstucation, "text/html", "UTF-8", null);
            } else {
                navigateToExamEngine();
            }
        }
    }

    private void getBundleData() {
        testQuestionPaperId = getIntent().getStringExtra("test_question_paper_id");
        testAnswerPaperId = getIntent().getStringExtra("test_answer_paper_id");
        testStatus = getIntent().getStringExtra("test_status");
    }

    private List<String> fetchSections() {
        List sections = new ArrayList<>();
        if (testPaperIndex != null && testPaperIndex.questionPaperIndecies != null) {
            for (QuestionPaperIndex questionPaperIndex : testPaperIndex.questionPaperIndecies) {
                if (!TextUtils.isEmpty(questionPaperIndex.sectionName)) {
                    if (!sections.contains(questionPaperIndex.sectionName)) {
                        sections.add(questionPaperIndex.sectionName);
                    }
                }
            }
        }
        return sections;
    }


    private void fetchTestPaperIndex() {
        testPaperIndex = getLocalTestPaperIndex();
        if (testPaperIndex != null) {
            showData();
        } else if (SystemUtils.isNetworkConnected(this)) {
            showProgress();
            ApiManager.getInstance(this).getTestPaperIndex(testQuestionPaperId, testAnswerPaperId, "N",
                    new ApiCallback<TestPaperIndex>(this) {
                        @Override
                        public void success(TestPaperIndex testPaperIndex, Response response) {
                            super.success(testPaperIndex, response);
                            closeProgress();
                            TestInstructionsActivity.this.testPaperIndex = testPaperIndex;
                            showData();
                        }

                        @Override
                        public void failure(CorsaliteError error) {
                            super.failure(error);
                            closeProgress();
                            showToast("Failed to load exam");
                            finish();
                        }
                    });
        } else {
            showToast("Test is not available in offline. Please come online and take the test");
        }
    }

    private TestPaperIndex getLocalTestPaperIndex() {
        ExamUtils examUtils = new ExamUtils(this);
        TestPaperIndex testPaperIndex = examUtils.getTestPaperIndex(testQuestionPaperId);
        return testPaperIndex;
    }


    private void setTimer(long millis) {
        if(timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(millis, 1 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                if (days == 0) {
                    daysLayout.setVisibility(View.GONE);
                } else {
                    millisUntilFinished -= TimeUnit.DAYS.toMillis(days);
                    daysTxt.setText(days+"");
                }
                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                if (days == 0 && hours == 0) {
                    hoursLayout.setVisibility(View.GONE);
                } else {
                    millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);
                    hoursTxt.setText(String.format("%02d", hours));
                }
                long mins = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                if (days == 0 && hours == 0 && mins == 0) {
                    minutesLayout.setVisibility(View.GONE);
                } else {
                    millisUntilFinished -= TimeUnit.MINUTES.toMillis(mins);
                    minutesTxt.setText(String.format("%02d", mins));
                }
                long secs = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                secondsTxt.setText(String.format("%02d", secs));
            }

            @Override
            public void onFinish() {
                timerLayout.setVisibility(View.GONE);
                new Handler().post(runnable);
            }
        }.start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            navigateToExamEngine();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if(timer != null) {
            timer.cancel();
        }
    }
}
