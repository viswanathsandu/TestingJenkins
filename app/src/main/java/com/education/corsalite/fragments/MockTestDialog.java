package com.education.corsalite.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.TestInstructionsActivity;
import com.education.corsalite.adapters.MockTestsListAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.db.MockTest;
import com.education.corsalite.models.requestmodels.PostQuestionPaperRequest;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.PostQuestionPaper;
import com.education.corsalite.services.TestDownloadService;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Viswanath on 17/10/15.
 */
public class MockTestDialog extends DialogFragment implements MockTestsListAdapter.IMockTestSelectedListener {

    @Bind(R.id.tv_title)TextView tvTitle;
    @Bind(R.id.mocktests_recyclerview)RecyclerView rvMockTestList;
    private Dialog dialog;
    private List<MockTest> mMockTestList;
    private String testQuestionPaperId;
    private MockTest selectedMockTest;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mocktest_list, container, false);
        ButterKnife.bind(this, v);
        loadMockTests();
        return v;
    }

    private void showMockTests() {
        if (getActivity() != null) {
            final LinearLayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rvMockTestList.setLayoutManager(layoutManager);
            MockTestsListAdapter mockTestsListAdapter = new MockTestsListAdapter(mMockTestList, getActivity().getLayoutInflater());
            mockTestsListAdapter.setMockTestSelectedListener(this);
            rvMockTestList.setAdapter(mockTestsListAdapter);
        }
    }

    @Override
    public void onMockTestSelected(int position) {
        MockTest test = mMockTestList.get(position);
        postQuestionPaper(LoginUserCache.getInstance().getEntityId(),
                test.examTemplateId, LoginUserCache.getInstance().getStudentId(), test.subjectId, false);
    }

    @Override
    public void onMockTestDownload(int position) {
        selectedMockTest = mMockTestList.get(position);
        postQuestionPaper(LoginUserCache.getInstance().getEntityId(),
                selectedMockTest.examTemplateId, LoginUserCache.getInstance().getStudentId(), selectedMockTest.subjectId, true);
    }

    private void loadMockTests() {
        String mockTestsGson = null;
        if(getArguments() != null) {
             mockTestsGson = getArguments().getString("mock_tests", null);
        }
        if(!TextUtils.isEmpty(mockTestsGson)) {
            try {
                Type listType = new TypeToken<List<MockTest>>() {
                }.getType();
                mMockTestList = Gson.get().fromJson(mockTestsGson, listType);
                showMockTests();
            } catch (Exception e) {
                if (getActivity() != null) {
                    ((AbstractBaseActivity) getActivity()).showToast("No mock tests available");
                    dismiss();
                }
            }
            return;
        }

        ApiManager.getInstance(getActivity()).getMockTests(
                AbstractBaseActivity.getSelectedCourseId(),
                LoginUserCache.getInstance().getStudentId(),
                new ApiCallback<List<MockTest>>(getActivity()) {

                    @Override
                    public void success(List<MockTest> mockTests, Response response) {
                        super.success(mockTests, response);
                        if (getActivity() != null && mockTests != null && !mockTests.isEmpty()) {
                            mMockTestList = mockTests;
                            showMockTests();
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if (getActivity() != null) {
                            ((AbstractBaseActivity) getActivity()).showToast("No mock tests available");
                            dismiss();
                        }
                    }
                });
    }

    private void postQuestionPaper(String entityId, String examTemplateId, String studentId, String subjectId, final boolean download) {
        PostQuestionPaperRequest postQuestionPaper = new PostQuestionPaperRequest();
        postQuestionPaper.idCollegeBatch = "";
        postQuestionPaper.idEntity = entityId;
        postQuestionPaper.idExamTemplate = examTemplateId;
        postQuestionPaper.idSubject = subjectId;
        postQuestionPaper.idStudent = studentId;
        ((AbstractBaseActivity)getActivity()).showProgress();
        ApiManager.getInstance(getActivity()).postQuestionPaper(Gson.get().toJson(postQuestionPaper),
            new ApiCallback<PostQuestionPaper>(getActivity()) {
                @Override
                public void success(PostQuestionPaper postQuestionPaper, Response response) {
                    super.success(postQuestionPaper, response);
                    if (getActivity() != null) {
                        ((AbstractBaseActivity)getActivity()).closeProgress();
                        if (postQuestionPaper != null && !TextUtils.isEmpty(postQuestionPaper.idTestQuestionPaper)) {
                            testQuestionPaperId = postQuestionPaper.idTestQuestionPaper;
                            goForward(download);
                        } else {
                            dismiss();
                        }
                    }
                }
            });
    }

    private void goForward(boolean download) {
        try {
            if (!download) {
                Intent intent = new Intent(getActivity(), TestInstructionsActivity.class);
                intent.putExtra(Constants.TEST_TITLE, "Mock Test");
                intent.putExtra("test_question_paper_id", testQuestionPaperId);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), TestDownloadService.class);
                intent.putExtra(Constants.TEST_TITLE, "Mock Test");
                intent.putExtra("testQuestionPaperId", testQuestionPaperId);
                intent.putExtra("studentId", LoginUserCache.getInstance().getStudentId());
                intent.putExtra("selectedMockTest", Gson.get().toJson(selectedMockTest));
                getActivity().startService(intent);
                Toast.makeText(getActivity(), "Downloading Mock test paper in background", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }
}