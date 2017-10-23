package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.education.corsalite.BuildConfig;
import com.education.corsalite.R;
import com.education.corsalite.fragments.AnalyticsTitleFragment;
import com.education.corsalite.fragments.DetailsWebviewFragment;
import com.education.corsalite.fragments.RecommendedTabFragment;

/**
 * Created by madhuri on 4/23/16.
 */
public class NewAnalyticsActivity extends AbstractBaseActivity implements AnalyticsTitleFragment.IAnalyticsTitleFragmentListener {

    public static final String K_TITLE_ACCURACY = "accuracy";
    public static final String K_TITLE_RECOMMENDED_READING = "recommendedReading";
    public static final String K_TITLE_TEST_COVERAGE = "testCoverage";
    public static final String K_TITLE_TIME_MANAGEMENT = "timeManagement";
    public static final String K_TITLE_PROGRESS_REPORT= "progressReport";
    public static final String K_TITLE_USAGE_ANALYSIS= "usageAnalysis";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_analytics, null);
        frameLayout.addView(myView);

        setToolbarForAnalytics();
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
                DetailsWebviewFragment accuracyVsSpeedTabFragment = new DetailsWebviewFragment();
                Bundle accuracyVsSpeed = new Bundle();
                accuracyVsSpeed.putString("URL_Pattern", BuildConfig.BASE_URL+"dashboard/courseAnalysis/%s/avgvsspeed?Header=0&Footer=0");
                accuracyVsSpeedTabFragment.setArguments(accuracyVsSpeed);
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,accuracyVsSpeedTabFragment).commit();
                break;
            case K_TITLE_PROGRESS_REPORT:
                DetailsWebviewFragment reportTabFragment = new DetailsWebviewFragment();
                Bundle reportbundle = new Bundle();
                reportbundle.putString("URL_Pattern", BuildConfig.BASE_URL+"dashboard/courseAnalysis/%s/progressreport?Header=0&Footer=0");
                reportTabFragment.setArguments(reportbundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,reportTabFragment).commit();
                break;
            case K_TITLE_RECOMMENDED_READING:
                RecommendedTabFragment recommendedTabFragment = new RecommendedTabFragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,recommendedTabFragment).commit();
                break;
            case K_TITLE_TEST_COVERAGE:
                DetailsWebviewFragment testCoverageTabFragment = new DetailsWebviewFragment();
                Bundle testBundle = new Bundle();
                testBundle.putString("URL_Pattern", BuildConfig.BASE_URL+"dashboard/courseAnalysis/%s/testcoverage?Header=0&Footer=0");
                testCoverageTabFragment.setArguments(testBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,testCoverageTabFragment).commit();
                break;
            case K_TITLE_TIME_MANAGEMENT:
                DetailsWebviewFragment timeManagementTabFragment = new DetailsWebviewFragment();
                Bundle timeBundle = new Bundle();
                timeBundle.putString("URL_Pattern", BuildConfig.BASE_URL+"dashboard/courseAnalysis/%s/timemanagementbysubject?Header=0&Footer=0");
                timeManagementTabFragment.setArguments(timeBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,timeManagementTabFragment).commit();
                break;
            case K_TITLE_USAGE_ANALYSIS:
//                UsageAnalysisFragment usageAnalysisFragment = new UsageAnalysisFragment();
//                getSupportFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,usageAnalysisFragment).commit();
                break;
            default:
//                AccuracySpeedTabFragment accuracySpeedTabFragment1 = new AccuracySpeedTabFragment();
//                getFragmentManager().beginTransaction().replace(R.id.fl_analytics_detail,accuracySpeedTabFragment1).commit();
                break;

        }
    }
}
