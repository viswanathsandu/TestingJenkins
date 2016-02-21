package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.models.responsemodels.FriendsData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sridharnalam on 12/21/15.
 */
public class FriendsAdapter extends AbstractRecycleViewAdapter {

    public ArrayList<FriendsData.Friends> selectedFriends;
    LayoutInflater inflater;

    public FriendsAdapter(FriendsData friendsData, LayoutInflater inflater) {
        this(friendsData);
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

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at
     * the given position.
     * <p/>
     * Note that unlike {@link android.widget.ListView}, RecyclerView will not call this
     * method again if the position of the item changes in the data set unless the item itself
     * is invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside this
     * method and should not keep a copy of it. If you need the position of an item later on
     * (e.g. in a click listener), use {@link RecyclerView.ViewHolder#getAdapterPosition()} which will have
     * the updated adapter position.
     *
     * @param viewHolder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        // TODO:Sridhar. Need to set profile pic to holder.
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
