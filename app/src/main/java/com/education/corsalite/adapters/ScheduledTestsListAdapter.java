package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.db.ScheduledTestList;
import com.education.corsalite.models.db.ScheduledTestsArray;
import com.education.corsalite.utils.L;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Girish on 18/10/15.
 */
public class ScheduledTestsListAdapter extends AbstractRecycleViewAdapter {

    LayoutInflater inflater;
    IScheduledTestSelectedListener mListener;

    public ScheduledTestsListAdapter(ScheduledTestList scheduledTest, LayoutInflater inflater) {
        this(scheduledTest);
        this.inflater = inflater;
    }

    private ScheduledTestsListAdapter(ScheduledTestList scheduledTest) {
        addAll(scheduledTest.MockTest);
    }

    public void setScheduledTestSelectedListener(IScheduledTestSelectedListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public ScheduledTestDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduledTestDataHolder(inflater.inflate(R.layout.mocktest_spinner_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ScheduledTestDataHolder) holder).bindData(position, (ScheduledTestsArray) getItem(position));
    }

    public class ScheduledTestDataHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.mock_test_txt)
        TextView tvName;
        @Bind(R.id.mock_test_time_txt)
        TextView tvTime;
        @Bind(R.id.download_test)
        ImageView ivDownload;
        View parent;

        public ScheduledTestDataHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final ScheduledTestsArray mockTest) {
            tvName.setText(mockTest.examName);
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mockTest.startTime);
                tvTime.setText(new SimpleDateFormat("yyyy/MM/dd hh:mm a").format(date));
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onScheduledTestSelected(position);
                    }
                }
            });
            ivDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onSchedledDownload(position);
                    }
                }
            });
        }
    }

    public interface IScheduledTestSelectedListener {
        void onScheduledTestSelected(int position);

        void onSchedledDownload(int position);
    }
}
