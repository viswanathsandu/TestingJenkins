package com.education.corsalite.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.education.corsalite.R;
import com.education.corsalite.adapters.CurrencyAdapter;
import com.education.corsalite.adapters.ExamAdapter;
import com.education.corsalite.adapters.MessageAdapter;
import com.education.corsalite.responsemodels.BaseModel;
import com.education.corsalite.responsemodels.ExamDetail;
import com.education.corsalite.responsemodels.Message;
import com.education.corsalite.responsemodels.VirtualCurrencyTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mt0060 on 12/09/15.
 */
public class MessageTabFragment extends BaseFragment {

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
        View v = inflater.inflate(R.layout.fragment_message,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.userdetail_recyclerView);

        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        List<Message> messageList = (List<Message>) getArguments().getSerializable("adapter_type");
        mAdapter = new MessageAdapter((ArrayList)messageList,inflater);
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
