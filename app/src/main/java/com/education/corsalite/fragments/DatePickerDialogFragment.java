package com.education.corsalite.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.education.corsalite.R;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Aastha
 */
public class DatePickerDialogFragment extends DialogFragment {

    private IDateSelectionListener dateSelectionListener;
    @Bind(R.id.datePicker)
    DatePicker mDatePicker;
    @Bind(R.id.btn_date_picker_cancel)
    Button mButtonCancel;
    @Bind(R.id.btn_date_picker_save)
    Button mButtonSave;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IDateSelectionListener) {
            dateSelectionListener = (IDateSelectionListener) activity;
        } else {
            throw new RuntimeException("activity must implement " + IDateSelectionListener.class.getCanonicalName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_date_picker, container, false);
        ButterKnife.bind(this, view);
        mButtonCancel.setOnClickListener(cancelClickListerner);
        mButtonSave.setOnClickListener(saveClickListener);
        mDatePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
        return view;
    }

    View.OnClickListener cancelClickListerner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getDialog().dismiss();
        }
    };

    View.OnClickListener saveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dateSelectionListener.onDateSelect(mDatePicker.getDayOfMonth(), mDatePicker.getMonth() + 1, mDatePicker.getYear());
            getDialog().dismiss();
        }
    };


    public interface IDateSelectionListener {
        void onDateSelect(int day, int month, int year);
    }

}
