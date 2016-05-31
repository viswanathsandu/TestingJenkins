package com.education.corsalite.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.education.corsalite.R;
import com.education.corsalite.activities.ChallengeActivity;
import com.education.corsalite.models.responsemodels.FriendsData;
import com.education.corsalite.services.ApiClientService;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sridharnalam on 12/21/15.
 */
public class FriendsAdapter extends AbstractRecycleViewAdapter {

    public ArrayList<FriendsData.Friend> selectedFriends;
    private LayoutInflater inflater;
    private Activity mActivity;
    ChallengeActivity.FriendsListCallback mFriendsListCallback;

    public FriendsAdapter(Activity activity, FriendsData friendsData, ChallengeActivity.FriendsListCallback friendsListCallback) {
        this(friendsData);
        this.mActivity = activity;
        this.inflater = activity.getLayoutInflater();
        this.mFriendsListCallback = friendsListCallback;
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
        ((FriendViewHolder) viewHolder).bindData(position, (FriendsData.Friend) getItem(position));
    }

    public void setFilter(ArrayList<FriendsData.Friend> filterList) {
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
        @Bind(R.id.status_view) View statusView;
        @Bind(R.id.tile_view) View tileView;
        View parent;

        public FriendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.parent = itemView;
        }

        public void bindData(final int position, final FriendsData.Friend clickedFriend) {
            tvName.setText(clickedFriend.displayName);
            tvEmail.setText(clickedFriend.emailID);
            statusView.setBackgroundColor(mActivity.getResources().getColor(clickedFriend.isOnline ? R.color.green : R.color.gray));
//            ivActionBtn.setVisibility(clickedFriend.isOnline ? View.VISIBLE : View.GONE);
//            if (selectedFriends.contains(clickedFriend)) {
//                ivActionBtn.setImageResource(android.R.drawable.ic_delete);
//            } else {
//                ivActionBtn.setImageResource(android.R.drawable.ic_input_add);
//            }
            if (!TextUtils.isEmpty(clickedFriend.photoUrl)) {
                Glide.with(mActivity).load(ApiClientService.getBaseUrl() + clickedFriend.photoUrl.replaceFirst("./", "")).into(ivProfilePic);
            }
            if(clickedFriend.isOnline) {
                parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedFriends.contains(clickedFriend)) {
                            tileView.setBackgroundColor(mActivity.getResources().getColor(R.color.challenge_tile_color));
                            ivActionBtn.setImageResource(android.R.drawable.ic_input_add);
                            selectedFriends.remove(clickedFriend);
                            if (mFriendsListCallback != null) {
                                mFriendsListCallback.onFriendRemoved(clickedFriend);
                            }
                        } else if (selectedFriends.size() < 4) {
                            tileView.setBackgroundColor(mActivity.getResources().getColor(R.color.green));
                            ivActionBtn.setImageResource(android.R.drawable.ic_delete);
                            selectedFriends.add(clickedFriend);
                            if (mFriendsListCallback != null) {
                                mFriendsListCallback.onFriendAdded(clickedFriend);
                            }
                        } else {
                            Toast.makeText(mActivity, "Only 4 members can be selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }

    public ArrayList<FriendsData.Friend> getSelectedFriends() {
        return selectedFriends;
    }
}
