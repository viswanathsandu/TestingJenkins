package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.responsemodels.ExamDetail;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 05/09/15.
 */
public class ExamAdapter extends AbstractRecycleViewAdapter {

    LayoutInflater inflater;

    public ExamAdapter(List<ExamDetail> examDetailList, LayoutInflater inflater) {
        this(examDetailList);
        this.inflater = inflater;
    }

    public ExamAdapter(List<ExamDetail> examDetailList) {
        addAll(examDetailList);
    }

    @Override
    public ExamDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExamDataHolder(inflater.inflate(R.layout.row_exams_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ExamDataHolder) holder).bindData(position, (ExamDetail)getItem(position));
    }

    public class ExamDataHolder extends RecyclerView.ViewHolder {
        private final String EMPTY_STRING = "";
        @Bind(R.id.tv_exam) TextView examTxt;
        @Bind(R.id.tv_days_remaining) TextView daysRemainingTxt;
        @Bind(R.id.tv_exam_date) TextView examDateTxt;
        @Bind(R.id.tv_hall_ticket_number) TextView hallTicketTxt;

        public ExamDataHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final ExamDetail examDetail) {
            examTxt.setText(examDetail.name == null ? EMPTY_STRING : examDetail.name);
            daysRemainingTxt.setText(examDetail.daysRemaining == null ? EMPTY_STRING : String.valueOf(examDetail.daysRemaining));
            examDateTxt.setText(examDetail.examDate == null ? EMPTY_STRING : examDetail.examDate);
            hallTicketTxt.setText(examDetail.hallTicketNumber == null ? EMPTY_STRING : examDetail.hallTicketNumber);
        }
    }
}
