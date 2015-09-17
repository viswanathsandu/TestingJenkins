package com.education.corsalite.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.adapters.MessageAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.responsemodels.CorsaliteError;
import com.education.corsalite.responsemodels.MessageResponse;
import com.education.corsalite.services.ApiClientService;

import retrofit.client.Response;

/**
 * Created by Girish on 12/09/15.
 */
public class MessageTabFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayout layoutEmpty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.userdetail_recyclerView);
        layoutEmpty = (LinearLayout) v.findViewById(R.id.layout_empty);

        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getMessage(inflater);
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

    private void getMessage(final LayoutInflater inflater) {
        ApiManager.getInstance(getActivity()).getMessages(LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<MessageResponse>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        hideRecyclerView();
                    }

                    @Override
                    public void success(MessageResponse messageResponse, Response response) {
                        if (messageResponse.isSuccessful() && messageResponse != null &&
                                messageResponse.messages != null && messageResponse.messages.size() > 0) {
                            mAdapter = new MessageAdapter(messageResponse.messages, inflater);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            hideRecyclerView();
                        }
                    }
                });
    }

    private void hideRecyclerView() {

        mRecyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

}