package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.adapters.LeaderBoardAdapter;
import com.education.corsalite.models.socket.response.LeaderBoardStudent;
import com.education.corsalite.models.socket.response.UpdateLeaderBoardEvent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Girish on 12/09/15.
 */
public class LeaderBoardFragment extends BaseFragment {

    @Bind(R.id.leader_board_recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.leader_list_layout)
    View leaderBoardLayout;
    @Bind(R.id.leader_board_btn)
    View leaderBoardBtn;

    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ragment_leader_board, container, false);
        ButterKnife.bind(this, v);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    private void loadLeaderBoard(List<LeaderBoardStudent> users) {
        mAdapter = new LeaderBoardAdapter(users, getActivity().getLayoutInflater());
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.close_btn)
    public void onCloseClick() {
        leaderBoardLayout.setVisibility(View.GONE);
        leaderBoardBtn.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.leader_board_btn)
    public void onLeaderBoardButtonClicked() {
        if (mAdapter != null && mAdapter.getItemCount() > 0) {
            leaderBoardLayout.setVisibility(View.VISIBLE);
            leaderBoardBtn.setVisibility(View.GONE);
        }
    }

    public void onEventMainThread(UpdateLeaderBoardEvent event) {
        loadLeaderBoard(event.getStudents());
    }
}