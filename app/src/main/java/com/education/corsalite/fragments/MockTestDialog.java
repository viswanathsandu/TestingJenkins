package com.education.corsalite.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.adapters.MockTestsListAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.MockTest;
import com.education.corsalite.models.responsemodels.CorsaliteError;

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

    private List<MockTest> mMockTestList;

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
        final LinearLayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvMockTestList.setLayoutManager(layoutManager);
        MockTestsListAdapter mockTestsListAdapter = new MockTestsListAdapter(mMockTestList, getActivity().getLayoutInflater());
        mockTestsListAdapter.setMockTestSelectedListener(this);
        rvMockTestList.setAdapter(mockTestsListAdapter);
    }

    @Override
    public void onMockTestSelected(int position) {
        dismiss();
        Intent intent = new Intent(getActivity(), ExamEngineActivity.class);
        startActivity(intent);
    }

    private void loadMockTests() {
        ApiManager.getInstance(getActivity()).getMockTests(
                AbstractBaseActivity.selectedCourse.courseId.toString(),
                LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<List<MockTest>>(getActivity()) {

                    @Override
                    public void success(List<MockTest> mockTests, Response response) {
                        super.success(mockTests, response);
                        if(mockTests != null && !mockTests.isEmpty()) {
                            mMockTestList = mockTests;
                            showMockTests();
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        ((AbstractBaseActivity)getActivity()).showToast("No mock tests available");
                    }
                });
    }
}
