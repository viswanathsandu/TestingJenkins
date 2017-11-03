package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.TimeUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExamResultActivity extends AbstractBaseActivity {

    private int totalQuestions = 0;
    private int answered = 0;
    private int skipped = 0;
    private int correct = 0;
    private int wrong = 0;
    private String exam = "";
    private String examname = "";
    private String examTimeTaken = "";
    private String examRecommendedTime = "";
    private String examType = "";
    private String examDate = "";
    private long dueDateInMillis;

    @Bind(R.id.exam_name_txt)
    TextView examNameTxt;
    @Bind(R.id.date_txt)
    TextView examDateTxt;
    @Bind(R.id.recommended_time_txt)
    TextView recommendedTimeTxt;
    @Bind(R.id.time_taken_txt)
    TextView timeTakenTxt;
    @Bind(R.id.total_questions_txt)
    TextView totalQuestionTxt;
    @Bind(R.id.answered_txt)
    TextView answeredTxt;
    @Bind(R.id.skipped_txt)
    TextView skippedTxt;
    @Bind(R.id.correct_txt)
    TextView correctTxt;
    @Bind(R.id.wrong_txt)
    TextView wrongTxt;

    private static List<ExamModel> examModels;

    public static void setSharedExamModels(List<ExamModel> examModels) {
        ExamResultActivity.examModels = examModels;
    }

    public static List<ExamModel> getSharedExamModels() {
        return ExamResultActivity.examModels;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.activity_exam_result, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        setToolbarTitle("Exam Results");
        loadIntentData();
        loadData();
        showViewAnswersButton();
    }

    private void showViewAnswersButton() {
        if (dueDateInMillis == 0 || dueDateInMillis < TimeUtils.currentTimeInMillis()) {
            findViewById(R.id.view_answers_btn).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.view_answers_btn).setVisibility(View.GONE);
        }
    }

    private void loadIntentData() {
        Bundle bundle = getIntent().getExtras();
        exam = bundle.getString("exam", "Chapter");
        examname = bundle.getString("exam_name", "Exam Result");
        examType = bundle.getString("type", "Custom");
        examRecommendedTime = bundle.getString("recommended_time", "--:--:--");
        examTimeTaken = bundle.getString("time_taken", "--:--:--");
        examDate = bundle.getString("exam_date", "--/--/----");
        totalQuestions = bundle.getInt("total_questions", 0);
        answered = bundle.getInt("answered_questions", 0);
        skipped = bundle.getInt("skipped_questions", 0);
        // TODO : Hack to for skipped fix
        if ((skipped + answered) != totalQuestions) {
            skipped = totalQuestions - answered;
        }
        correct = bundle.getInt("correct", 0);
        wrong = bundle.getInt("wrong", 0);
        dueDateInMillis = bundle.getLong("due_date_millis", 0);
    }

    private void loadData() {
        examNameTxt.setText(examname);
        examDateTxt.setText(examDate);
        recommendedTimeTxt.setText(examRecommendedTime);
        timeTakenTxt.setText(examTimeTaken);
        totalQuestionTxt.setText(totalQuestions + "");
        answeredTxt.setText(answered + "");
        skippedTxt.setText(skipped + "");
        correctTxt.setText(correct + "");
        wrongTxt.setText(wrong + "");
    }

    @OnClick({R.id.view_answers_btn, R.id.exam_history_btn, R.id.course_analysis_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_answers_btn:
                if (examModels != null) {
                    AbstractBaseActivity.setSharedExamModels(examModels);
                }
                Bundle bundle = new Bundle();
                bundle.putString(Constants.TEST_TITLE, "View Answers");
                startActivity(ExamEngineActivity.getMyIntent(this, bundle));
                break;
            case R.id.exam_history_btn:
                startActivity(new Intent(this, ExamHistoryActivity.class));
                break;
            case R.id.course_analysis_btn:
                startActivity(new Intent(this, AnalyticsActivity.class));
                break;
        }
    }
}
