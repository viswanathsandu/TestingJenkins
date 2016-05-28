package com.corsalite.tabletapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.models.responsemodels.ScheduledTest;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vissu on 1/24/16.
 */
public class ScheduledTestAdapter extends BaseAdapter {

    private Context mContext;
    private List<ScheduledTest> mScheduledTestList;

    public ScheduledTestAdapter(Context context, List<ScheduledTest> scheduledTestList) {
        this.mContext = context;
        this.mScheduledTestList = scheduledTestList;
    }

    public void updateData(List<ScheduledTest> scheduledTestList) {
        this.mScheduledTestList = scheduledTestList;
        notifyDataSetChanged();
    }

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
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_test_scheduled, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mDueDate.setText(getItem(position).dueDate);
        viewHolder.mStartTime.setText(getItem(position).startTime);
        viewHolder.mExamName.setText(getItem(position).examName);

        return convertView;
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