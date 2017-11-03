package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.fragments.AnalyticsTitleFragment;
import com.education.corsalite.fragments.RecommendedTabFragment;
import com.education.corsalite.fragments.TestCoverageTabFragment;

/**
 * Created by Aastha on 27/09/15.
 */
public class AnalyticsActivity extends AbstractBaseActivity implements AnalyticsTitleFragment.IAnalyticsTitleFragmentListener {

    public static final String K_TITLE_ACCURACY = "accuracy";
    public static final String K_TITLE_RECOMMENDED_READING = "recommendedReading";
    public static final String K_TITLE_TEST_COVERAGE = "testCoverage";
    public static final String K_TITLE_TIME_MANAGEMENT = "timeManagement";
    public static final String K_TITLE_PROGRESS_REPORT = "progressReport";
    public static final String K_TITLE_USAGE_ANALYSIS = "usageAnalysis";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_analytics, null);
        frameLayout.addView(myView);

        setToolbarForAnalytics();
        setUpTitleLayout();
    }

    private void setUpTitleLayout() {
        AnalyticsTitleFragment details = new AnalyticsTitleFragment();
        getFragmentManager().beginTransaction().replace(R.id.fl_analytics_title, details).commit();
    }

    @Override
    public void onAnalyticsTitleSelected(String title) {
        switch (title) {
            case K_TITLE_PROGRESS_REPORT:
//                ProgressReportTabFragment reportTabFragment = new ProgressReportTabFragment();
//                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,reportTabFragment).commit();
                break;
            case K_TITLE_RECOMMENDED_READING:
                RecommendedTabFragment recommendedTabFragment = new RecommendedTabFragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail, recommendedTabFragment).commit();
                break;
            case K_TITLE_TEST_COVERAGE:
                TestCoverageTabFragment testCoverageTabFragment = new TestCoverageTabFragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail, testCoverageTabFragment).commit();
                break;
            case K_TITLE_TIME_MANAGEMENT:
//                TimeManagementTabFragment timeManagementTabFragment = new TimeManagementTabFragment();
//                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,timeManagementTabFragment).commit();
                break;
            case K_TITLE_USAGE_ANALYSIS:
//                UsageAnalysisFragment usageAnalysisFragment = new UsageAnalysisFragment();
//                getSupportFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,usageAnalysisFragment).commit();
                break;
            case K_TITLE_ACCURACY:
            default:
//                AccuracySpeedTabFragment accuracySpeedTabFragment1 = new AccuracySpeedTabFragment();
//                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,accuracySpeedTabFragment1).commit();
                break;

        }
    }

}
