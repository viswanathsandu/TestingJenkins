package com.corsalite.tabletapp.fragments;

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

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.activities.AbstractBaseActivity;
import com.corsalite.tabletapp.activities.ContentReadingActivity;
import com.corsalite.tabletapp.adapters.RecommendationsAdapter;
import com.corsalite.tabletapp.api.ApiCallback;
import com.corsalite.tabletapp.api.ApiManager;
import com.corsalite.tabletapp.cache.LoginUserCache;
import com.corsalite.tabletapp.models.responsemodels.CorsaliteError;
import com.corsalite.tabletapp.models.responsemodels.Course;
import com.corsalite.tabletapp.models.responsemodels.RecommendedModel;
import com.corsalite.tabletapp.utils.L;
import com.corsalite.tabletapp.utils.SystemUtils;

import java.util.ArrayList;
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

    @Bind(R.id.rv_analytics_recommended) RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar_tab) ProgressBar mProgressBar;
    @Bind(R.id.headerLayout) LinearLayout linearLayout;
    @Bind(R.id.tv_failure_text) TextView mFailureText;
    @Bind(R.id.failure_container) View failureContainer;
    @Bind(R.id.color_bar) public LinearLayout colorBar;
    @Bind(R.id.subjects_name_id) LinearLayout topbarLayout;
    private LinearLayoutManager mLayoutManager;
    private RecommendationsAdapter mAdapter;
    private LayoutInflater mInflater;
    private HashSet<String> subjects;
    private TextView selectedSubjectTxt;
    private List<RecommendedModel> completeRecommendedModels = new ArrayList<>();
    private static final int MAX_ROW_COUNT = 500;
    private boolean mLoading = true;
    private int currentPage = 0;
    private boolean isCompleted = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommended_tab, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mRecyclerView.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int totalItem = mLayoutManager.getItemCount();
                    int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                    if (!mLoading && lastVisibleItem == totalItem - 1) {
                        mLoading = true;
                        currentPage++;
                        // Scrolled to bottom. Do something here.
                        getRecommendedReading((currentPage * MAX_ROW_COUNT), MAX_ROW_COUNT);
                    }
                }
            }
        });
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
        failureContainer.setVisibility(View.GONE);

        completeRecommendedModels = new ArrayList<>();
        isCompleted = false;
        currentPage = 0;
        mLoading = true;
        getRecommendedReading(0, MAX_ROW_COUNT);
    }

    private void getRecommendedReading(int startIndex, int noOfRows) {
        if(isCompleted) {
            return;
        }
        if(startIndex == 0) {
            topbarLayout.removeAllViews();
        }
        ApiManager.getInstance(getActivity()).getRecommendedReading(LoginUserCache.getInstance().loginResponse.studentId,
                AbstractBaseActivity.selectedCourse.courseId+"", startIndex+"", noOfRows+"",
                new ApiCallback<List<RecommendedModel>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if (getActivity() == null) {
                            return;
                        }
                        mLoading = false;
                        L.error(error.message);
                        mProgressBar.setVisibility(View.GONE);
                        failureContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void success(List<RecommendedModel> recommendedModels, Response response) {
                        super.success(recommendedModels, response);
                        if (getActivity() == null) {
                            return;
                        }
                        mLoading = false;
                        if (recommendedModels != null && !recommendedModels.isEmpty()) {
                            completeRecommendedModels.addAll(recommendedModels);
                            setupSubjects(completeRecommendedModels);
                        } else {
                            isCompleted = true;
                            if(completeRecommendedModels.isEmpty()) {
                                mProgressBar.setVisibility(View.GONE);
                                failureContainer.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    private void setupSubjects(List<RecommendedModel> recommendedModels) {
        subjects = new HashSet<String>();
        for (RecommendedModel model : recommendedModels) {
            addSubjectsAndCreateViews(model);
        }
    }

    private void addSubjectsAndCreateViews(RecommendedModel model) {
        String subject = model.subjectName;
        if (!subjects.contains(subject)) {
            subjects.add(subject);
            if(topbarLayout.findViewWithTag(model.subjectId+"") == null) {
                topbarLayout.addView(getSubjectView(subject, model.subjectId + "", subjects.size() == 1));
            } else {
                loadRecommendedReading(selectedSubjectTxt.getText().toString());
            }
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
            loadRecommendedReading(subjectName);
        }

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSubjectTxt != null) {
                    selectedSubjectTxt.setSelected(false);
                }
                selectedSubjectTxt = tv;
                selectedSubjectTxt.setSelected(true);
                loadRecommendedReading(subjectName);
            }
        });
        return v;
    }

    public void loadRecommendedReading(String subjectName) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        List<RecommendedModel> selectedSubjectRecommendedModels = new ArrayList<>();

        for (int i = 0; i < completeRecommendedModels.size(); i++) {
            if (completeRecommendedModels.get(i).subjectName.equalsIgnoreCase(subjectName)) {
                selectedSubjectRecommendedModels.add(completeRecommendedModels.get(i));
            }
        }
        if (selectedSubjectRecommendedModels.size() > 0) {
            mAdapter = new RecommendationsAdapter(selectedSubjectRecommendedModels, mInflater, RecommendedTabFragment.this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            failureContainer.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            failureContainer.setVisibility(View.GONE);
            mFailureText.setText("No Data available for " + subjectName);
        }
    }
    @Override
    public void onItemClick(int position) {
        RecommendedModel model = (RecommendedModel) mAdapter.getItem(position);
        if (SystemUtils.isNetworkConnected(getActivity())) {
            Intent intent = new Intent(getActivity(), ContentReadingActivity.class);
            intent.putExtra("subjectId", "" + model.subjectId);
            intent.putExtra("chapterId", "" + model.chapterId);
            intent.putExtra("topicId", "" + model.topicId);
            startActivity(intent);
        }
    }
}
