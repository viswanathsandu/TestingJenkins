package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.ChallengeResultsAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.helpers.WebSocketHelper;
import com.education.corsalite.models.requestmodels.ChallengeStatusRequest;
import com.education.corsalite.models.responsemodels.ChallengeCompleteResponseModel;
import com.education.corsalite.models.responsemodels.ChallengeUser;
import com.education.corsalite.models.responsemodels.CommonResponseModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.socket.requests.ChallengeTestUpdateRequestEvent;
import com.education.corsalite.models.socket.response.ChallengeTestCompletedEvent;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.WebUrls;
import com.google.gson.Gson;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.client.Response;

/**
 * Created by Madhuri on 07-04-2016.
 */
public class ChallengeResultActivity extends AbstractBaseActivity {

    @Bind(R.id.refresh_btn) ImageButton refreshBtn;
    @Bind(R.id.refresh_txt) TextView refreshTxt;
    @Bind(R.id.status_image) ImageView statusImage;
    @Bind(R.id.results_recycelr_view) RecyclerView resultsList;
    @Bind(R.id.challengepart_refresh_layout) View refreshLayout;
    @Bind(R.id.challenge_result_layout) View resultsLayout;

    private String challengeTestId;
    private String testQuestionPaperId;
    private List<ChallengeUser> challengeUsers;
    private ChallengeUser mCurrentUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.challenge_result, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        loadScreen();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadScreen();
    }

    private void loadScreen() {
        challengeTestId = getIntent().getStringExtra("challenge_test_id");
        testQuestionPaperId = getIntent().getStringExtra("test_question_paper_id");
        completeChallengeTest();
        resultsLayout.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
    }

    private void completeChallengeTest() {
        ApiManager.getInstance(this).completeChallengeTest(challengeTestId, testQuestionPaperId, LoginUserCache.getInstance().getStudentId(),
                new ApiCallback<ChallengeCompleteResponseModel>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                    }

                    @Override
                    public void success(ChallengeCompleteResponseModel challengeCompleteResponseModel, Response response) {
                        super.success(challengeCompleteResponseModel, response);
                        if(challengeCompleteResponseModel.leaderBoardUsers != null) {
                            getChallengeResults();
                            updateChallengeStatus("Completed");
                        }
                    }
                });
    }

    private void sendChallengeTestCompleteEvent() {
        try {
            ChallengeTestCompletedEvent event = new ChallengeTestCompletedEvent(challengeTestId, mCurrentUser.displayName);
            WebSocketHelper.get(this).sendChallengeTestcompleteEvent(event);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void getChallengeResults() {
        ApiManager.getInstance(this).getChallengeResults(challengeTestId,
                new ApiCallback<List<ChallengeUser>>(this) {
                    @Override
                    public void success(List<ChallengeUser> challengeUsers, Response response) {
                        super.success(challengeUsers, response);
                        if(challengeUsers != null) {
                            ChallengeResultActivity.this.challengeUsers = challengeUsers;
                            showResults();
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                    }
                });
    }

    private void updateChallengeStatus(final String status) {
        ChallengeStatusRequest request = new ChallengeStatusRequest(LoginUserCache.getInstance().getStudentId(),
                challengeTestId, status);
        ApiManager.getInstance(this).postChallengeStatus(new Gson().toJson(request),
                new ApiCallback<CommonResponseModel>(this) {
                    @Override
                    public void success(CommonResponseModel commonResponseModel, Response response) {
                        super.success(commonResponseModel, response);
                        postStatusEvent(status);
                        sendChallengeTestCompleteEvent();
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        showToast(error.message);
                    }
                });
    }

    private void postStatusEvent(String status) {
        try {
            ChallengeTestUpdateRequestEvent event = new ChallengeTestUpdateRequestEvent();
            event.setChallengeTestParentId(challengeTestId);
            event.challengerName = mCurrentUser.displayName;
            event.challengerStatus = status;
            WebSocketHelper.get(this).sendChallengeUpdateEvent(event);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }


    private void showResults() {
        int pendingUsers = 0;
        for(ChallengeUser user : challengeUsers) {
            if(!user.status.equals("Completed")) {
                pendingUsers++;
            }
            if(user.idStudent.equalsIgnoreCase(LoginUserCache.getInstance().getStudentId())) {
                mCurrentUser = user;
            }
        }
        if(pendingUsers > 0) {
            resultsLayout.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
            refreshTxt.setText(String.format("%s more participants are still battling out", pendingUsers));
            return;
        }

        // show results
        showToast("showing Results");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ChallengeResultsAdapter adapter = new ChallengeResultsAdapter(challengeUsers, inflater);
        resultsList.setLayoutManager(new LinearLayoutManager(this));
        resultsList.setAdapter(adapter);
        if(mCurrentUser != null) {
            if(mCurrentUser.getChallengeStatus().equals("WON")) {
                statusImage.setImageDrawable(getResources().getDrawable(R.drawable.won_trophy));
            } else if(mCurrentUser.getChallengeStatus().equals("LOST")) {
                statusImage.setImageDrawable(getResources().getDrawable(R.drawable.lost));
            } else if(mCurrentUser.getChallengeStatus().equals("TIE")) {
                statusImage.setImageDrawable(getResources().getDrawable(R.drawable.ico_challenge_cup));
            }
        }
        resultsLayout.setVisibility(View.VISIBLE);
        refreshLayout.setVisibility(View.GONE);
    }

    public void onEventMainThread(ChallengeTestCompletedEvent event) {
        getChallengeResults();
    }

    @OnClick(R.id.refresh_btn)
    public void onRefresh() {
        getChallengeResults();
    }

    @OnClick(R.id.exam_summary_btn)
    public void onExamHistoryClicked() {
        if(mCurrentUser != null && !TextUtils.isEmpty(mCurrentUser.testAnswerPaperId)) {
            Intent intent = new Intent(this, WebviewActivity.class);
            L.info("URL : " + WebUrls.getExamResultsSummaryUrl() + mCurrentUser.testAnswerPaperId);
            intent.putExtra(LoginActivity.URL, WebUrls.getExamResultsSummaryUrl() + mCurrentUser.testAnswerPaperId);
            intent.putExtra(LoginActivity.TITLE, getString(R.string.results));
            startActivity(intent);
        }
    }

    @OnClick(R.id.challenge_history_btn)
    public void onChallengeHistoryClicked() {
        showToast("Under Development");
    }

    @OnClick(R.id.challenge_again_btn)
    public void onChallengeAgainClicked() {
        startActivity(new Intent(this, ChallengeActivity.class));
    }
}
