package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.ExamDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 20/09/15.
 */
public class AddExamScheduleDialogFragment extends DialogFragment {

    @Bind(R.id.datePicker)DatePicker examDatePicker;
    @Bind(R.id.et_hall_ticket)EditText hallTicketNumber;
    @Bind(R.id.btn_cancel)Button cancel;
    @Bind(R.id.btn_submit)Button submit;
    @Bind(R.id.ll_add_exam_date)LinearLayout addExamDate;
    @Bind(R.id.ll_add_hall_tckt)TextInputLayout addHallTicket;

    int position = -1;
    IUpdateExamDetailsListener updateExamDetailsListener;
    public static final String ADD_EXAM_DATE = "addExamDate";
    public static final String ADD_HALL_TICKET = "addHallTicket";
    private final static long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;


    ExamDetail examDetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_exam_schedule, container, false);
        ButterKnife.bind(this, v);

        examDetail =(ExamDetail) getArguments().getSerializable("examDetail");
        String type = getArguments().getString("type");
        getDialog().setTitle(examDetail.name);
        initialize(type);
        return v;
    }

    public void setUpdateExamDetailsListener(IUpdateExamDetailsListener updateExamDetailsListener,int position) {
        this.updateExamDetailsListener = updateExamDetailsListener;
        this.position = position;
    }

    private void initialize(final String type){

        if(type.equalsIgnoreCase(ADD_EXAM_DATE)){
            addExamDate.setVisibility(View.VISIBLE);
            addHallTicket.setVisibility(View.GONE);
        }else if(type.equalsIgnoreCase(ADD_HALL_TICKET)){
            addHallTicket.setVisibility(View.VISIBLE);
            addExamDate.setVisibility(View.GONE);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(type.equalsIgnoreCase(ADD_EXAM_DATE)){
                    String dateString = examDatePicker.getDayOfMonth()+"/" +(examDatePicker.getMonth()+1)+"/"+examDatePicker.getYear();
                    //Update days remaininig
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date chosenDate = new Date();
                    Date todayDate = new Date();
                    try
                    {
                        chosenDate = formatter.parse(dateString);
                    } catch(ParseException e)
                    {

                    }
                    examDetail.examDate = dateString;
                    examDetail.daysRemaining  = ((int)((chosenDate.getTime()/MILLISECS_PER_DAY)
                                    -(int)(todayDate.getTime()/MILLISECS_PER_DAY)));

                }else if(type.equalsIgnoreCase(ADD_HALL_TICKET))
                {
                    examDetail.hallTicketNumber = hallTicketNumber.getText().toString();
                }

                updateExamDetailsListener.onUpdateExamDetails(view, examDetail, position);
                getDialog().dismiss();

            }
        });

    }

    public interface IUpdateExamDetailsListener {
        void onUpdateExamDetails(View view,ExamDetail data,int position);
    }

}