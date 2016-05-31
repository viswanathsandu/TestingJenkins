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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sridharnalam on 12/21/15.
 */
public class AddFriendsAdapter extends AbstractRecycleViewAdapter {

    private LayoutInflater inflater;
    private Activity mActivity;
    private OnFriendClickListener onFriendClickListener;


    public AddFriendsAdapter(Activity activity, List<FriendsData.Friend> friends, OnFriendClickListener onFriendClickListener) {
        this(friends);
        this.mActivity = activity;
        this.inflater = activity.getLayoutInflater();
        this.onFriendClickListener = onFriendClickListener;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.row_friend_item, viewGroup, false);
        FriendViewHolder fvh = new FriendViewHolder(v);
        return fvh;
    }

    private AddFriendsAdapter(List<FriendsData.Friend> friends) {
        removeAll();
        addAll(friends);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        ((FriendViewHolder) viewHolder).bindData(position, (FriendsData.Friend) getItem(position));
    }

    public void setFilter(List<FriendsData.Friend> filterList) {
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
        @Bind(R.id.status_view)
        View statusView;
        @Bind(R.id.tile_view)
        View tileView;
        View parent;

        public FriendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.parent = itemView;
        }

        public void bindData(final int position, final FriendsData.Friend clickedFriend) {
            tvName.setText(clickedFriend.displayName);
            tvEmail.setText(clickedFriend.emailID);
            ivActionBtn.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(clickedFriend.friendRequest)) {
                if (clickedFriend.friendRequest.equalsIgnoreCase("AddFriend")) {
                    ivActionBtn.setImageResource(android.R.drawable.ic_input_add);
                    ivActionBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(onFriendClickListener != null) {
                                onFriendClickListener.addFriend((FriendsData.Friend)getItem(position));
                            }
                        }
                    });
                } else if (clickedFriend.friendRequest.equalsIgnoreCase("UnFriend")) {
                    ivActionBtn.setImageResource(android.R.drawable.ic_delete);
                    ivActionBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(onFriendClickListener != null) {
                                onFriendClickListener.removeFriend((FriendsData.Friend)getItem(position));
                            }
                        }
                    });
                }
            }
            if (!TextUtils.isEmpty(clickedFriend.photoUrl)) {
                Glide.with(mActivity).load(ApiClientService.getBaseUrl() + clickedFriend.photoUrl.replaceFirst("./", "")).into(ivProfilePic);
            }
        }

    }

    public interface OnFriendClickListener {
        void addFriend(FriendsData.Friend friend);
        void removeFriend(FriendsData.Friend friend);
    }
}
