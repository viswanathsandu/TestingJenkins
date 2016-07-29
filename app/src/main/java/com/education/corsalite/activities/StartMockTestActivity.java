package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamDetails;
import com.education.corsalite.models.responsemodels.QuestionPaperIndex;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.ExamUtils;
import com.education.corsalite.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by madhuri on 2/20/16.
 */
public class StartMockTestActivity extends AbstractBaseActivity {

    @Bind(R.id.webview)WebView instuctions;
    @Bind(R.id.tv_title)TextView title;
    @Bind(R.id.bStart)Button btStart;
    @Bind(R.id.bCancel)Button btCancel;

    private LayoutInflater inflater;
    private TestPaperIndex testPaperIndex;
    private String testQuestionPaperId;
    private String testAnswerPaperId;
    private String testStatus;

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
                Intent intent = new Intent(StartMockTestActivity.this, ExamEngineActivity.class);

                intent.putExtra(Constants.TEST_TITLE, getTestName());
                intent.putExtra("test_question_paper_id", testQuestionPaperId);
                intent.putExtra("test_answer_paper_id", testQuestionPaperId);
                intent.putExtra("Test_Instructions", Gson.get().toJson(testPaperIndex));
                intent.putExtra("exam_template_id", testPaperIndex.examDetails.get(0).examTemplateId);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
            }
        });
    }

    private String getTestName() {
        if(testPaperIndex != null && testPaperIndex.examDetails != null && !testPaperIndex.examDetails.isEmpty()) {
            ExamDetails details = testPaperIndex.examDetails.get(0);
            if(details != null && !TextUtils.isEmpty(details.scheduledTime)) {
                return "Scheduled Test";
            } else if(fetchSections().size() > 1){
                return "MockTest";
            } else {
                return "";
            }
        }
        return "";
    }

    private void showData() {
        title.setText(testPaperIndex.examDetails.get(0).examName);
        if(!TextUtils.isEmpty(testStatus) && testStatus.equalsIgnoreCase("Suspended")) {
            btStart.setText("Restart");
        } else {
            btStart.setText("Start");
        }
        instuctions.loadData(testPaperIndex.examDetails.get(0).examInstucation , "text/html; charset=UTF-8", null);
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
        if(testPaperIndex != null) {
            showData();
        } else if(SystemUtils.isNetworkConnected(this)){
            showProgress();
            ApiManager.getInstance(this).getTestPaperIndex(testQuestionPaperId, testAnswerPaperId, "N",
                new ApiCallback<TestPaperIndex>(this) {
                    @Override
                    public void success(TestPaperIndex testPaperIndex, Response response) {
                        super.success(testPaperIndex, response);
                        closeProgress();
                        StartMockTestActivity.this.testPaperIndex = testPaperIndex;
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


}
