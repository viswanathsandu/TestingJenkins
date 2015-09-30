package com.education.corsalite.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.ExamDetail;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aastha on 20/09/15.
 */
public class AddExamScheduleDialogFragment extends DialogFragment implements DatePickerDialogFragment.IDateSelectionListener{

    @Bind(R.id.et_datePicker)EditText examDatePicker;
    @Bind(R.id.et_hall_ticket)EditText hallTicketNumber;
    @Bind(R.id.rg_add_exam_list)RadioGroup addExamGroup;
    @Bind(R.id.vs_add_exam)ViewSwitcher addExamSwitcher;
    @Bind(R.id.tv_cancel1)TextView cancel1;
    @Bind(R.id.tv_cancel2)TextView cancel2;

    @Bind(R.id.tv_next)TextView next;
    @Bind(R.id.tv_add)TextView add;

    List<ExamDetail> examDetails;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_exam_schedule, container, false);
        ButterKnife.bind(this, v);

        examDetails = (List<ExamDetail>)getArguments().getSerializable("examDetailList");
        getDialog().setTitle("Add an Exam Schedule");
        populateExamList();
        initialize();

        return v;
    }

    private void populateExamList(){
        for(ExamDetail exam:examDetails){
            if(!exam.isExamDetailsAvailable()){
                RadioButton button = new RadioButton(getActivity());
                button.setText(exam.name);
                addExamGroup.addView(button);
            }
        }
    }

    private void initialize(){
        cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        if (addExamGroup.getCheckedRadioButtonId() != -1) {
            next.setEnabled(true);
        }else{
            Toast.makeText(getActivity(),"Select any Exam to Proceed",Toast.LENGTH_SHORT).show();
        }
               next.setOnClickListener(new View.OnClickListener() {
                                    @Override
                    public void onClick(View view) {
                        addExamSwitcher.showNext();
                                    }
                                 }
        );
        examDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implement dialog from datepicker
                //DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                //datePickerDialogFragment.show(getFragmentManager(),"datePicker");
            }
        });

       // examDatePicker.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_sidemenu_calendar, 0, 0);



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : update API
            }
        });
    }

    @Override
    public void onDateSelect(int day, int month, int year) {
        examDatePicker.setText(day+"/"+month+"/"+year);
    }
}