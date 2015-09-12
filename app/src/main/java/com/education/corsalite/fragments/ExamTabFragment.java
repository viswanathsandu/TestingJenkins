package com.education.corsalite.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.adapters.ExamAdapter;
import com.education.corsalite.responsemodels.ExamDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Girish on 12/09/15.
 */
public class ExamTabFragment extends BaseFragment {

    private static final String ADAPTER_TYPE = "adapter_type";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_profile_tab,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.userdetail_recyclerView);

        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        List<ExamDetail> examDetailList = (List<ExamDetail>) getArguments().getSerializable("adapter_type");
        mAdapter = new ExamAdapter((ArrayList)examDetailList,inflater);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
