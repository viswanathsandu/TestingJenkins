package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.MessageAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.MessageResponse;

import retrofit.client.Response;

/**
 * Created by Girish on 12/09/15.
 */
public class MessageTabFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayout layoutEmpty;
    private TextView tvNoData;

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
        tvNoData = (TextView)v.findViewById(R.id.tv_no_data);

        tvNoData.setText("No Message Found");

        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getMessage(inflater);
        return v;
    }

    private void getMessage(final LayoutInflater inflater) {
        ApiManager.getInstance(getActivity()).getMessages(LoginUserCache.getInstance().loginResponse.studentId,
                new ApiCallback<MessageResponse>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        if(error!= null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
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