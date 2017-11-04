package com.education.corsalite.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AnalyticsActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 28/09/15.
 */
public class AnalyticsTitleFragment extends Fragment {

    @Bind(R.id.tv_accuracy)
    TextView accuracy;
    @Bind(R.id.tv_progress_report)
    TextView progressReport;
    @Bind(R.id.tv_test_coverage)
    TextView testCoverage;
    @Bind(R.id.tv_recommended_reading)
    TextView recommededReading;
    @Bind(R.id.tv_time_management)
    TextView timeManagement;
    @Bind(R.id.tv_usage_analysis)
    TextView usageAnalysis;
    IAnalyticsTitleFragmentListener titleSelectedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IAnalyticsTitleFragmentListener) {
            titleSelectedListener = (IAnalyticsTitleFragmentListener) activity;
        } else {
            throw new RuntimeException("Activity must implement " + IAnalyticsTitleFragmentListener.class.getCanonicalName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_analytics_title, container, false);
        ButterKnife.bind(this, v);

        accuracy.setOnClickListener(mTitleSelectedListener);
        progressReport.setOnClickListener(mTitleSelectedListener);
        testCoverage.setOnClickListener(mTitleSelectedListener);
        recommededReading.setOnClickListener(mTitleSelectedListener);
        timeManagement.setOnClickListener(mTitleSelectedListener);
        usageAnalysis.setOnClickListener(mTitleSelectedListener);

        //Default accuracy tab should be selected
        accuracy.setSelected(true);
        mTitleSelectedListener.onClick(accuracy);
        return v;
    }


    View.OnClickListener mTitleSelectedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_progress_report:
                    progressReport.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_report_selected, 0, 0);
                    timeManagement.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_time_unselected, 0, 0);
                    testCoverage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_test_unselected, 0, 0);
                    recommededReading.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_recommend_unselected, 0, 0);
                    accuracy.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_accuracy_unselected, 0, 0);
                    usageAnalysis.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_sidemenu_groups, 0, 0);
                    progressReport.setBackground(getResources().getDrawable(R.drawable.background_rounded_corner_left_bottom));
                    progressReport.setTextColor(getResources().getColor(R.color.green));
                    timeManagement.setBackgroundColor(getResources().getColor(R.color.green));
                    timeManagement.setTextColor(Color.WHITE);
                    recommededReading.setBackgroundColor(getResources().getColor(R.color.green));
                    recommededReading.setTextColor(Color.WHITE);
                    accuracy.setBackgroundColor(getResources().getColor(R.color.green));
                    accuracy.setTextColor(Color.WHITE);
                    testCoverage.setBackgroundColor(getResources().getColor(R.color.green));
                    testCoverage.setTextColor(Color.WHITE);
                    usageAnalysis.setBackgroundColor(getResources().getColor(R.color.green));
                    usageAnalysis.setTextColor(Color.WHITE);
                    titleSelectedListener.onAnalyticsTitleSelected(AnalyticsActivity.K_TITLE_PROGRESS_REPORT);
                    break;
                case R.id.tv_time_management:
                    timeManagement.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_time_selected, 0, 0);
                    progressReport.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_report_unselected, 0, 0);
                    testCoverage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_test_unselected, 0, 0);
                    recommededReading.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_recommend_unselected, 0, 0);
                    accuracy.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_accuracy_unselected, 0, 0);
                    usageAnalysis.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_sidemenu_groups, 0, 0);
                    titleSelectedListener.onAnalyticsTitleSelected(AnalyticsActivity.K_TITLE_TIME_MANAGEMENT);
                    timeManagement.setBackground(getResources().getDrawable(R.drawable.background_rounded_corner_left_bottom));
                    timeManagement.setTextColor(getResources().getColor(R.color.green));
                    progressReport.setBackgroundColor(getResources().getColor(R.color.green));
                    progressReport.setTextColor(Color.WHITE);
                    recommededReading.setBackgroundColor(getResources().getColor(R.color.green));
                    recommededReading.setTextColor(Color.WHITE);
                    accuracy.setBackgroundColor(getResources().getColor(R.color.green));
                    accuracy.setTextColor(Color.WHITE);
                    testCoverage.setBackgroundColor(getResources().getColor(R.color.green));
                    testCoverage.setTextColor(Color.WHITE);
                    usageAnalysis.setBackgroundColor(getResources().getColor(R.color.green));
                    usageAnalysis.setTextColor(Color.WHITE);
                    break;
                case R.id.tv_test_coverage:
                    testCoverage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_test_selected, 0, 0);
                    progressReport.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_report_unselected, 0, 0);
                    timeManagement.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_time_unselected, 0, 0);
                    recommededReading.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_recommend_unselected, 0, 0);
                    accuracy.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_accuracy_unselected, 0, 0);
                    usageAnalysis.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_sidemenu_groups, 0, 0);
                    titleSelectedListener.onAnalyticsTitleSelected(AnalyticsActivity.K_TITLE_TEST_COVERAGE);
                    testCoverage.setBackground(getResources().getDrawable(R.drawable.background_rounded_corner_left_bottom));
                    testCoverage.setTextColor(getResources().getColor(R.color.green));
                    timeManagement.setBackgroundColor(getResources().getColor(R.color.green));
                    timeManagement.setTextColor(Color.WHITE);
                    progressReport.setBackgroundColor(getResources().getColor(R.color.green));
                    progressReport.setTextColor(Color.WHITE);
                    recommededReading.setBackgroundColor(getResources().getColor(R.color.green));
                    recommededReading.setTextColor(Color.WHITE);
                    accuracy.setBackgroundColor(getResources().getColor(R.color.green));
                    accuracy.setTextColor(Color.WHITE);
                    usageAnalysis.setBackgroundColor(getResources().getColor(R.color.green));
                    usageAnalysis.setTextColor(Color.WHITE);
                    break;
                case R.id.tv_accuracy:
                    accuracy.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_accuracy_selected, 0, 0);
                    progressReport.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_report_unselected, 0, 0);
                    timeManagement.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_time_unselected, 0, 0);
                    testCoverage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_test_unselected, 0, 0);
                    recommededReading.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_recommend_unselected, 0, 0);
                    usageAnalysis.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_sidemenu_groups, 0, 0);
                    titleSelectedListener.onAnalyticsTitleSelected(AnalyticsActivity.K_TITLE_ACCURACY);
                    accuracy.setBackground(getResources().getDrawable(R.drawable.background_rounded_corner_left_bottom));
                    accuracy.setTextColor(getResources().getColor(R.color.green));
                    timeManagement.setBackgroundColor(getResources().getColor(R.color.green));
                    timeManagement.setTextColor(Color.WHITE);
                    progressReport.setBackgroundColor(getResources().getColor(R.color.green));
                    progressReport.setTextColor(Color.WHITE);
                    recommededReading.setBackgroundColor(getResources().getColor(R.color.green));
                    recommededReading.setTextColor(Color.WHITE);
                    testCoverage.setBackgroundColor(getResources().getColor(R.color.green));
                    testCoverage.setTextColor(Color.WHITE);
                    usageAnalysis.setBackgroundColor(getResources().getColor(R.color.green));
                    usageAnalysis.setTextColor(Color.WHITE);
                    break;
                case R.id.tv_recommended_reading:
                    recommededReading.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_recommend_selected, 0, 0);
                    progressReport.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_report_unselected, 0, 0);
                    timeManagement.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_time_unselected, 0, 0);
                    testCoverage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_test_unselected, 0, 0);
                    accuracy.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_accuracy_unselected, 0, 0);
                    usageAnalysis.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_sidemenu_groups, 0, 0);
                    titleSelectedListener.onAnalyticsTitleSelected(AnalyticsActivity.K_TITLE_RECOMMENDED_READING);
                    recommededReading.setBackground(getResources().getDrawable(R.drawable.background_rounded_corner_left_bottom));
                    recommededReading.setTextColor(getResources().getColor(R.color.green));
                    timeManagement.setBackgroundColor(getResources().getColor(R.color.green));
                    timeManagement.setTextColor(Color.WHITE);
                    progressReport.setBackgroundColor(getResources().getColor(R.color.green));
                    progressReport.setTextColor(Color.WHITE);
                    accuracy.setBackgroundColor(getResources().getColor(R.color.green));
                    accuracy.setTextColor(Color.WHITE);
                    testCoverage.setBackgroundColor(getResources().getColor(R.color.green));
                    testCoverage.setTextColor(Color.WHITE);
                    usageAnalysis.setBackgroundColor(getResources().getColor(R.color.green));
                    usageAnalysis.setTextColor(Color.WHITE);
                    break;
                case R.id.tv_usage_analysis:
                    recommededReading.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_recommend_unselected, 0, 0);
                    progressReport.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_report_unselected, 0, 0);
                    timeManagement.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_time_unselected, 0, 0);
                    testCoverage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_test_unselected, 0, 0);
                    accuracy.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_accuracy_unselected, 0, 0);
                    usageAnalysis.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_sidemenu_groups, 0, 0);
                    titleSelectedListener.onAnalyticsTitleSelected(AnalyticsActivity.K_TITLE_USAGE_ANALYSIS);
                    usageAnalysis.setBackground(getResources().getDrawable(R.drawable.background_rounded_corner_left_bottom));
                    usageAnalysis.setTextColor(getResources().getColor(R.color.green));
                    timeManagement.setBackgroundColor(getResources().getColor(R.color.green));
                    timeManagement.setTextColor(Color.WHITE);
                    progressReport.setBackgroundColor(getResources().getColor(R.color.green));
                    progressReport.setTextColor(Color.WHITE);
                    accuracy.setBackgroundColor(getResources().getColor(R.color.green));
                    accuracy.setTextColor(Color.WHITE);
                    recommededReading.setBackgroundColor(getResources().getColor(R.color.green));
                    recommededReading.setTextColor(Color.WHITE);
                    testCoverage.setBackgroundColor(getResources().getColor(R.color.green));
                    testCoverage.setTextColor(Color.WHITE);
                    break;
            }
        }
    };

    public interface IAnalyticsTitleFragmentListener {
        void onAnalyticsTitleSelected(String title);
    }
}
