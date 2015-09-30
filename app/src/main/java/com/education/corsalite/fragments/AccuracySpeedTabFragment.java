package com.education.corsalite.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.CourseAnalysisResponse;
import com.education.corsalite.utils.L;
import com.github.mikephil.charting.charts.ScatterChart;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 27/09/15.
 */
public class AccuracySpeedTabFragment extends Fragment   {

    @Bind(R.id.sch_accuray_chapter)ScatterChart accuracyChapterChart;
    @Bind(R.id.sch_accuray_date)ScatterChart accuracyDateChart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accuracy_tab, container, false);
        ButterKnife.bind(this, view);

        initializeGraph();
        //TODO: Build from api response later
        ApiManager.getInstance(getActivity()).getCourseAnalysisData("1154", "13", "51", "Chapter", "Month", "365", "true",
                new ApiCallback<CourseAnalysisResponse>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(CourseAnalysisResponse courseAnalysis, Response response) {
                        buildChapterGraphData();
                    }
                });

        ApiManager.getInstance(getActivity()).getCourseAnalysisData("1154", "13", "51", "Dates", "Month", "365", "true",
                new ApiCallback<CourseAnalysisResponse>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(CourseAnalysisResponse courseAnalysis, Response response) {
                        buildDateGraphData();
                    }
                });

        return view;
    }

    private void initializeGraph(){




    }

    private void buildChapterGraphData(){

    }

    private void buildDateGraphData(){

    }
}
