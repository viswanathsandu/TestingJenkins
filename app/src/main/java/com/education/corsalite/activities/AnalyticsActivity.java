package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.fragments.AccuracySpeedTabFragment;
import com.education.corsalite.fragments.AnalyticsTitleFragment;
import com.education.corsalite.fragments.ProgressReportTabFragment;
import com.education.corsalite.fragments.RecommendedTabFragment;
import com.education.corsalite.fragments.TestCoverageTabFragment;
import com.education.corsalite.fragments.TimeManagementTabFragment;

import butterknife.ButterKnife;

/**
 * Created by Aastha on 27/09/15.
 */
public class AnalyticsActivity extends AbstractBaseActivity implements AnalyticsTitleFragment.IAnalyticsTitleFragmentListener {

    final String K_TITLE_ACCURACY = "accuracy";
    final String K_TITLE_RECOMMENDED_READING = "recommendedReading";
    final String K_TITLE_TEST_COVERAGE = "testCoverage";
    final String K_TITLE_TIME_MANAGEMENT = "timeManagement";
    final String K_TITLE_PROGRESS_REPORT= "progressReport";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_analytics, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        setUpTitleLayout();
    }
    private void setUpTitleLayout(){
        AnalyticsTitleFragment details = new AnalyticsTitleFragment();
        getFragmentManager().beginTransaction().replace(R.id.fl_analytics_title, details).commit();
    }

    @Override
    public void onAnalyticsTitleSelected(String title) {
        switch (title){
            case K_TITLE_ACCURACY:
                AccuracySpeedTabFragment accuracySpeedTabFragment = new AccuracySpeedTabFragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,accuracySpeedTabFragment).commit();
                break;
            case K_TITLE_PROGRESS_REPORT:
                ProgressReportTabFragment reportTabFragment = new ProgressReportTabFragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,reportTabFragment).commit();
                break;
            case K_TITLE_RECOMMENDED_READING:
                RecommendedTabFragment recommendedTabFragment = new RecommendedTabFragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,recommendedTabFragment).commit();
                break;
            case K_TITLE_TEST_COVERAGE:
                TestCoverageTabFragment testCoverageTabFragment = new TestCoverageTabFragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,testCoverageTabFragment).commit();
                break;
            case K_TITLE_TIME_MANAGEMENT:
                TimeManagementTabFragment timeManagementTabFragment = new TimeManagementTabFragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,timeManagementTabFragment).commit();
                break;
            default:
                AccuracySpeedTabFragment accuracySpeedTabFragment1 = new AccuracySpeedTabFragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,accuracySpeedTabFragment1).commit();
                break;

        }
    }

    public void onClickTitle(final View view){
        String title = null;
        switch (view.getId()){
            case R.id.tv_accuracy:
                title= K_TITLE_ACCURACY;
                break;
            case R.id.tv_recommended_reading:
                title = K_TITLE_RECOMMENDED_READING;
                break;
            case R.id.tv_test_coverage:
                title = K_TITLE_TEST_COVERAGE;
                break;
            case R.id.tv_time_management:
                title = K_TITLE_TIME_MANAGEMENT;
                break;
            case R.id.tv_progress_report:
                title = K_TITLE_PROGRESS_REPORT;
                break;
        }
        onAnalyticsTitleSelected(title);
    }
}
