package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.utils.Constants;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        showData();

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
                intent.putExtra(Constants.TEST_TITLE, "Mock Test");
                intent.putExtra("test_question_paper_id", testQuestionPaperId);
                intent.putExtra("Test_Instructions", new Gson().toJson(testPaperIndex));
                intent.putExtra("exam_template_id", testPaperIndex.examDetails.get(0).examTemplateId);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
            }
        });
    }

    private void showData() {
        title.setText(testPaperIndex.examDetails.get(0).examName);
        instuctions.loadData(testPaperIndex.examDetails.get(0).examInstucation , "text/html; charset=UTF-8", null);
        //instuctions.setText(Html.fromHtml(testPaperIndex.examDetails.get(0).examInstucation));
    }

    private void getBundleData() {
        String testString = getIntent().getStringExtra("Test_Instructions");
        testPaperIndex = new Gson().fromJson(testString, TestPaperIndex.class);
        testQuestionPaperId = getIntent().getStringExtra("test_question_paper_id");
    }

}
