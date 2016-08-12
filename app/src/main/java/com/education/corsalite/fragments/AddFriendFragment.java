package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.adapters.AddFriendsAdapter;
import com.education.corsalite.adapters.DividerItemDecoration;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.helpers.WebSocketHelper;
import com.education.corsalite.models.responsemodels.CommonResponseModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.FriendsData;
import com.education.corsalite.models.socket.response.ChallengeUserList;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.utils.L;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddFriendFragment extends BaseFragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, AddFriendsAdapter.OnFriendClickListener {

    private static final String ARG_CALLBACK = "ARG_CALLBACK";
    private SearchView mFriendSearchView;
    private RecyclerView mRecyclerView;
    private AddFriendsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String searchKey;
    private Set<String> challengeFriendsId = new HashSet<>();
    private List<FriendsData.Friend> friends;

    public AddFriendFragment(){}

    public static AddFriendFragment newInstance() {
        AddFriendFragment fragment = new AddFriendFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        mFriendSearchView = (SearchView) view.findViewById(R.id.sv_friend_search);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_friends_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFriendSearchView.setIconifiedByDefault(false);
        mFriendSearchView.setOnQueryTextListener(this);
        mFriendSearchView.setOnCloseListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        WebSocketHelper.get(getActivity()).sendGetUserListEvent();
    }

    private void loadFriendsList(String searchKey) {
        if(getActivity() != null) {
            if (TextUtils.isEmpty(searchKey)) {
                showToast("Plean enter the search text");
                return;
            }
            this.searchKey = searchKey;
            ApiManager.getInstance(getActivity()).searchFriends(
                    appPref.getUserId(),
                    AbstractBaseActivity.getSelectedCourseId(),
                    searchKey,
                    new ApiCallback<List<FriendsData.Friend>>(getActivity()) {

                        @Override
                        public void success(List<FriendsData.Friend> friends, Response response) {
                            super.success(friends, response);
                            if (getActivity() != null && friends != null) {
                                AddFriendFragment.this.friends = friends;
                                showFriendsList();
                                fetchDisplayName();
                            }
                        }

                        @Override
                        public void failure(CorsaliteError error) {
                            super.failure(error);
                            if(getActivity() != null) {
                                ((AbstractBaseActivity) getActivity()).showToast("No results found");
                            }
                        }
                    });
        }
    }

    private void showFriendsList() {
        updateFriendsListStatus();
        if(getActivity() != null && friends != null && friends != null) {
            mAdapter = new AddFriendsAdapter(getActivity(), friends, this);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.horizontal_line, true, true));
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onClose() {
        if (friends != null && friends != null && friends.size() > 0)
            mAdapter.setFilter(friends);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String searchKey) {
        loadFriendsList(searchKey);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    private List<FriendsData.Friend> filter(String query) {
        query = query.toLowerCase();

        final List<FriendsData.Friend> filteredModelList = new ArrayList<>();
        if (friends != null && friends.size() > 0) {
            for (FriendsData.Friend model : friends) {
                final String text = model.displayName.toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }

    private void updateFriendsListStatus() {
        if (friends != null ) {
            for (FriendsData.Friend friend : friends) {
                friend.isOnline = challengeFriendsId.contains(friend.idStudent);
            }
        }
    }

    public void onEventMainThread(ChallengeUserList event) {
        challengeFriendsId = event.users;
        showFriendsList();
        L.info("Websocket : " + Gson.get().toJson(event.users));
    }

    private void fetchDisplayName() {
        if (friends != null ) {
            for (FriendsData.Friend friend : friends) {
                if (friend.idStudent.equals(LoginUserCache.getInstance().getLongResponse().studentId)) {
                    LoginUserCache.getInstance().getLongResponse().displayName = friend.displayName;
                }
            }
        }
    }

    @Override
    public void addFriend(FriendsData.Friend friend) {
        showProgress();
        ApiManager.getInstance(getActivity()).addFriend(appPref.getUserId(), friend.idUser,
                new ApiCallback<CommonResponseModel>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                if(getActivity() != null) {
                    closeProgress();
                    showToast("Failed adding friend. Please try again");
                }
            }

            @Override
            public void success(CommonResponseModel commonResponseModel, Response response) {
                super.success(commonResponseModel, response);
                closeProgress();
                if(getActivity() != null && commonResponseModel.status.equalsIgnoreCase("SUCCESS")) {
                    loadFriendsList(searchKey);
                    showToast("Added friend successfully");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                if(getActivity() != null) {
                    closeProgress();
                }
            }
        });
    }

    @Override
    public void removeFriend(FriendsData.Friend friend) {
        showProgress();
        ApiManager.getInstance(getActivity()).unFriend(appPref.getUserId(), friend.idUser,
                new ApiCallback<CommonResponseModel>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                if(getActivity() != null) {
                    closeProgress();
                    showToast("Failed removeing friend. Please try again");
                }
            }

            @Override
            public void success(CommonResponseModel commonResponseModel, Response response) {
                super.success(commonResponseModel, response);
                closeProgress();
                if(getActivity() != null && commonResponseModel.status.equalsIgnoreCase("SUCCESS")) {
                    loadFriendsList(searchKey);
                    showToast("Removed friend successfully");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                if(getActivity() != null) {
                    closeProgress();
                }
            }
        });
    }
}
