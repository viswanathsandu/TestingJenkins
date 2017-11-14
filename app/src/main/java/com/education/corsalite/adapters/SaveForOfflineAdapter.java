package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.ExamHistory;
import com.education.corsalite.utils.L;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 27/11/15.
 */
public class SaveForOfflineAdapter extends AbstractRecycleViewAdapter {

    private SetOnExamHistoryClick setOnExamHistoryClick;

    public interface SetOnExamHistoryClick {
        void onItemClick(int position);
    }

    LayoutInflater inflater;

    public SaveForOfflineAdapter(List<ContentIndex> mContentIndexs, LayoutInflater inflater,
            SetOnExamHistoryClick setOnExamHistoryClick) {
        this(mContentIndexs);
        this.inflater = inflater;
        this.setOnExamHistoryClick = setOnExamHistoryClick;
    }

    public SaveForOfflineAdapter(List<ContentIndex> mContentIndexs) {
        addAll(mContentIndexs);
    }

    @Override
    public SaveForOfflineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SaveForOfflineViewHolder(inflater.inflate(R.layout.save_for_offline_item, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SaveForOfflineViewHolder) holder).bindData(position, (ExamHistory) getItem(position));
    }

    public class SaveForOfflineViewHolder extends RecyclerView.ViewHolder {

        View parent;
        @Bind(R.id.node_selectors)
        TextView date;
        @Bind(R.id.month_year_txt)
        TextView monthYear;
        @Bind(R.id.time_txt)
        TextView time;
        @Bind(R.id.tv_score)
        TextView score;
        @Bind(R.id.tv_exam)
        TextView exam;
        @Bind(R.id.tv_type)
        TextView type;
        @Bind(R.id.tv_rank)
        TextView rank;
        @Bind(R.id.tv_status)
        TextView status;

        public SaveForOfflineViewHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final ExamHistory examHistory) {
            this.date.setText(examHistory.getDate());
            this.time.setText(examHistory.getTime());
            this.monthYear.setText(examHistory.getMonthYear());
            this.exam.setText(examHistory.examName);
            this.type.setText(examHistory.testType);
            this.rank.setText(examHistory.rank);
            this.status.setText("");
            this.score.setText("-");
            if (!TextUtils.isEmpty(examHistory.totalScore) && examHistory.totalScore.contains(".")) {
                int scoreInt = -1;
                try {
                    scoreInt = (int) (Double.parseDouble(examHistory.totalScore));
                } catch (Exception e) {
                    L.error(e.getMessage(), e);
                }
                this.score.setText(scoreInt == -1 ? "-" : scoreInt + "");
            }
            if (!TextUtils.isEmpty(examHistory.status)) {
                this.status.setText(examHistory.status);
            }
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setOnExamHistoryClick.onItemClick(position);
                }
            });


        }
    }

}
