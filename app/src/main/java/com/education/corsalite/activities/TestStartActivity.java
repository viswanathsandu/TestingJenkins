package com.education.corsalite.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.TestCoverage;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.Data;
import com.education.corsalite.utils.L;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.client.Response;

public class TestStartActivity extends AbstractBaseActivity {

    @Bind(R.id.bar_chart_test_start)
    BarChart mTestBarChart;
    @Bind(R.id.ll_container)
    LinearLayout mContainerLayout;
    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;
    @Bind(R.id.txt_view_test_start_chapter_name)
    TextView mChapterNameTxtView;
    @Bind(R.id.tv_failure_text)
    TextView mFailureTextView;
    @Bind(R.id.txt_view_test_start_note)
    TextView mNoteTxtView;

    private String chapterID, chapterName, subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_test_start, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        setToolbarForTestStartScreen();
        //Hide the toolabar button.
        toolbar.findViewById(R.id.start_btn).setVisibility(View.GONE);

        initializeGraph();
        loadDataFromIntent();
        fetchDataFromServer();
    }

    @OnClick({R.id.btn_header_test_cancel, R.id.btn_header_test_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_header_test_cancel : {
                onBackPressed();
                break;
            }
            case R.id.btn_header_test_next : {
                startActivity(ExerciseActivity.getMyIntent(this, getIntent().getExtras()));
                finish();
                break;
            }
        }
    }

    private void initializeGraph() {
        mTestBarChart.setBackgroundColor(Color.WHITE);
        mTestBarChart.setDescription("");
        mTestBarChart.setDrawBarShadow(false);
        mTestBarChart.setDrawGridBackground(false);

        YAxis rightAxis = mTestBarChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setEnabled(false);

        YAxis leftAxis = mTestBarChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextSize(15f);

        XAxis xAxis = mTestBarChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(15f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        mTestBarChart.getLegend().setTextSize(15f);
        mTestBarChart.getLegend().setEnabled(false);
        mTestBarChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
    }

    private void fetchDataFromServer() {
        ApiManager.getInstance(this).getTestCoverage(LoginUserCache.getInstance().loginResponse.studentId, AbstractBaseActivity.selectedCourse.courseId.toString(), subjectId, chapterID,
                new ApiCallback<List<TestCoverage>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        L.error(error.message);
                        mProgressBar.setVisibility(View.GONE);
                        mFailureTextView.setText("Sorry, couldn't fetch data");
                    }

                    @Override
                    public void success(List<TestCoverage> testCoverages, Response response) {
                        super.success(testCoverages, response);
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        setData(testCoverages);
                        mProgressBar.setVisibility(View.GONE);
                        mContainerLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void loadDataFromIntent() {
        Bundle bundle = getIntent().getExtras();
        chapterID = bundle.getString(Constants.SELECTED_CHAPTERID, "");
        chapterName = bundle.getString(Constants.SELECTED_CHAPTER_NAME, "");
        subjectId = bundle.getString(Constants.SELECTED_SUBJECTID, "");
        if (TextUtils.isEmpty(chapterID) || TextUtils.isEmpty(chapterName) || TextUtils.isEmpty(subjectId)) {
            //In case data is missing finish this activity,
            finish();
        }
    }

    private BarData generateBarData(List<TestCoverage> testCoverages) {
        int index = 0;
        List<String> columnNames = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (TestCoverage testCoverage : testCoverages) {
            float answersCorrect = Data.getInt(testCoverage.attendedCorrectQCount);
            float answersRemaining = Data.getInt(testCoverage.questionCount) - Data.getInt(testCoverage.attendedQCount);
            float answersWrong = Data.getInt(testCoverage.attendedQCount) - Data.getInt(testCoverage.attendedCorrectQCount);

            columnNames.add("Level " + testCoverage.level);

            BarEntry barEntry = new BarEntry(new float[]{answersCorrect, answersRemaining, answersWrong}, index++);
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(new int[]{getResources().getColor(R.color.green), getResources().getColor(R.color.red), getResources().getColor(R.color.skyblue)});
        barDataSet.setDrawValues(false);
        barDataSet.setBarSpacePercent(20f);

        BarData barData = new BarData(columnNames);
        barData.addDataSet(barDataSet);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return barData;
    }

    private void setData(List<TestCoverage> testCoverages) {
        mChapterNameTxtView.setText(getString(R.string.value_test_chapter_name, chapterName));
        mNoteTxtView.setText(getNote());
        mTestBarChart.setData(generateBarData(testCoverages));
        mTestBarChart.invalidate();
    }

    private CharSequence getNote() {
        String noteValue = getString(R.string.value_test_note);
        return TextUtils.concat(Data.getBoldString(getString(R.string.label_note)), noteValue);
    }
}
