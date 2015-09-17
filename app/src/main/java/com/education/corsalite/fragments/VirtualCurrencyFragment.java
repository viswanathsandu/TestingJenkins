package com.education.corsalite.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.adapters.CurrencyAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.responsemodels.CorsaliteError;
import com.education.corsalite.responsemodels.VirtualCurrencySummaryResponse;
import com.education.corsalite.services.ApiClientService;

import retrofit.client.Response;

/**
 * Created by Girish on 12/09/15.
 */
public class VirtualCurrencyFragment extends BaseFragment {

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
        View v = inflater.inflate(R.layout.fragment_currencylist, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.userdetail_recyclerView);

        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getTransactionHistory(inflater);
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

    private void getTransactionHistory(final LayoutInflater inflater) {
        ApiManager.getInstance(getActivity()).getVirtualCurrencyTransactions(LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<VirtualCurrencySummaryResponse>() {
                    @Override
                    public void failure(CorsaliteError error) {

                    }

                    @Override
                    public void success(VirtualCurrencySummaryResponse virtualCurrencySummaryResponse, Response response) {
                        mAdapter = new CurrencyAdapter(virtualCurrencySummaryResponse.virtualCurrencyTransaction, inflater);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });
    }

}