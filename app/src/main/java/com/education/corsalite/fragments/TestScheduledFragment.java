package com.education.corsalite.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.adapters.ScheduledTestAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ScheduledTest;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created on 23/01/16.
 *
 * @author Meeth D Jain
 */
public class TestScheduledFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @Bind(R.id.ll_container)
    LinearLayout mContainerLayout;
    @Bind(R.id.ll_error)
    LinearLayout mErrorLayout;
    @Bind(R.id.list_view_test_scheduled)
    ListView mListViewScheduledTests;
    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_failure_text)
    TextView mFailureTextView;

    private ScheduledTestAdapter mAdapter;
    private List<ScheduledTest> mScheduledTestList;

    public static TestScheduledFragment newInstance(Bundle bundle) {
        TestScheduledFragment fragment = new TestScheduledFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static String getMyTag() {
        return "tag_fragment_test_scheduled";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScheduledTestList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_scheduled, container, false);
        ButterKnife.bind(this, rootView);
        mAdapter = new ScheduledTestAdapter(getActivity(), new ArrayList<ScheduledTest>());
        mListViewScheduledTests.setAdapter(mAdapter);

        getScheduledTests();
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ScheduledTest scheduledTest = mScheduledTestList.get(position);
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Start " + scheduledTest.examName)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.SELECTED_EXAM_NAME, scheduledTest.examName);
                        bundle.putString(Constants.SELECTED_QUESTION_PAPER, scheduledTest.testQuestionPaperId);
                        startActivity(new Intent(getActivity(), ExamEngineActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void getScheduledTests() {
        ApiManager.getInstance(getActivity()).getScheduledTests(LoginUserCache.getInstance().getStudentId(),
                new ApiCallback<List<ScheduledTest>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        L.error(error.message);
                        mProgressBar.setVisibility(View.GONE);
                        mErrorLayout.setVisibility(View.VISIBLE);
                        mFailureTextView.setText("Sorry, No exam scheduled");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        L.error(error.getMessage());
                        mProgressBar.setVisibility(View.GONE);
                        mErrorLayout.setVisibility(View.VISIBLE);
                        mFailureTextView.setText("Sorry, couldn't fetch data");
                    }

                    @Override
                    public void success(List<ScheduledTest> scheduledTests, Response response) {
                        super.success(scheduledTests, response);
                        if (getActivity().isFinishing() || getActivity().isDestroyed() || !isResumed()) {
                            return;
                        }
                        setData(scheduledTests);
                        mProgressBar.setVisibility(View.GONE);
                        mContainerLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setData(List<ScheduledTest> scheduledTests) {
        mScheduledTestList = scheduledTests;
        mAdapter.updateData(mScheduledTestList);
        mAdapter.notifyDataSetChanged();
    }
}
