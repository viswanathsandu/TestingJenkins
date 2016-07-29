package com.education.corsalite.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ContentReadingActivity;
import com.education.corsalite.activities.TestInstructionsActivity;
import com.education.corsalite.adapters.CurriculumAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.enums.CurriculumEntityType;
import com.education.corsalite.enums.CurriculumTabType;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.CurriculumEntity;
import com.education.corsalite.models.responsemodels.CurriculumResponseModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by sridharnalam on 1/8/16.
 */
public class CurriculumFragment extends BaseFragment implements CurriculumAdapter.OnItemClickListener {
    private static final String tabTypeExta = "TAB_TYPE";

    @Bind(R.id.rcv_curriculum) RecyclerView mRecyclerView;
    @Bind(R.id.empty_layout) View emptyLayout;
    @Bind(R.id.progress_layout) View progress;

    private LinearLayoutManager mLayoutManager;
    private CurriculumAdapter mCurriculumAdapter;
    private CurriculumTabType selectedTab = null;

    public static CurriculumFragment newInstance(CurriculumTabType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(tabTypeExta, type);
        CurriculumFragment fragment = new CurriculumFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_curiculum, container, false);
        ButterKnife.bind(this, view);
        selectedTab = (CurriculumTabType) getArguments().get(tabTypeExta);
        setUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!AbstractBaseActivity.getSelectedCourseId().isEmpty()) {
            refreshData();
        }
    }

    private void setUI() {
        mLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mCurriculumAdapter = new CurriculumAdapter(getActivity(), selectedTab, this);
        mRecyclerView.setAdapter(mCurriculumAdapter);
    }

    public void refreshData() {
        if(selectedTab != null) {
            loadCurriculumData();
        }
    }

    ApiCallback<CurriculumResponseModel> curriculumResponseModelApiCallback = new ApiCallback<CurriculumResponseModel>(getActivity()) {
        @Override
        public void success(CurriculumResponseModel curriculumResponseModel, Response response) {
            super.success(curriculumResponseModel, response);
            closeProgress();
            if (curriculumResponseModel!= null && curriculumResponseModel.curriculumEntities != null && !curriculumResponseModel.curriculumEntities.isEmpty()) {
                setCurriculumData(curriculumResponseModel.curriculumEntities);
                emptyLayout.setVisibility(View.GONE);
            } else {
                emptyLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void failure(CorsaliteError error) {
            super.failure(error);
            closeProgress();
            emptyLayout.setVisibility(View.VISIBLE);
        }
    };

    private void loadCurriculumData() {
        showProgress();
        ApiManager.getInstance(getActivity()).getCurriculumData(LoginUserCache.getInstance().getStudentId(),
                AbstractBaseActivity.getSelectedCourseId(),
                LoginUserCache.getInstance().getEntityId(),
                selectedTab.toString(),
                "T",
                curriculumResponseModelApiCallback);
    }

    private void setCurriculumData(List<CurriculumEntity> entities) {
        mCurriculumAdapter.setCurrilumList(entities);
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(CurriculumEntity entity) {
        CurriculumEntityType type = CurriculumEntityType.getCurriculumEntityType(entity.recType);
        if (type != null) {
            switch (type) {
                case READING:
                    Intent intent = new Intent(getActivity(), ContentReadingActivity.class);
                    intent.putExtra("courseId", entity.idCourse);
                    intent.putExtra("subjectId", entity.idCourseSubject);
                    intent.putExtra("chapterId", entity.idCourseSubjectChapter);
                    startActivity(intent);
                    break;
                case CUSTOM_EXERCISE:
                    break;
                case PRACTIVE_TEST:
                    break;
                case SCHEDULED_EXAM:
                case REVISION_TEST:
                case SCHEDULED_TEST:
                    intent = new Intent(getActivity(), TestInstructionsActivity.class);
                    intent.putExtra("test_question_paper_id", entity.idTestQuestionPaper);
                    startActivity(intent);
                    break;
            }
        }
    }
}