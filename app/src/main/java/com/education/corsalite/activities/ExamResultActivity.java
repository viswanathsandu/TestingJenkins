package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;

import butterknife.ButterKnife;

public class ExamResultActivity extends AbstractBaseActivity {

    private int totalQuestions = 0;
    private int correct = 0;
    private int wrong = 0;
    private String exam = "";
    private String examTimeTaken = "";
    private String examRecommendedTime = "";
    private String examType = "";
    private String examDate = "";

    TextView recommendedTimeTxt;
    TextView timeTakenTxt;
    TextView totalQuestionTxt;
    TextView correctTxt;
    TextView wrongTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_exam_result, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        initUi();
        setToolbarTitle("Exam Results");
        loadIntentData();
        loadData();
    }

    private void initUi() {
        recommendedTimeTxt = (TextView) findViewById(R.id.tv_recommended_time);
        timeTakenTxt = (TextView) findViewById(R.id.tv_time_taken);
        totalQuestionTxt = (TextView) findViewById(R.id.tv_total_questions);
        correctTxt = (TextView) findViewById(R.id.tv_correct);
        wrongTxt = (TextView) findViewById(R.id.tv_wrong);
    }

    private void loadIntentData() {
        Bundle bundle = getIntent().getExtras();
        exam = bundle.getString("exam", "Chapter");
        examType = bundle.getString("type", "Custom");
        examRecommendedTime = bundle.getString("recommended_time", "00:00:00");
        examTimeTaken = bundle.getString("time_taken", "00:00:00");
        examDate  = bundle.getString("time_taken", "00:00:00");
        totalQuestions = bundle.getInt("total_questions", 0);
        correct = bundle.getInt("correct", 0);
        wrong = bundle.getInt("wrong", 0);
    }

    private void loadData() {
        recommendedTimeTxt.setText(examRecommendedTime);
        timeTakenTxt.setText(examTimeTaken);
        totalQuestionTxt.setText(totalQuestions+"");
        correctTxt.setText(correct+"");
        wrongTxt.setText(wrong+"");
    }
}
