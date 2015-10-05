package com.education.corsalite.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.education.corsalite.R;
import com.education.corsalite.adapters.RecommendationsAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.utils.L;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Aastha on 27/09/15.
 */
public class RecommendedTabFragment extends Fragment {

    @Bind(R.id.rv_analytics_recommended)
    RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar_tab)ProgressBar mProgressBar;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommended_tab, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getCourseData(inflater);
        return view;

    }

    private void getCourseData(final LayoutInflater inflater) {
        //passing static data TODO
        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId, "13", null, "Topic", "None", "180", "true",
                new ApiCallback<List<CourseAnalysis>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        if (courseAnalysisList != null) {
                            mProgressBar.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mAdapter = new RecommendationsAdapter(courseAnalysisList, inflater);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }
                });


    }
}
