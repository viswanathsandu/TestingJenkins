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
public class EditProfileDialogFragment extends DialogFragment {

    IUpdateExamDetailsListener updateExamDetailsListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile_dialog_fragment, container, false);
        ButterKnife.bind(this, v);
        getDialog().setTitle("Edit Profile");
        return v;
    }

    public interface IUpdateExamDetailsListener {
        void onUpdateExamDetails(View view, ExamDetail data, int position);
    }

}