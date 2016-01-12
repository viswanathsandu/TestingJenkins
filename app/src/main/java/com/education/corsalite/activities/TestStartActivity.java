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
        toolbar.findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest();
            }
        });

        initializeGraph();
        loadDataFromIntent();
        fetchDataFromServer();
    }

    private void initializeGraph() {
        mTestBarChart.setDescription("");
        mTestBarChart.setBackgroundColor(Color.WHITE);
        mTestBarChart.setDrawGridBackground(false);

        YAxis rightAxis = mTestBarChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setEnabled(false);

        YAxis leftAxis = mTestBarChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        XAxis xAxis = mTestBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        mTestBarChart.getLegend().setTextSize(15f);
        mTestBarChart.getLegend().setCustom(new int[]{getResources().getColor(R.color.green), getResources().getColor(R.color.red), getResources().getColor(R.color.blue)},
                getResources().getStringArray(R.array.label_questions));
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
                    }

                    @Override
                    public void success(List<TestCoverage> testCoverages, Response response) {
                        super.success(testCoverages, response);
                        mProgressBar.setVisibility(View.GONE);
                        mContainerLayout.setVisibility(View.VISIBLE);
                        setData(testCoverages);
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

            columnNames.add(testCoverage.level);

            BarEntry barEntry = new BarEntry(new float[]{answersCorrect, answersRemaining, answersWrong}, index++);
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(new int[]{getResources().getColor(R.color.green), getResources().getColor(R.color.red), getResources().getColor(R.color.blue)});
        barDataSet.setValueTextColor(Color.rgb(60, 220, 78));
        barDataSet.setValueTextSize(10f);
        barDataSet.setBarSpacePercent(20f);

        BarData barData = new BarData(columnNames);
        barData.addDataSet(barDataSet);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return barData;
    }

    private void setData(List<TestCoverage> testCoverages) {
        mChapterNameTxtView.setText(getString(R.string.value_test_chapter_name, chapterName));
        mNoteTxtView.setText(getString(R.string.value_test_note));
        mTestBarChart.setData(generateBarData(testCoverages));
        mTestBarChart.invalidate();
    }

    private void startTest() {
        startActivity(ExerciseActivity.getMyIntent(this, getIntent().getExtras()));
        finish();
    }
}
