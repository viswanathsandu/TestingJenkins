package com.education.corsalite.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.FriendsData;
import com.education.corsalite.services.ApiClientService;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sridharnalam on 12/21/15.
 */
public class FriendsAdapter extends AbstractRecycleViewAdapter {

    public ArrayList<FriendsData.Friends> selectedFriends;
    private LayoutInflater inflater;
    private Activity mActivity;

    public FriendsAdapter(Activity activity, FriendsData friendsData, LayoutInflater inflater) {
        this(friendsData);
        this.mActivity = activity;
        this.inflater = inflater;
        selectedFriends = new ArrayList<>();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.row_friend_item, viewGroup, false);
        FriendViewHolder fvh = new FriendViewHolder(v);
        return fvh;
    }

    private FriendsAdapter(FriendsData friendsData) {
        addAll(friendsData.friendsList);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        ((FriendViewHolder) viewHolder).bindData(position, (FriendsData.Friends) getItem(position));
    }

    public void setFilter(ArrayList<FriendsData.Friends> filterList) {
        removeAll();
        addAll(filterList);
        notifyDataSetChanged();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_friend_pic)
        ImageView ivProfilePic;
        @Bind(R.id.iv_friend_btn)
        ImageView ivActionBtn;
        @Bind(R.id.tv_friend_name)
        TextView tvName;
        @Bind(R.id.tv_friend_email)
        TextView tvEmail;
        View parent;

        public FriendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.parent = itemView;
        }

        public void bindData(final int position, final FriendsData.Friends clickedFriend) {
            tvName.setText(clickedFriend.displayName);
            tvEmail.setText(clickedFriend.emailID);
            if (selectedFriends.contains(clickedFriend)) {
                ivActionBtn.setImageResource(android.R.drawable.ic_delete);
            } else {
                ivActionBtn.setImageResource(android.R.drawable.ic_input_add);
            }
            if(!TextUtils.isEmpty(clickedFriend.photoUrl)) {
                Glide.with(mActivity).load(ApiClientService.getBaseUrl() + clickedFriend.photoUrl.replaceFirst("./", "")).into(ivProfilePic);
            }
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                ivActionBtn.setImageResource(android.R.drawable.ic_delete);
                if (selectedFriends != null && selectedFriends.size() > 0) {
                    if (selectedFriends.contains(clickedFriend)) {
                        ivActionBtn.setImageResource(android.R.drawable.ic_input_add);
                        selectedFriends.remove(clickedFriend);
                    } else {
                        selectedFriends.add(clickedFriend);
                    }
                } else {
                    selectedFriends = new ArrayList<FriendsData.Friends>();
                    selectedFriends.add(clickedFriend);
                }
                }
            });
        }

    }

    public ArrayList<FriendsData.Friends> getSelectedFriends(){
        return selectedFriends;
    }
}
