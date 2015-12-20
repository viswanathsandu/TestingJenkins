package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.fragments.FriendsListFragment;

import java.util.List;

/**
 * Created by sridharnalam on 12/21/15.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {
    private final List<FriendsListFragment.FriendData> mFriendsList;

    public FriendsAdapter(List<FriendsListFragment.FriendData> friendsList) {
        mFriendsList = friendsList;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_friend_item, viewGroup, false);
        FriendViewHolder fvh = new FriendViewHolder(v);
        return fvh;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        // TODO:Sridhar. Need to set profile pic to holder.
        holder.tvEmail.setText(mFriendsList.get(position).getEmail());
        holder.tvName.setText(mFriendsList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        ImageView ivActionBtn;
        TextView tvName;
        TextView tvEmail;

        public FriendViewHolder(View itemView) {
            super(itemView);
            ivProfilePic = (ImageView) itemView.findViewById(R.id.iv_friend_pic);
            ivActionBtn = (ImageView) itemView.findViewById(R.id.iv_friend_btn);
            tvName = (TextView) itemView.findViewById(R.id.tv_friend_name);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_friend_email);
        }
    }
}
