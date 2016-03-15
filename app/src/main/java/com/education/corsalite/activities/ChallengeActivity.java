package com.education.corsalite.activities;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.education.corsalite.R;
import com.education.corsalite.fragments.FriendsListFragment;
import com.education.corsalite.fragments.TestSetupFragment;
import com.education.corsalite.models.responsemodels.FriendsData;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChallengeActivity extends AbstractBaseActivity {

    @Bind(R.id.left_player) ImageView leftPlayerImg;
    @Bind(R.id.right_player1) ImageView rightPlayer1Img;
    @Bind(R.id.right_player2) ImageView rightPlayer2Img;
    @Bind(R.id.right_player3) ImageView rightPlayer3Img;
    @Bind(R.id.right_player4) ImageView rightPlayer4Img;


    private FriendsListCallback mFriendsListCallback;
    private TestSetupCallback mTestSetupCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        ButterKnife.bind(this);
        initListeners();
        loadPlayers();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, FriendsListFragment.newInstance(mFriendsListCallback), "FriendsList").addToBackStack(null).commit();
    }

    private void initListeners() {
        mFriendsListCallback = new FriendsListCallback() {
            @Override
            public void onNextClick(ArrayList<FriendsData.Friends> selectedFriends) {
                showToast(selectedFriends.size() + "");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, TestSetupFragment.newInstance(mTestSetupCallback, selectedFriends)).addToBackStack(null).commit();
            }
        };

        mTestSetupCallback = new TestSetupCallback() {
            @Override
            public void popUpFriendsListFragment() {
                getSupportFragmentManager().popBackStackImmediate();
            }
        };
    }

    public interface FriendsListCallback extends Serializable {
        void onNextClick(ArrayList<FriendsData.Friends> selectedFriends);
    }

    public interface TestSetupCallback extends Serializable {
        void popUpFriendsListFragment();
    }

    private void loadPlayers() {
        loadGif(leftPlayerImg, R.raw.character_anim_left);
        loadGif(rightPlayer1Img, R.raw.character_anim_right);
        loadGif(rightPlayer2Img, R.raw.character_anim_right);
        loadGif(rightPlayer3Img, R.raw.character_anim_right);
        loadGif(rightPlayer4Img, R.raw.character_anim_right);
    }

    private void loadGif(ImageView imageView, int rawGifId) {
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(rawGifId).into(imageViewTarget);
    }
}
