package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.adapters.DividerItemDecoration;
import com.education.corsalite.adapters.FriendsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsListFragment extends BaseFragment {

    private SearchView mFriendSearchView;
    private RecyclerView mRecyclerView;
    private FriendsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public FriendsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        mFriendSearchView = (SearchView) view.findViewById(R.id.sv_friend_search);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_friends_list);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FriendsAdapter(generateData());
        mRecyclerView.setAdapter(mAdapter);
        //TODO: Sridhar, Need to fix horizontal devider
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.horizontal_line, true, true));
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
