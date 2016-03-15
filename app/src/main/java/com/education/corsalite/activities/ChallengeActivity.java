package com.education.corsalite.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.education.corsalite.R;
import com.education.corsalite.fragments.FriendsListFragment;
import com.education.corsalite.fragments.TestSetupFragment;
import com.education.corsalite.models.responsemodels.FriendsData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChallengeActivity extends AbstractBaseActivity {

    @Bind(R.id.player1_layout) LinearLayout player1Layout;
    @Bind(R.id.player2_layout) LinearLayout player2Layout;
    @Bind(R.id.player3_layout) LinearLayout player3Layout;
    @Bind(R.id.player4_layout) LinearLayout player4Layout;
    @Bind(R.id.left_player) ImageView leftPlayerImg;
    @Bind(R.id.right_player1) ImageView rightPlayer1Img;
    @Bind(R.id.right_player2) ImageView rightPlayer2Img;
    @Bind(R.id.right_player3) ImageView rightPlayer3Img;
    @Bind(R.id.right_player4) ImageView rightPlayer4Img;
    @Bind(R.id.player1_txt) TextView player1_txt;
    @Bind(R.id.player2_txt) TextView player2_txt;
    @Bind(R.id.player3_txt) TextView player3_txt;
    @Bind(R.id.player4_txt) TextView player4_txt;

    private TestSetupCallback mTestSetupCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        ButterKnife.bind(this);
        initListeners();
        loadCharecters();
        loadPlayers();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, FriendsListFragment.newInstance(mFriendsListCallback), "FriendsList").commit();
    }

    private void initListeners() {
        mTestSetupCallback = new TestSetupCallback() {
            @Override
            public void popUpFriendsListFragment() {
                getSupportFragmentManager().popBackStackImmediate();
            }
        };
    }

    private FriendsListCallback mFriendsListCallback = new FriendsListCallback() {
        @Override
        public void onNextClick(ArrayList<FriendsData.Friend> selectedFriends) {
            showToast(selectedFriends.size() + "");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, TestSetupFragment.newInstance(mTestSetupCallback, selectedFriends)).addToBackStack(null).commit();
        }
        @Override
        public void onFriendAdded(FriendsData.Friend friend) {
            if(friend != null) {
                selectedFriends.add(friend);
                loadPlayers();
            }
        }

        @Override
        public void onFriendRemoved(FriendsData.Friend friend) {
            if(friend != null) {
                selectedFriends.remove(friend);
                loadPlayers();
            }
        }
    };

    public interface FriendsListCallback extends Serializable {
        void onNextClick(ArrayList<FriendsData.Friend> selectedFriends);
        void onFriendAdded(FriendsData.Friend friend);
        void onFriendRemoved(FriendsData.Friend friend);
    }

    public interface TestSetupCallback extends Serializable {
        void popUpFriendsListFragment();
    }

    private void loadCharecters() {
        loadGif(leftPlayerImg, R.raw.character_anim_left);
        loadGif(rightPlayer1Img, R.raw.character_anim_right);
        loadGif(rightPlayer2Img, R.raw.character_anim_right);
        loadGif(rightPlayer3Img, R.raw.character_anim_right);
        loadGif(rightPlayer4Img, R.raw.character_anim_right);
    }

    private void loadPlayers() {
        player1Layout.setVisibility(View.GONE);
        player2Layout.setVisibility(View.GONE);
        player3Layout.setVisibility(View.GONE);
        player4Layout.setVisibility(View.GONE);
        for(int i=0; i<selectedFriends.size(); i++) {
            if(i == 0) {
                player1Layout.setVisibility(View.VISIBLE);
                player1_txt.setText(selectedFriends.get(i).displayName);
            } else if(i == 1) {
                player2Layout.setVisibility(View.VISIBLE);
                player2_txt.setText(selectedFriends.get(i).displayName);
            } else if(i == 2) {
                player3Layout.setVisibility(View.VISIBLE);
                player3_txt.setText(selectedFriends.get(i).displayName);
            } else if(i == 3) {
                player4Layout.setVisibility(View.VISIBLE);
                player4_txt.setText(selectedFriends.get(i).displayName);
            }
        }
    }

    private void loadGif(ImageView imageView, int rawGifId) {
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(rawGifId).into(imageViewTarget);
    }

    private List<FriendsData.Friend> selectedFriends = new ArrayList<>();
}
