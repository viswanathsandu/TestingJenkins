package com.education.corsalite.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ExamEngineActivity;
import com.education.corsalite.adapters.ExamSpinnerAdapter;
import com.education.corsalite.adapters.PartTestGridAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Exam;
import com.education.corsalite.models.responsemodels.PartTestGridElement;
import com.education.corsalite.models.responsemodels.PartTestModel;
import com.education.corsalite.services.TestDownloadService;
import com.education.corsalite.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit.client.Response;

/**
 * Created by madhuri on 3/26/16.
 */
public class PartTestDialog extends BaseDialogFragment {

    @Bind(R.id.tv_recommended) TextView tvReceommended;
    @Bind(R.id.tv_all) TextView tvAll;
    @Bind(R.id.parttest_recyclerView) RecyclerView recyclerView;
    @Bind(R.id.checkbox_adaptive_learning) CheckBox catCheckBox;
    @Bind(R.id.btn_download) Button downloadBtn;
    @Bind(R.id.exam_id_selection_layout) View examSelectionLayout;
    @Bind(R.id.exam_spinner) Spinner examsSpinner;
    @Bind(R.id.exam_selected_txt) TextView selectedExamTxt;

    private int idCourseSubject;
    private String subjectName;
    private List<PartTestGridElement> recommendedList;
    private List<PartTestGridElement> allList;
    private PartTestGridAdapter adapter;
    private boolean mIsAdaptiveTest;
    private Exam selectedExam;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_parttestgrid, container, false);
        ButterKnife.bind(this, v);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        getDialog().setTitle("Part Test");
        getDialog().getWindow().setBackgroundDrawableResource(R.color.white);
        getIntentData();
        loadPartTest(null);
        return v;
    }

    private void getIntentData() {
        Bundle bundle = getArguments();
        idCourseSubject = bundle.getInt("idCourseSubject");
        subjectName = bundle.getString("SubjectName");
    }

    private void loadExamSpinner(LinkedTreeMap<String, String> examIds) {
        List<Exam> exams = new ArrayList<>();
        for(Map.Entry<String,String> entry : examIds.entrySet()) {
            Exam exam = new Exam();
            exam.examId = entry.getKey();
            exam.examName = entry.getValue();
            exams.add(exam);
        }
        final ExamSpinnerAdapter adapter = new ExamSpinnerAdapter(getActivity(), R.layout.spinner_title_textview_notes, exams);
        examsSpinner.setAdapter(adapter);
        examsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedExam = ((Exam)parent.getAdapter().getItem(position));
                selectedExamTxt.setText(((Exam)parent.getAdapter().getItem(position)).examName);
                adapter.setSelectedPosition(position);
                loadPartTest(selectedExam.examId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        selectedExamTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                examsSpinner.performClick();
            }
        });
    }

    private void loadPartTest(final String selectedExamId) {
        showProgress();
        ApiManager.getInstance(getActivity()).getPartTestGrid(LoginUserCache.getInstance().loginResponse.studentId,
                AbstractBaseActivity.selectedCourse.courseId.toString(), idCourseSubject + "",
                selectedExamId, new ApiCallback<PartTestModel>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                        if(getActivity() != null) {
                            showToast("No part tests available");
                            dismiss();
                        }
                    }

                    @Override
                    public void success(PartTestModel partTestModel, Response response) {
                        super.success(partTestModel, response);
                        if(getActivity() != null) {
                            closeProgress();
                            if(partTestModel.examIds != null && !partTestModel.examIds.isEmpty()) {
                                if (selectedExamId == null) {
                                    if (partTestModel.examIds.size() == 1) {
                                        examSelectionLayout.setVisibility(View.GONE);
                                    } else {
                                        loadExamSpinner(partTestModel.examIds);
                                    }
                                }
                                allList = partTestModel.partTestGrid;
                                if (allList != null) {
                                    filterRecommended();
                                    tvReceommended.setSelected(true);
                                    tvAll.setSelected(false);
                                    loadRecommendedList();
                                }
                            }
                        }
                    }
                });
    }

    private void filterRecommended() {
        recommendedList = new ArrayList<>();
        for (PartTestGridElement element : allList) {
            if (element.isRecommended.equalsIgnoreCase("Y")) {
                recommendedList.add(element);
            }
        }
    }

    private void loadRecommendedList() {
        if (recommendedList != null && !recommendedList.isEmpty()) {
            adapter = new PartTestGridAdapter(recommendedList, getActivity().getLayoutInflater());
            recyclerView.setAdapter(adapter);
        }
    }

    private void loadAllList() {
        if (allList != null && !allList.isEmpty()) {
            adapter = new PartTestGridAdapter(allList, getActivity().getLayoutInflater());
            recyclerView.setAdapter(adapter);
        }
    }

    @OnClick({R.id.tv_all, R.id.btn_cancel, R.id.tv_recommended, R.id.btn_next, R.id.btn_download})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.tv_recommended:
                if (!tvReceommended.isSelected()) {
                    loadRecommendedList();
                    tvReceommended.setSelected(true);
                    tvAll.setSelected(false);
                }
                break;
            case R.id.tv_all:
                if (!tvAll.isSelected()) {
                    tvAll.setSelected(true);
                    tvReceommended.setSelected(false);
                    loadAllList();
                }
                break;
            case R.id.btn_next:
                startPartTest();
                dismiss();
                break;
            case R.id.btn_download:
                downloadPartTest();
                dismiss();
            default:
                dismiss();
        }
    }

    private void startPartTest() {
        Intent intent = new Intent(getActivity(), ExamEngineActivity.class);
        intent.putExtra(Constants.TEST_TITLE, "Part Test - " + subjectName);
        intent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.selectedCourse.courseId.toString());
        intent.putExtra(Constants.SELECTED_SUBJECTID, idCourseSubject + "");
        intent.putExtra(Constants.SELECTED_TOPIC, subjectName);
        intent.putExtra(Constants.ADAPIVE_LEAERNING, mIsAdaptiveTest);
        if(selectedExam != null) {
            intent.putExtra(Constants.PARTTEST_EXAMMODEL, new Gson().toJson(selectedExam));
        }
        if (adapter != null && adapter.getListData() != null) {
            intent.putExtra(Constants.PARTTEST_GRIDMODELS, new Gson().toJson(adapter.getListData()));
        }
        startActivity(intent);
    }

    private void downloadPartTest() {
        Intent intent = new Intent(getActivity(), TestDownloadService.class);
        intent.putExtra("selectedPartTest", subjectName);
        intent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.selectedCourse.courseId.toString());
        intent.putExtra("subjectId", idCourseSubject + "");
        if (adapter != null && adapter.getListData() != null) {
            intent.putExtra(Constants.PARTTEST_GRIDMODELS, new Gson().toJson(adapter.getListData()));
        }
        getActivity().startService(intent);
        Toast.makeText(getActivity(), "Downloading test paper in background", Toast.LENGTH_SHORT).show();
    }

    @OnCheckedChanged(R.id.checkbox_adaptive_learning)
    public void onCatCheckChanged() {
        mIsAdaptiveTest = catCheckBox.isChecked();
        downloadBtn.setVisibility(mIsAdaptiveTest ? View.GONE : View.VISIBLE);
    }
}
