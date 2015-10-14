package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.ExamAdapter;
import com.education.corsalite.models.responsemodels.ExamDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish on 12/09/15.
 */
public class ExamTabFragment extends BaseFragment implements ExamAdapter.IAddExamOnClickListener,AddExamScheduleDialogFragment.IUpdateExamDetailsListener{

    private static final String ADAPTER_TYPE = "adapter_type";
    List<ExamDetail> examDetailList;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayout layoutEmpty;
    private TextView tvNoData;
    ExamAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_profile_tab, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.userdetail_recyclerView);
        layoutEmpty = (LinearLayout) v.findViewById(R.id.layout_empty);
        tvNoData = (TextView)v.findViewById(R.id.tv_no_data);
        tvNoData.setText("No Exam Data Found");

        mRecyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);

        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
       examDetailList = (List<ExamDetail>) getArguments().getSerializable(ADAPTER_TYPE);
        if(examDetailList != null && examDetailList.size() > 0) {
            mAdapter = new ExamAdapter((ArrayList) examDetailList, inflater);
            mAdapter.setAddExamOnClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            hideRecyclerView();
        }

        return v;
    }

    private void hideRecyclerView() {
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAddExamClickListener(View view, ExamDetail examDetail,String type,int position) {
        AddExamScheduleDialogFragment dialogFragment = new AddExamScheduleDialogFragment();
        dialogFragment.setUpdateExamDetailsListener(this, position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("examDetail", examDetail);
        bundle.putString("type",type);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(),"addExam");

    }

    @Override
    public void onUpdateExamDetails(View view, ExamDetail examDetail,int position) {
        //TODO UPDATE API

        examDetailList.add(position,examDetail);
        mAdapter.notifyItemChanged(position);
    }


}
