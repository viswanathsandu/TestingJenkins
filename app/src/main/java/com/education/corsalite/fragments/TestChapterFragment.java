package com.education.corsalite.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.gson.Gson;
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

/**
 * Created on 23/01/16.
 *
 * @author Meeth D Jain
 */
public class TestChapterFragment extends BaseFragment {

    @Bind(R.id.bar_chart_test_start)
    BarChart mTestBarChart;
    @Bind(R.id.ll_container)
    LinearLayout mContainerLayout;
    @Bind(R.id.txt_view_test_start_chapter_name)
    TextView mChapterNameTxtView;
    @Bind(R.id.tv_failure_text)
    TextView mFailureTextView;
    @Bind(R.id.txt_view_test_start_note)
    TextView mNoteTxtView;

    private ArrayList<String> mChapterLevels = new ArrayList<>();
    private Bundle mExtras;
    private String chapterID, chapterName, subjectId;
    private int levelCrossed;
    private List<TestCoverage> testCoverages;

    public static TestChapterFragment newInstance(Bundle bundle) {
        TestChapterFragment fragment = new TestChapterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static String getMyTag() {
        return "tag_fragment_test_chapter";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtras = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_chapter, container, false);
        ButterKnife.bind(this, rootView);
        initializeGraph();
        loadDataFromIntent();
        fetchDataFromServer();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity() != null && getActivity() instanceof AbstractBaseActivity) {
            ((AbstractBaseActivity) getActivity()).hideKeyboard();
        }
    }

    @OnClick({R.id.btn_header_test_cancel, R.id.btn_header_test_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_header_test_cancel : {
                getActivity().onBackPressed();
                break;
            }
            case R.id.btn_header_test_next : {
                setupTest();
                break;
            }
        }
    }

    private void loadDataFromIntent() {
        chapterID = mExtras.getString(Constants.SELECTED_CHAPTERID, "");
        chapterName = mExtras.getString(Constants.SELECTED_CHAPTER_NAME, "");
        subjectId = mExtras.getString(Constants.SELECTED_SUBJECTID, "");
        levelCrossed = mExtras.getInt(Constants.LEVEL_CROSSED, 0) + 1;
        String chapterStr = mExtras.getString("chapter");
        if (TextUtils.isEmpty(chapterID) || TextUtils.isEmpty(chapterName) || TextUtils.isEmpty(subjectId)) {
            //In case data is missing finish this activity,
            getActivity().finish();
        }
    }

    private void fetchDataFromServer() {
        showProgress();
        ApiManager.getInstance(getActivity()).getTestCoverage(LoginUserCache.getInstance().getStudentId(), AbstractBaseActivity.getSelectedCourseId(), subjectId, chapterID,
            new ApiCallback<List<TestCoverage>>(getActivity()) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    closeProgress();
                    L.error(error.message);
                    mFailureTextView.setText("Sorry, couldn't fetch data");
                }

                @Override
                public void success(List<TestCoverage> testCoverages, Response response) {
                    super.success(testCoverages, response);
                    if (getActivity() != null && getActivity().isFinishing() || getActivity().isDestroyed() || !isResumed()) {
                        return;
                    }
                    closeProgress();
                    TestChapterFragment.this.testCoverages = testCoverages;
                    setData(testCoverages);
                    mContainerLayout.setVisibility(View.VISIBLE);
                }
            });
    }

    private void setData(List<TestCoverage> testCoverages) {
        for (TestCoverage coverage : testCoverages) {
            if(coverage.idCourseSubjectChapter.equalsIgnoreCase(chapterID)) {
                mChapterNameTxtView.setText(chapterName);
                mNoteTxtView.setText(getNote(levelCrossed, coverage.attendedQCount, coverage.attendedCorrectQCount));
                mTestBarChart.setData(generateBarData(testCoverages));
                mTestBarChart.invalidate();
                break;
            }
        }
    }

    private BarData generateBarData(List<TestCoverage> testCoverages) {
        int index = 0;
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (TestCoverage testCoverage : testCoverages) {
            float answersCorrect = Data.getInt(testCoverage.attendedCorrectQCount);
            float answersRemaining = Data.getInt(testCoverage.questionCount) - Data.getInt(testCoverage.attendedQCount);
            float answersWrong = Data.getInt(testCoverage.attendedQCount) - Data.getInt(testCoverage.attendedCorrectQCount);

            mChapterLevels.add(testCoverage.level);

            BarEntry barEntry = new BarEntry(new float[]{answersCorrect, answersRemaining, answersWrong}, index++);
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(new int[]{getResources().getColor(R.color.green), getResources().getColor(R.color.red), getResources().getColor(R.color.skyblue)});
        barDataSet.setDrawValues(false);
        barDataSet.setBarSpacePercent(20f);

        BarData barData = new BarData(mChapterLevels);
        barData.addDataSet(barDataSet);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return barData;
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

    private CharSequence getNote(int levelCrossed, String questionsAttempted, String questionCorrect) {
        double accuracyPercentage = Double.valueOf(questionCorrect) / Double.valueOf(questionsAttempted) * 100;
        String noteValue = getActivity().getResources().getString(R.string.value_test_note, levelCrossed+"", ((int)accuracyPercentage)+"", questionCorrect, questionsAttempted);
        return TextUtils.concat(Data.getBoldString(getActivity().getResources().getString(R.string.label_note)), noteValue);
    }

    private void setupTest() {
        mExtras.putStringArrayList(TestChapterSetupFragment.EXTRAS_CHAPTER_LEVELS, mChapterLevels);
        mExtras.putInt(Constants.LEVEL_CROSSED, levelCrossed);
        if(testCoverages != null) {
            mExtras.putString(Constants.TEST_COVERAGE_LIST_GSON, Gson.get().toJson(testCoverages));
        }
        TestChapterSetupFragment fragment = TestChapterSetupFragment.newInstance(mExtras);
        fragment.show(getFragmentManager(), TestChapterSetupFragment.getMyTag());
    }
}
