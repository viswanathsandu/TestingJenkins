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
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.adapters.PartTestGridAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.PartTestGridElement;
import com.education.corsalite.models.responsemodels.PartTestModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.client.Response;

/**
 * Created by madhuri on 3/26/16.
 */
public class PartTestDialog extends DialogFragment {

    @Bind(R.id.tv_recommended)
    TextView tvReceommended;
    @Bind(R.id.tv_all)
    TextView tvAll;
    @Bind(R.id.parttest_recyclerView)
    RecyclerView recyclerView;

    private int idCourseSubject;
    private List<PartTestGridElement> recommendedList;
    private List<PartTestGridElement> allList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_parttestgrid, container, false);
        ButterKnife.bind(this, v);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        getIntentData();
        loadPartTest();
        return v;
    }

    private void getIntentData() {
        Bundle bundle = getArguments();
        idCourseSubject = bundle.getInt("idCourseSubject");
    }

    private void loadPartTest() {
        ApiManager.getInstance(getActivity()).getPartTestGrid(LoginUserCache.getInstance().loginResponse.studentId,
                AbstractBaseActivity.selectedCourse.courseId.toString(), idCourseSubject + "", new ApiCallback<PartTestModel>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        ((AbstractBaseActivity) getActivity()).showToast("No part tests available");
                        dismiss();
                    }

                    @Override
                    public void success(PartTestModel partTestModel, Response response) {
                        super.success(partTestModel, response);
                        allList = partTestModel.partTestGrid;
                        if (allList != null) {
                            filterRecommended();
                            tvReceommended.setSelected(true);
                            tvAll.setSelected(false);
                           // tvReceommended.setBackgroundColor(getResources().getColor(R.color.gray));
                           // tvAll.setBackgroundColor(getResources().getColor(R.color.white));
                            loadRecommendedList();
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

    private void loadRecommendedList(){
        if(recommendedList != null && !recommendedList.isEmpty()){
            PartTestGridAdapter adapter = new PartTestGridAdapter(recommendedList,getActivity().getLayoutInflater());
            recyclerView.setAdapter(adapter);
        }
    }

    private void loadAllList(){
        if(allList != null && !allList.isEmpty()){
            PartTestGridAdapter adapter = new PartTestGridAdapter(allList,getActivity().getLayoutInflater());
            recyclerView.setAdapter(adapter);
        }
    }

    @OnClick({R.id.iv_cancel,R.id.tv_all,R.id.tv_cancel,R.id.tv_recommended,R.id.tv_CAT,R.id.tv_taketest})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_cancel:
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_recommended:
                if(!tvReceommended.isSelected()){
                   // tvReceommended.setBackgroundColor(getResources().getColor(R.color.gray));
                   // tvReceommended.setBackgroundResource(R.drawable.ambershape);
                    //tvAll.setBackgroundColor(getResources().getColor(R.color.white));
                    loadRecommendedList();
                    tvReceommended.setSelected(true);
                    tvAll.setSelected(false);
                }
                break;
            case R.id.tv_all:
                if(!tvAll.isSelected()){
                   // tvReceommended.setBackgroundColor(getResources().getColor(R.color.white));
                    //tvAll.setBackgroundColor(getResources().getColor(R.color.gray));
                    tvAll.setSelected(true);
                    tvReceommended.setSelected(false);
                    loadAllList();
                }
                break;
            case R.id.tv_taketest:
                break;
            case R.id.tv_CAT:
                break;
            default:dismiss();
        }
    }
}
