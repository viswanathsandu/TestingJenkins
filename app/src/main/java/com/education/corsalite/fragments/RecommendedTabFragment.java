package com.education.corsalite.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.adapters.RecommendationsAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.utils.L;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.client.Response;

/**
 * Created by Aastha on 27/09/15.
 */
public class RecommendedTabFragment extends Fragment {

    @Bind(R.id.rv_analytics_recommended)
    RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar_tab)ProgressBar mProgressBar;
    @Bind(R.id.headerLayout)LinearLayout linearLayout;
    @Bind(R.id.tv_failure_text)TextView mFailureText;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private LayoutInflater mInflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommended_tab, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mRecyclerView.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        this.mInflater = inflater;;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(AbstractBaseActivity.selectedCourse != null) {
            onEvent(AbstractBaseActivity.selectedCourse);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(Course course) {
        getCourseData(course.courseId.toString());
    }

    private void getCourseData(String courseId) {
        //passing static data TODO
        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, null, "Topic", "None", "180", "true",
                new ApiCallback<List<CourseAnalysis>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        L.error(error.message);
                        mProgressBar.setVisibility(View.GONE);
                        mFailureText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        super.success(courseAnalysisList, response);
                        if(getActivity() == null) {
                            return;
                        }
                        if (courseAnalysisList != null) {
                            mProgressBar.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.VISIBLE);
                            mAdapter = new RecommendationsAdapter(courseAnalysisList, mInflater);
                            mRecyclerView.setAdapter(mAdapter);
                        }

                    }
                });


    }
}
