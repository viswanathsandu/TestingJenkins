package com.education.corsalite.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.AddFriendsActivity;
import com.education.corsalite.activities.ChallengeActivity;
import com.education.corsalite.adapters.DividerItemDecoration;
import com.education.corsalite.adapters.FriendsAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.helpers.WebSocketHelper;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.FriendsData;
import com.education.corsalite.models.socket.response.ChallengeUserList;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.utils.L;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class InviteFriendsFragment extends BaseFragment {

    private static final String ARG_CALLBACK = "ARG_CALLBACK";
    private RecyclerView mRecyclerView;
    private FriendsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ChallengeActivity.FriendsListCallback mFriendsListCallback;

    private Set<String> challengeFriendsId = new HashSet<>();
    private FriendsData friendsData;

    public InviteFriendsFragment(){}

    public static InviteFriendsFragment newInstance(ChallengeActivity.FriendsListCallback mFriendsListCallback) {
        InviteFriendsFragment fragment = new InviteFriendsFragment();
        Bundle args = new Bundle();
        fragment.mFriendsListCallback = mFriendsListCallback;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_friends, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_friends_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 4);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FriendsAdapter(getActivity(), friendsData, mFriendsListCallback);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.horizontal_line, true, true));
        mRecyclerView.setAdapter(mAdapter);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFriendsList();
        WebSocketHelper.get(getActivity()).sendGetUserListEvent();
    }

    private void setListeners() {
        if(getActivity() instanceof AbstractBaseActivity) {
            Toolbar toolbar = ((AbstractBaseActivity) getActivity()).toolbar;
            toolbar.findViewById(R.id.add_friends_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getActivity() != null) {
                        startActivity(new Intent(getActivity(), AddFriendsActivity.class));
                    }
                }
            });
            toolbar.findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null && mAdapter != null && mAdapter.getSelectedFriends() != null && mAdapter.getSelectedFriends().size() > 0) {
                        mFriendsListCallback.onNextClick(mAdapter.getSelectedFriends());
                    }
                }
            });
        }
    }

    private void loadFriendsList() {
        showProgress();
        ApiManager.getInstance(getActivity()).getFriendsList(
                appPref.getUserId(),
                AbstractBaseActivity.getSelectedCourseId(),
                AbstractBaseActivity.getSelectedCourse() != null ? AbstractBaseActivity.getSelectedCourse().courseInstanceId : null,
                new ApiCallback<FriendsData>(getActivity()) {

                    @Override
                    public void success(FriendsData friendsData, Response response) {
                        super.success(friendsData, response);
                        if (getActivity() != null && friendsData != null) {
                            closeProgress();
                            InviteFriendsFragment.this.friendsData = friendsData;
                            showFriendsList();
                            fetchDisplayName();
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if(getActivity() != null) {
                            closeProgress();
                            ((AbstractBaseActivity) getActivity()).showToast("No friends available");
                        }
                    }
                });
    }

    private void showFriendsList() {
        updateFriendsListStatus();
        if(friendsData != null && friendsData.friendsList != null) {
//            mAdapter = new FriendsAdapter(getActivity(), friendsData, mFriendsListCallback);
            mAdapter.updateData(friendsData);
//            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.horizontal_line, true, true));
//            mRecyclerView.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
        }
    }

    private List<FriendsData.Friend> filter(String query) {
        query = query.toLowerCase();

        final List<FriendsData.Friend> filteredModelList = new ArrayList<>();
        if (friendsData != null && friendsData.friendsList != null && friendsData.friendsList.size() > 0) {
            for (FriendsData.Friend model : friendsData.friendsList) {
                final String text = model.displayName.toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }

    private void updateFriendsListStatus() {
        if (friendsData != null && friendsData.friendsList != null) {
            for (FriendsData.Friend friend : friendsData.friendsList) {
                friend.isOnline = challengeFriendsId.contains(friend.idStudent);
            }
        }
    }

    public void onEventMainThread(ChallengeUserList event) {
        if(getActivity() != null) {
            challengeFriendsId = event.users;
            showFriendsList();
            L.info("Websocket : " + Gson.get().toJson(event.users));
        }
    }

    private void fetchDisplayName() {
        if (friendsData != null && friendsData.friendsList != null) {
            for (FriendsData.Friend friend : friendsData.friendsList) {
                if (friend.idStudent.equals(LoginUserCache.getInstance().getLoginResponse().studentId)) {
                    LoginUserCache.getInstance().getLoginResponse().displayName = friend.displayName;
                }
            }
        }
    }

}
