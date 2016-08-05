package com.education.corsalite.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.adapters.FullQuestionsAdapter;
import com.education.corsalite.models.responsemodels.ExamModel;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Viswanath on 05/12/15.
 */
public class FullQuestionDialog extends DialogFragment {

    @Bind(R.id.tv_close)
    TextView tvClose;
    @Bind(R.id.questions_recyclerview)
    RecyclerView questionsRecyclerView;

    private FullQuestionsAdapter fullQuestionsAdapter;
    private List<ExamModel> localExamModelList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fullquestion, container, false);
        ButterKnife.bind(this, v);
        getAvailableArguments();
        loadAdapter(inflater);
        return v;
    }

    private void getAvailableArguments() {
        localExamModelList = ((ExamEngineActivity) getActivity()).getLocalExamModelList();
        if (localExamModelList != null && localExamModelList.size() > 0) {
            Collections.sort(localExamModelList);
        }
    }

    private void loadAdapter(LayoutInflater inflater) {
        fullQuestionsAdapter = new FullQuestionsAdapter(getActivity(), localExamModelList, inflater);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        questionsRecyclerView.setAdapter(fullQuestionsAdapter);
    }
}
