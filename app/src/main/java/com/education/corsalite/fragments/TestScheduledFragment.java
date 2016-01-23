package com.education.corsalite.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ScheduledTest;
import com.education.corsalite.utils.L;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created on 23/01/16.
 *
 * @author Meeth D Jain
 */
public class TestScheduledFragment extends BaseFragment {

    @Bind(R.id.ll_container)
    LinearLayout mContainerLayout;
    @Bind(R.id.list_view_test_scheduled)
    ListView mListViewScheduledTests;
    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_failure_text)
    TextView mFailureTextView;

    ScheduledTestAdapter mAdapter;
    List<ScheduledTest> mScheduledTestList;

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
        mAdapter = new ScheduledTestAdapter();
        mListViewScheduledTests.setAdapter(mAdapter);

        //getScheduledTests();
        return rootView;
    }

    private void getScheduledTests() {
        ApiManager.getInstance(getActivity()).getScheduledTests(LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<List<ScheduledTest>>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        L.error(error.message);
                        mProgressBar.setVisibility(View.GONE);
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
        mAdapter.notifyDataSetChanged();
    }

    class ScheduledTestAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mScheduledTestList.size();
        }

        @Override
        public ScheduledTest getItem(int position) {
            return mScheduledTestList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_test_scheduled, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mDueDate.setText(getItem(position).dueDate);
            viewHolder.mStartTime.setText(getItem(position).startTime);
            viewHolder.mExamName.setText(getItem(position).examName);

            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.txt_view_exam_name)
        TextView mExamName;
        @Bind(R.id.txt_view_due_date)
        TextView mDueDate;
        @Bind(R.id.txt_view_start_time)
        TextView mStartTime;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
