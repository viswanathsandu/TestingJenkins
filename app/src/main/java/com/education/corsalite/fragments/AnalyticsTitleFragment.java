package com.education.corsalite.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;

/**
 * Created by Aastha on 28/09/15.
 */
public class AnalyticsTitleFragment extends Fragment{

    TextView accuracy;
    TextView progressReport;
    TextView testCoverage;
    TextView recommededReading;
    TextView timeManagement;
    IAnalyticsTitleFragmentListener titleSelectedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof IAnalyticsTitleFragmentListener)
            titleSelectedListener = (IAnalyticsTitleFragmentListener) activity;
        else{
            throw new RuntimeException("Activity must implement " + IAnalyticsTitleFragmentListener.class.getCanonicalName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_analytics_title,container,false);
        accuracy = (TextView)v.findViewById(R.id.tv_accuracy);
        progressReport = (TextView)v.findViewById(R.id.tv_progress_report);
        testCoverage = (TextView)v.findViewById(R.id.tv_test_coverage);
        recommededReading = (TextView)v.findViewById(R.id.tv_recommended_reading);
        timeManagement = (TextView)v.findViewById(R.id.tv_time_management);
        return v;
    }

    public interface IAnalyticsTitleFragmentListener{
        void onAnalyticsTitleSelected(String title);
    }
}
