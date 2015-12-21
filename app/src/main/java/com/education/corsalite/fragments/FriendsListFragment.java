package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.ChallengeActivity;
import com.education.corsalite.adapters.DividerItemDecoration;
import com.education.corsalite.adapters.FriendsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsListFragment extends BaseFragment  implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String ARG_CALLBACK = "ARG_CALLBACK";
    private SearchView mFriendSearchView;
    private RecyclerView mRecyclerView;
    private FriendsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTextViewCancel;
    private TextView mTextViewNext;
    ChallengeActivity.FriendsListCallback mFriendsListCallback;

    public FriendsListFragment(){}


    public static FriendsListFragment newInstance(ChallengeActivity.FriendsListCallback mFriendsListCallback) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CALLBACK, mFriendsListCallback);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFriendsListCallback = (ChallengeActivity.FriendsListCallback) getArguments().getSerializable(ARG_CALLBACK);
        }
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

        mAdapter = new FriendsAdapter(generateData());
        mRecyclerView.setAdapter(mAdapter);
        //TODO: Sridhar, Need to fix horizontal devider
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.horizontal_line, true, true));

        mFriendSearchView.setIconifiedByDefault(false);

        mFriendSearchView.setOnQueryTextListener(this);
        mFriendSearchView.setOnCloseListener(this);

        mTextViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendsListCallback.onNextClick(new ArrayList<FriendData>());
            }
        });
        return view;
    }

    public List<FriendData> generateData(){
        List<FriendData> list =new ArrayList<FriendData>();
        list.add(new FriendData("", "Sridhar", "sridhar.nalam@gmail.com"));
        list.add(new FriendData("", "Anu", "anu.radha@gmail.com"));
        list.add(new FriendData("", "Balu", "bala.gopal@gmail.com"));
        list.add(new FriendData("", "Madhu", "madhu.babu@gmail.com"));
        list.add(new FriendData("", "Anil", "anil.rayudu@gmail.com"));
        list.add(new FriendData("", "Prasanth", "prasanth.ndu@gmail.com"));
        list.add(new FriendData("", "Bunny", "bunny.happy@gmail.com"));
        list.add(new FriendData("", "Srinu", "srinu.vasu@gmail.com"));
        list.add(new FriendData("", "Prasad", "prasad.challa@gmail.com"));
        list.add(new FriendData("", "Jaya", "jaya.sree@gmail.com"));
        list.add(new FriendData("", "Jaswanth", "jaswanth.joy@gmail.com"));
        list.add(new FriendData("", "Naveen", "naveen.novel@gmail.com"));
        return list;
    }

    @Override
    public boolean onClose() {
        mAdapter.setFilter(generateData());
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<FriendData> filteredModelList = filter(generateData(), newText);
        mAdapter.setFilter(filteredModelList);
        return true;
    }

    private List<FriendData> filter(List<FriendData> models, String query) {
        query = query.toLowerCase();

        final List<FriendData> filteredModelList = new ArrayList<>();
        for (FriendData model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public class FriendData {
        private String pic;
        private String name;
        private String email;

        public FriendData(String pic, String name, String email) {
            this.pic = pic;
            this.name = name;
            this.email = email;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
