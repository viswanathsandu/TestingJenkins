package com.education.corsalite.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.StartMockTestActivity;
import com.education.corsalite.adapters.MockTestsListAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.db.MockTest;
import com.education.corsalite.models.requestmodels.PostQuestionPaperRequest;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.PostQuestionPaper;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.services.TestDownloadService;
import com.education.corsalite.utils.Gson;


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
        if(getActivity() != null) {
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
        ApiManager.getInstance(getActivity()).getMockTests(
                AbstractBaseActivity.getSelectedCourseId(),
                LoginUserCache.getInstance().getStudentId(),
                new ApiCallback<List<MockTest>>(getActivity()) {

                    @Override
                    public void success(List<MockTest> mockTests, Response response) {
                        super.success(mockTests, response);
                        if (mockTests != null && !mockTests.isEmpty()) {
                            mMockTestList = mockTests;
                            showMockTests();
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        ((AbstractBaseActivity) getActivity()).showToast("No mock tests available");
                        dismiss();
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
        showProgress();
        ApiManager.getInstance(getActivity()).postQuestionPaper(Gson.get().toJson(postQuestionPaper),
            new ApiCallback<PostQuestionPaper>(getActivity()) {
                @Override
                public void success(PostQuestionPaper postQuestionPaper, Response response) {
                    super.success(postQuestionPaper, response);
                    if (postQuestionPaper != null && !TextUtils.isEmpty(postQuestionPaper.idTestQuestionPaper)) {
                        testQuestionPaperId = postQuestionPaper.idTestQuestionPaper;
                        getIndex(postQuestionPaper.idTestQuestionPaper, null, "N", download);
                    } else {
                        dialog.dismiss();
                    }
                }
            });
    }


    private void getIndex(String questionPaperId, String answerPaperId, String all, final boolean download){
        ApiManager.getInstance(getActivity()).getTestPaperIndex(questionPaperId, answerPaperId, all, new ApiCallback<TestPaperIndex>(getActivity()) {
            @Override
            public void success(TestPaperIndex testPaperIndexes, Response response) {
                super.success(testPaperIndexes, response);
                dialog.dismiss();
                dismiss();
                if (testPaperIndexes != null) {
                    if(!download) {
                        Intent intent = new Intent(getActivity(), StartMockTestActivity.class);
                        intent.putExtra("Test_Instructions", Gson.get().toJson(testPaperIndexes));
                        intent.putExtra("test_question_paper_id", testQuestionPaperId);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), TestDownloadService.class);
                        intent.putExtra("testQuestionPaperId",testQuestionPaperId);
                        intent.putExtra("Test_Instructions", Gson.get().toJson(testPaperIndexes));
                        String mockTestStr = Gson.get().toJson(selectedMockTest);
                        intent.putExtra("selectedMockTest",mockTestStr);
                        getActivity().startService(intent);
                        Toast.makeText(getActivity(), "Downloading Mock test paper in background", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void showProgress(){
        ProgressBar pbar = new ProgressBar(getActivity());
        pbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(pbar);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}