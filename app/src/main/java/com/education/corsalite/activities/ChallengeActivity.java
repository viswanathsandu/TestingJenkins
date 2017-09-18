package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.event.SocketConnectionStatusEvent;
import com.education.corsalite.fragments.ChallengeTestRequestDialogFragment;
import com.education.corsalite.fragments.InviteFriendsFragment;
import com.education.corsalite.fragments.TestSetupFragment;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.helpers.WebSocketHelper;
import com.education.corsalite.models.responsemodels.CreateChallengeResponseModel;
import com.education.corsalite.models.responsemodels.FriendsData;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.models.socket.requests.NewChallengeTestRequestEvent;
import com.education.corsalite.models.socket.response.ChallengeTestRequestEvent;
import com.education.corsalite.models.socket.response.ChallengeTestStartEvent;
import com.education.corsalite.models.socket.response.ChallengeTestUpdateEvent;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.client.Response;

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
    public String mChallengeTestId = "";
    public String mTestQuestionPaperId = "";
    public String challengeTestTimeDuration = "";
    private String screenType = "NEW_CHALLENGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_challenge, null);
        frameLayout.addView(myView);
        setToolbarForChallengeTest(true);

        ButterKnife.bind(this);
        initListeners();
        fetchDisplayName();
        loadCharecters();
        loadPlayers();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            screenType = bundle.getString("type", "REQUEST");
        }
        if(!TextUtils.isEmpty(screenType)) {
            if(screenType.equalsIgnoreCase("NEW_CHALLENGE")) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, InviteFriendsFragment.newInstance(mFriendsListCallback), "FriendsList").commit();
            } else if(screenType.equalsIgnoreCase("REQUEST")) {
                String challengeTestRequestJson = bundle.getString("challenge_test_request_json");
                ChallengeTestRequestEvent event = Gson.get().fromJson(challengeTestRequestJson, ChallengeTestRequestEvent.class);
                showChallengeTestRequestFragment(event);
            } else if(screenType.equalsIgnoreCase("UPDATE")) {
                String challengeTestRequestJson = bundle.getString("challenge_test_update_json");
                ChallengeTestUpdateEvent event = Gson.get().fromJson(challengeTestRequestJson, ChallengeTestUpdateEvent.class);
                showChallengeTestRequestFragment(getRequestEvent(event));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!WebSocketHelper.get(this).isConnected()) {
            showSocketDisconnectionAlert(false);
        }
    }

    public void onEventMainThread(SocketConnectionStatusEvent event) {
        showSocketDisconnectionAlert(event.isConnected);
    }

    private void fetchDisplayName() {
        try {
            ApiManager.getInstance(this).getUserProfile(LoginUserCache.getInstance().getStudentId(),
                    LoginUserCache.getInstance().getEntityId(),
                    new ApiCallback<UserProfileResponse>(this) {
                        @Override
                        public void success(UserProfileResponse userProfileResponse, Response response) {
                            if(!isDestroyed()) {
                                if (LoginUserCache.getInstance().getLoginResponse() != null) {
                                    LoginUserCache.getInstance().getLoginResponse().displayName = userProfileResponse.basicProfile.displayName;
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void initListeners() {
        mTestSetupCallback = new TestSetupCallback() {
            @Override
            public void popUpFriendsListFragment() {
                getSupportFragmentManager().popBackStackImmediate();
            }
        };
    }

    public void showChallengeTestRequestFragment(ChallengeTestRequestEvent event) {
        ChallengeTestRequestDialogFragment challengeRequestDialog = ChallengeTestRequestDialogFragment.newInstance(event);
        if (challengeRequestDialog != null) {
            challengeRequestDialog.setCancelable(false);
            challengeRequestDialog.show(getSupportFragmentManager(), ChallengeTestRequestDialogFragment.class.getSimpleName());
        }
    }

    private ChallengeTestRequestEvent getRequestEvent(ChallengeTestUpdateEvent event) {
        ChallengeTestRequestEvent requestEvent = new ChallengeTestRequestEvent();
        requestEvent.challengeTestParentId = event.challengeTestParentId;
        requestEvent.challengerName = event.challengerName;
        return requestEvent;
    }

    private FriendsListCallback mFriendsListCallback = new FriendsListCallback() {
        @Override
        public void onNextClick(ArrayList<FriendsData.Friend> selectedFriends) {
            if(!isDestroyed()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, TestSetupFragment.newInstance(mTestSetupCallback)).addToBackStack(null).commit();
            }
        }
        @Override
        public void onFriendAdded(FriendsData.Friend friend) {
            if(!isDestroyed() && friend != null && !selectedFriends.contains(friend)) {
                selectedFriends.add(friend);
                loadPlayers();
            }
        }

        @Override
        public void onFriendRemoved(FriendsData.Friend friend) {
            if(!isDestroyed() && friend != null) {
                selectedFriends.remove(friend);
                loadPlayers();
            }
        }
    };

    public interface FriendsListCallback {
        void onNextClick(ArrayList<FriendsData.Friend> selectedFriends);
        void onFriendAdded(FriendsData.Friend friend);
        void onFriendRemoved(FriendsData.Friend friend);
    }

    public interface TestSetupCallback {
        void popUpFriendsListFragment();
    }

    private void loadCharecters() {
        loadGif(leftPlayerImg, R.raw.character_anim_left);
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
                loadGif(rightPlayer1Img, selectedFriends.get(i).isRobot()
                        ? R.raw.robot1 : R.raw.character_anim_right);
            } else if(i == 1) {
                player2Layout.setVisibility(View.VISIBLE);
                player2_txt.setText(selectedFriends.get(i).displayName);
                loadGif(rightPlayer2Img, selectedFriends.get(i).isRobot()
                        ? R.raw.robot1 : R.raw.character_anim_right);
            } else if(i == 2) {
                player3Layout.setVisibility(View.VISIBLE);
                player3_txt.setText(selectedFriends.get(i).displayName);
                loadGif(rightPlayer3Img, selectedFriends.get(i).isRobot()
                        ? R.raw.robot1 : R.raw.character_anim_right);
            } else if(i == 3) {
                player4Layout.setVisibility(View.VISIBLE);
                player4_txt.setText(selectedFriends.get(i).displayName);
                loadGif(rightPlayer4Img, selectedFriends.get(i).isRobot()
                        ? R.raw.robot1 : R.raw.character_anim_right);
            }
        }
    }

    private void loadGif(ImageView imageView, int rawGifId) {
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(rawGifId).into(imageViewTarget);
    }

    public void onEventMainThread(CreateChallengeResponseModel model) {
        mChallengeTestId = model.challengeTestId;
        mTestQuestionPaperId = model.testQuestionPaperId;
        postChallengeTestStartEvent();
        showChallengeRequestDialog(mChallengeTestId);
    }

    private void showChallengeRequestDialog(String chalengeTestParentId) {
        ChallengeTestRequestEvent event = new ChallengeTestRequestEvent();
        event.challengerName = LoginUserCache.getInstance().getDisplayName();
        event.challengeTestParentId = chalengeTestParentId;
        EventBus.getDefault().post(event);
    }

    private void postChallengeTestStartEvent() {
        NewChallengeTestRequestEvent event = new NewChallengeTestRequestEvent();
        event.challengerName = LoginUserCache.getInstance().getDisplayName();
        event.challengeTestParentId = mChallengeTestId;
        WebSocketHelper.get(this).sendChallengeTestEvent(event);
    }

    public void onEventMainThread(ChallengeTestStartEvent event) {
        mTestQuestionPaperId = event.testQuestionPaperId;
        startChallengeTest(mTestQuestionPaperId, event.challengeTestParentId);
    }

    public void startChallengeTest(String testQuestionPaperId, String challngeTestId) {
        Intent intent = new Intent(this, ExamEngineActivity.class);
        intent.putExtra(Constants.TEST_TITLE, "Challenge Test");
        intent.putExtra("test_question_paper_id", testQuestionPaperId);
        intent.putExtra("challenge_test_id", challngeTestId);
        if(!TextUtils.isEmpty(challengeTestTimeDuration)) {
            intent.putExtra("challenge_test_time_duration", challengeTestTimeDuration);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onEventMainThread(ChallengeTestRequestEvent event) {
        showChallengeTestRequestFragment(event);
    }
}
