package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.fragments.AddExamScheduleDialogFragment;
import com.education.corsalite.models.responsemodels.ExamDetail;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 05/09/15.
 */
public class ExamAdapter extends AbstractRecycleViewAdapter {

    LayoutInflater inflater;
    IAddExamOnClickListener addExamOnClickListener;

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
        ((ExamDataHolder) holder).bindData(position, (ExamDetail) getItem(position));
    }


    public void setAddExamOnClickListener(IAddExamOnClickListener addExamOnClickListener){
        this.addExamOnClickListener = addExamOnClickListener;
    }
    public class ExamDataHolder extends RecyclerView.ViewHolder {
        private final String EMPTY_STRING = "";
        private final String ADD_HTML = "<u><font color=#87CEFA>Add</font></u>&nbsp;";
        @Bind(R.id.tv_exam) TextView examTxt;
        @Bind(R.id.tv_days_remaining) TextView daysRemainingTxt;
        @Bind(R.id.tv_exam_date) TextView examDateTxt;
        @Bind(R.id.tv_hall_ticket_number) TextView hallTicketTxt;

        View parent;

        public ExamDataHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void bindData(final int position, final ExamDetail examDetail) {
            if((position+1)% 2 == 0) {
                parent.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.tab_recycler_alternate_row));
            }
            examTxt.setText(examDetail.name == null ? EMPTY_STRING : examDetail.name);
            daysRemainingTxt.setText(examDetail.daysRemaining == null ? EMPTY_STRING : String.valueOf(examDetail.daysRemaining));
            if(examDetail.examDate == null || examDetail.examDate.isEmpty()){
                examDateTxt.setText(Html.fromHtml(ADD_HTML));
                examDateTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addExamOnClickListener.onAddExamClickListener(view, examDetail, AddExamScheduleDialogFragment.ADD_EXAM_DATE,position);
                    }
                });
            }else{

                examDateTxt.setText(examDetail.examDate.split("")[0]);
            }

            if(examDetail.hallTicketNumber == null || examDetail.hallTicketNumber.isEmpty()){
                hallTicketTxt.setText(Html.fromHtml(ADD_HTML));
                hallTicketTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addExamOnClickListener.onAddExamClickListener(view,examDetail,AddExamScheduleDialogFragment.ADD_HALL_TICKET,position);
                    }
                });
            }else{
                hallTicketTxt.setText(examDetail.hallTicketNumber);
            }

        }
    }
    public interface IAddExamOnClickListener {
        void onAddExamClickListener(View view,ExamDetail examDetail,String type,int position);
    }

}
