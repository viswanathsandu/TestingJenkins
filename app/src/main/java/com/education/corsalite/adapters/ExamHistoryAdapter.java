package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.ExamHistory;
import com.education.corsalite.utils.L;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 27/11/15.
 */
public class ExamHistoryAdapter extends AbstractRecycleViewAdapter {

    private SetOnExamHistoryClick setOnExamHistoryClick;

    public interface SetOnExamHistoryClick {
        void onItemClick(int position);
    }

    LayoutInflater inflater;

    public ExamHistoryAdapter(List<ExamHistory> examHistoryList, LayoutInflater inflater, SetOnExamHistoryClick setOnExamHistoryClick) {
        this(examHistoryList);
        this.inflater = inflater;
        this.setOnExamHistoryClick = setOnExamHistoryClick;
    }

    public ExamHistoryAdapter(List<ExamHistory> examHistoryList) {
        addAll(examHistoryList);
    }

    @Override
    public ExamHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExamHistoryViewHolder(inflater.inflate(R.layout.row_exam_history, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ExamHistoryViewHolder) holder).bindData(position, (ExamHistory) getItem(position));
    }

    public class ExamHistoryViewHolder extends RecyclerView.ViewHolder {

        View parent;
        @Bind(R.id.tv_examhistory_date) TextView examHistoryDate;
        @Bind(R.id.tv_score) TextView score;
        @Bind(R.id.tv_exam) TextView exam;
        @Bind(R.id.tv_type) TextView type;
        @Bind(R.id.tv_rank) TextView rank;
        @Bind(R.id.tv_status) TextView status;

        public ExamHistoryViewHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final ExamHistory examHistory) {
            // different color for alternate rows
            if ((position + 1) % 2 == 0) {
                parent.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.tab_recycler_alternate_row));
            } else {
                parent.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.white));
            }
            this.examHistoryDate.setText(examHistory.getTime());
            this.exam.setText(examHistory.examName);
            this.type.setText(examHistory.testType);
            this.rank.setText(examHistory.rank);
            if(!TextUtils.isEmpty(examHistory.totalScore)) {
                if(examHistory.totalScore.contains(".")) {
                    this.status.setText("Completed");
                    int scoreInt = -1;
                    try {
                        scoreInt = (int) (Double.parseDouble(examHistory.totalScore));
                    } catch (Exception e) {
                        L.error(e.getMessage(), e);
                    }
                    this.score.setText(scoreInt == -1 ? "-" : scoreInt+"");
                } else {
                    this.status.setText(examHistory.totalScore);
                }
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
