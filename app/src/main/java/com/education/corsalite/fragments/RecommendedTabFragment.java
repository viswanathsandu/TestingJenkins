package com.education.corsalite.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ContentReadingActivity;
import com.education.corsalite.adapters.RecommendationsAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.client.Response;

/**
 * Created by Aastha on 27/09/15.
 */
public class RecommendedTabFragment extends Fragment implements RecommendationsAdapter.SetOnRecommendationClickListener {

    @Bind(R.id.rv_analytics_recommended)
    RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar_tab)
    ProgressBar mProgressBar;
    @Bind(R.id.headerLayout)
    LinearLayout linearLayout;
    @Bind(R.id.tv_failure_text)
    TextView mFailureText;
    @Bind(R.id.color_bar)
    public LinearLayout colorBar;
    @Bind(R.id.subjects_name_id)
    LinearLayout topbarLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecommendationsAdapter mAdapter;
    private LayoutInflater mInflater;
    private HashSet<String> subjects;
    private TextView selectedSubjectTxt;
    private List<CourseAnalysis> completeCourses;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommended_tab, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mRecyclerView.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        this.mInflater = inflater;
        colorBar.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AbstractBaseActivity.selectedCourse != null) {
            onEvent(AbstractBaseActivity.selectedCourse);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(Course course) {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mFailureText.setVisibility(View.GONE);
        getCourseData(course.courseId.toString());
    }

    private void getCourseData(String courseId) {
        ApiManager.getInstance(getActivity()).getCourseAnalysisData(LoginUserCache.getInstance().loginResponse.studentId,
                courseId, null, "Topic", "Month", "365", "false",
                new ApiCallback<List<CourseAnalysis>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if (getActivity() == null) {
                            return;
                        }
                        L.error(error.message);
                        mProgressBar.setVisibility(View.GONE);
                        mFailureText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void success(List<CourseAnalysis> courseAnalysisList, Response response) {
                        super.success(courseAnalysisList, response);
                        if (getActivity() == null) {
                            return;
                        }
                        if (courseAnalysisList != null) {
                            completeCourses = courseAnalysisList;
                            setupSubjects(courseAnalysisList);
                        }

                    }
                });
    }

    private void setupSubjects(List<CourseAnalysis> courseAnalysis) {
        topbarLayout.removeAllViews();
        subjects = new HashSet<String>();
        for (CourseAnalysis course : courseAnalysis) {
            addSubjectsAndCreateViews(course);
        }
    }

    private void addSubjectsAndCreateViews(CourseAnalysis course) {
        String subject = course.subjectName;
        if (!subjects.contains(subject)) {
            subjects.add(subject);
            topbarLayout.addView(getSubjectView(subject, course.idCourseSubject + "", subjects.size() == 1));
        }
    }

    private View getTextView() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.study_center_text_view, null);
    }

    private View getSubjectView(final String subjectName, String subjectId, boolean isSelected) {
        View v = getTextView();
        final TextView tv = (TextView) v.findViewById(R.id.subject);
        tv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.roboto_regular)));
        tv.setText(subjectName);
        tv.setTag(subjectId);
        tv.setPadding(8, 0, 8, 0);

        ImageView iv = (ImageView) v.findViewById(R.id.arrow_img);
        iv.setVisibility(View.GONE);

        if (isSelected) {
            tv.setSelected(true);
            selectedSubjectTxt = tv;
            loadCoursesAnalytics(subjectName);
        }

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSubjectTxt != null) {
                    selectedSubjectTxt.setSelected(false);
                }
                selectedSubjectTxt = tv;
                selectedSubjectTxt.setSelected(true);
                loadCoursesAnalytics(subjectName);
            }
        });
        return v;
    }

    public void loadCoursesAnalytics(String subjectName) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        List<CourseAnalysis> selectedSubjectCourses = new ArrayList<>();

        for (int i = 0; i < completeCourses.size(); i++) {
            if (completeCourses.get(i).subjectName.equalsIgnoreCase(subjectName)) {
                selectedSubjectCourses.add(completeCourses.get(i));
            }
        }
        Collections.sort(selectedSubjectCourses);
        if (selectedSubjectCourses.size() > 0) {
            mAdapter = new RecommendationsAdapter(selectedSubjectCourses, mInflater, RecommendedTabFragment.this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mFailureText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mFailureText.setVisibility(View.GONE);
            mFailureText.setText("No Analytics available for " + subjectName + " Recommended Reading");
        }
    }
    @Override
    public void onItemClick(int position) {
        CourseAnalysis courseAnalysis = (CourseAnalysis) mAdapter.getItem(position);
        if (SystemUtils.isNetworkConnected(getActivity())) {
            Intent intent = new Intent(getActivity(), ContentReadingActivity.class);
            intent.putExtra("subjectId", "" + courseAnalysis.idCourseSubject);
            intent.putExtra("chapterId", "" + courseAnalysis.idCourseSubjectChapter);
            intent.putExtra("topicId", "" + courseAnalysis.idTopic);
            startActivity(intent);
        }
    }
}
