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
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.adapters.MessageAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.Message;
import com.education.corsalite.models.responsemodels.MessageResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by Girish on 12/09/15.
 */
public class MessageTabFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
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
        tvNoData.setTextAppearance(getActivity(),R.style.user_profile_text);
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MessageAdapter(new ArrayList<Message>(), inflater);
        mRecyclerView.setAdapter(mAdapter);
        getMessage(inflater);
        return v;
    }

    public void onEvent(Course course) {


    }

    private void getMessage(final LayoutInflater inflater) {
        ApiManager.getInstance(getActivity()).getMessages(LoginUserCache.getInstance().getStudentId(),
                AbstractBaseActivity.getSelectedCourseId(), LoginUserCache.getInstance().getEntityId(),
                new ApiCallback<MessageResponse>(getActivity()) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if(error!= null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                        hideRecyclerView();
                    }

                    @Override
                    public void success(MessageResponse messageResponse, Response response) {
                        super.success(messageResponse, response);
                        List<Message> messages = messageResponse.messages;
                        if (messages != null && messages.size() > 0) {
                            layoutEmpty.setVisibility(View.GONE);
                            mAdapter.loadData(messages);
                        } else {
                            hideRecyclerView();
                        }
                    }
                });
    }

    private void hideRecyclerView() {
        layoutEmpty.setVisibility(View.VISIBLE);
    }
}