package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
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
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsListFragment extends BaseFragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String ARG_CALLBACK = "ARG_CALLBACK";
    private SearchView mFriendSearchView;
    private RecyclerView mRecyclerView;
    private FriendsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTextViewCancel;
    private TextView mTextViewNext;
    private ChallengeActivity.FriendsListCallback mFriendsListCallback;

    private Set<String> challengeFriendsId = new HashSet<>();
    private FriendsData friendsData;

    public FriendsListFragment(){}

    public static FriendsListFragment newInstance(ChallengeActivity.FriendsListCallback mFriendsListCallback) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        fragment.mFriendsListCallback = mFriendsListCallback;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        mFriendSearchView = (SearchView) view.findViewById(R.id.sv_friend_search);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_friends_list);
        mTextViewCancel = (TextView) view.findViewById(R.id.tv_friendlist_cancel);
        mTextViewNext = (TextView) view.findViewById(R.id.tv_friendlist_next);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        loadFriendsList();
        mFriendSearchView.setIconifiedByDefault(false);
        mFriendSearchView.setOnQueryTextListener(this);
        mFriendSearchView.setOnCloseListener(this);
        mTextViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getSelectedFriends() != null && mAdapter.getSelectedFriends().size() > 0) {
                    mFriendsListCallback.onNextClick(mAdapter.getSelectedFriends());
                }
            }
        });
        mTextViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        WebSocketHelper.get(getActivity()).sendGetUserListEvent();
    }

    private void loadFriendsList() {
        ApiManager.getInstance(getActivity()).getFriendsList(
                LoginUserCache.getInstance().loginResponse.userId,
                AbstractBaseActivity.selectedCourse.courseId.toString(),
                new ApiCallback<FriendsData>(getActivity()) {

                    @Override
                    public void success(FriendsData friendsData, Response response) {
                        super.success(friendsData, response);
                        if (friendsData != null) {
                            FriendsListFragment.this.friendsData = friendsData;
                            showFriendsList();
                            fetchDisplayName();
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        ((AbstractBaseActivity) getActivity()).showToast("No friends available");
                    }
                });
    }

    private void showFriendsList() {
        updateFriendsListStatus();
        if(friendsData != null && friendsData.friendsList != null) {
            mAdapter = new FriendsAdapter(getActivity(), friendsData, mFriendsListCallback);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.horizontal_line, true, true));
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onClose() {
        if (friendsData != null && friendsData.friendsList != null && friendsData.friendsList.size() > 0)
            mAdapter.setFilter(friendsData.friendsList);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<FriendsData.Friend> filteredModelList = filter(newText);
        mAdapter.setFilter((ArrayList<FriendsData.Friend>) filteredModelList);
        return true;
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
        showToast("User list event");
        challengeFriendsId = event.users;
        showFriendsList();
        L.info("Websocket : " + new Gson().toJson(event.users));
    }

    private void fetchDisplayName() {
        if (friendsData != null && friendsData.friendsList != null) {
            for (FriendsData.Friend friend : friendsData.friendsList) {
                if (friend.idStudent.equals(LoginUserCache.getInstance().getLongResponse().studentId)) {
                    LoginUserCache.getInstance().loginResponse.displayName = friend.displayName;
                }
            }
        }
    }

}
