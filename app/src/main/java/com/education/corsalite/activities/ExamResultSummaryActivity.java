package com.education.corsalite.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.fragments.ExamResultSummaryGraphFragment;
import com.education.corsalite.fragments.ExamResultSummaryMultiLineFragment;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.db.ExamResultDetails;
import com.education.corsalite.models.db.ExamResultSummarySubject;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamResultSummaryResponse;
import com.education.corsalite.utils.SystemUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class ExamResultSummaryActivity extends AbstractBaseActivity {

    @Bind(R.id.tv_examname) TextView examNameTxt;
    @Bind(R.id.tv_type) TextView typeTxt;
    @Bind(R.id.tv_time_taken) TextView timeTakenTxt;
    @Bind(R.id.tv_date) TextView dateTxt;
    @Bind(R.id.result_table_layout) TableLayout resultTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_exam_result_summary, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String testAnswerPaperIds = getIntent().getExtras().getString("test_answer_papaer_ids");
        fetchExamSummaryData(testAnswerPaperIds);

        // TODO : loading dummy fragment
      //  getSupportFragmentManager().beginTransaction().add(R.id.graph_container, new ExamResultSummaryGraphFragment(), "Summary").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.graph_container, new ExamResultSummaryMultiLineFragment(), "Summary").commit();

    }

    private void fetchExamSummaryData(String testAnswerPaperIds) {
        if(SystemUtils.isNetworkConnected(this)) {
            ApiManager.getInstance(this).getExamResultSummaryData(testAnswerPaperIds, LoginUserCache.getInstance().getStudentId(),
                    new ApiCallback<ExamResultSummaryResponse>(ExamResultSummaryActivity.this) {
                        @Override
                        public void failure(CorsaliteError error) {
                            super.failure(error);
                            showToast("Failed to fetch exam result summary");
                            // TODO : remove it after testing
                            // even if the api call is failed due to any issue, we need to run with dummy data
                            ExamResultSummaryResponse response = getDummyResponse();
                            updateExamDetails(response.details);
                            updateResultTable(response.examResultSummary);
                        }

                        @Override
                        public void success(ExamResultSummaryResponse examResultSummaryResponse, Response response) {
                            super.success(examResultSummaryResponse, response);
                            showToast("Received the exam result summary data");
                            updateExamDetails(examResultSummaryResponse.details);
                            updateResultTable(examResultSummaryResponse.examResultSummary);
                        }
                    });
        } else {
            // TODO : load results from local data
        }
    }

    private void updateExamDetails(ExamResultDetails details) {
        examNameTxt.setText(details.examName);
        typeTxt.setText(details.templateType);
        dateTxt.setText(details.examDate);
        timeTakenTxt.setText(details.timeTaken);
    }

    private void updateResultTable(List<ExamResultSummarySubject> subjects) {
        for(ExamResultSummarySubject subject : subjects) {
            resultTable.addView(getTableRow(subject));
        }
    }

    private View getTableRow(ExamResultSummarySubject subject) {
        View tableRow = getLayoutInflater().inflate(R.layout.exam_result_summary_table_row, null, false);
        ((TextView)tableRow.findViewById(R.id.subject_name_txt)).setText(subject.subjectName);
        ((TextView)tableRow.findViewById(R.id.total_questions_txt)).setText(subject.questionTotal);
        ((TextView)tableRow.findViewById(R.id.correct_questions_txt)).setText(subject.questionCorrect);
        ((TextView)tableRow.findViewById(R.id.wrong_questions_txt)).setText(subject.questionWrong);
        ((TextView)tableRow.findViewById(R.id.max_marks_txt)).setText(subject.markMax);
        ((TextView)tableRow.findViewById(R.id.positive_marks_txt)).setText(subject.scorePositive);
        ((TextView)tableRow.findViewById(R.id.negative_marks_txt)).setText(subject.scoreNegative);
        ((TextView)tableRow.findViewById(R.id.total_marks_txt)).setText(subject.scoreTotal);
        ((TextView)tableRow.findViewById(R.id.recommended_time_txt)).setText(subject.timeRecommended);
        ((TextView)tableRow.findViewById(R.id.actual_time_txt)).setText(subject.timeTaken);
        return tableRow;
    }

    private ExamResultSummaryResponse getDummyResponse() {
        return Gson.get().fromJson("{\n" +
                "    \"examResultDetails\": {\n" +
                "        \"TestName\": \"Chapter\", \n" +
                "        \"TemplateType\": \"Custom\", \n" +
                "        \"TimeTaken\": \"00:00:42\", \n" +
                "        \"examDate\": \"09/08/2017 12:43:22\", \n" +
                "        \"Rank\": \"\"\n" +
                "    }, \n" +
                "    \"examResultSumm\": [\n" +
                "        {\n" +
                "            \"idSubject\": \"195\", \n" +
                "            \"SubjectName\": \"Physics\", \n" +
                "            \"QuestionTotal\": \"20\", \n" +
                "            \"QuestionCorrect\": \"4\", \n" +
                "            \"QuestionWrong\": 16, \n" +
                "            \"MarkMax\": 20, \n" +
                "            \"ScorePositive\": 4, \n" +
                "            \"ScoreNegative\": 0, \n" +
                "            \"ScoreTotal\": 4, \n" +
                "            \"PeerAvgScore\": \"N/A\", \n" +
                "            \"Percentile\": \"N/A\", \n" +
                "            \"TimeRecommended\": \"00:10:00\", \n" +
                "            \"TimeTaken\": \"00:00:42\", \n" +
                "            \"PeerAvgTimeTaken\": \"N/A\", \n" +
                "            \"sectionData\": [\n" +
                "                {\n" +
                "                    \"SectionName\": \"Physics\", \n" +
                "                    \"QuestionTotal\": \"20\", \n" +
                "                    \"QuestionCorrect\": \"4\", \n" +
                "                    \"QuestionWrong\": 16, \n" +
                "                    \"MarkMax\": 20, \n" +
                "                    \"ScorePositive\": 4, \n" +
                "                    \"ScoreNegative\": 0, \n" +
                "                    \"ScoreTotal\": 4, \n" +
                "                    \"PeerAvgScore\": \"N/A\", \n" +
                "                    \"Percentile\": \"N/A\", \n" +
                "                    \"TimeRecommended\": \"00:10:00\", \n" +
                "                    \"TimeTaken\": \"00:00:42\", \n" +
                "                    \"PeerAvgTimeTaken\": \"N/A\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ], \n" +
                "    \"percentileChartData\": {\n" +
                "        \"195\": {\n" +
                "            \"studentAccuracy\": 20, \n" +
                "            \"SubjectName\": \"Physics\"\n" +
                "        }\n" +
                "    }, \n" +
                "    \"subjectGraphsData\": {\n" +
                "        \"parttestDataArr\": null, \n" +
                "        \"levelWiseChartData\": null, \n" +
                "        \"examResultSummJson\": {\n" +
                "            \"195\": {\n" +
                "                \"QuestionsJson\": [\n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    3, \n" +
                "                    4, \n" +
                "                    5, \n" +
                "                    6, \n" +
                "                    7, \n" +
                "                    8, \n" +
                "                    9, \n" +
                "                    10, \n" +
                "                    11, \n" +
                "                    12, \n" +
                "                    13, \n" +
                "                    14, \n" +
                "                    15, \n" +
                "                    16, \n" +
                "                    17, \n" +
                "                    18, \n" +
                "                    19, \n" +
                "                    20\n" +
                "                ], \n" +
                "                \"MarkTotalJson\": [\n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    1\n" +
                "                ], \n" +
                "                \"MarkMaxJson\": [\n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1\n" +
                "                ], \n" +
                "                \"PeerAvgScoreJson\": [\n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    0, \n" +
                "                    1, \n" +
                "                    0, \n" +
                "                    1\n" +
                "                ], \n" +
                "                \"TimeTakenJson\": [\n" +
                "                    3, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    3, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    3\n" +
                "                ], \n" +
                "                \"LevelJson\": [\n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1\n" +
                "                ], \n" +
                "                \"TimeRecommendedJson\": [\n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30, \n" +
                "                    30\n" +
                "                ], \n" +
                "                \"PeerAvgTimeTakenJson\": [\n" +
                "                    3, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    3, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    1, \n" +
                "                    1, \n" +
                "                    2, \n" +
                "                    3\n" +
                "                ]\n" +
                "            }\n" +
                "        }, \n" +
                "        \"examResultSummGraph\": null, \n" +
                "        \"topperStudentGraph\": null\n" +
                "    }\n" +
                "}\n", ExamResultSummaryResponse.class);
    }
}
