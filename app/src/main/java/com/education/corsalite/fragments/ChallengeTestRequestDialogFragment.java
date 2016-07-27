package com.education.corsalite.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.ChallengeActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.helpers.WebSocketHelper;
import com.education.corsalite.models.requestmodels.ChallengeStatusRequest;
import com.education.corsalite.models.requestmodels.ChallengestartRequest;
import com.education.corsalite.models.responsemodels.ChallengeStartResponseModel;
import com.education.corsalite.models.responsemodels.ChallengeUser;
import com.education.corsalite.models.responsemodels.ChallengeUserListResponse;
import com.education.corsalite.models.responsemodels.CommonResponseModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.socket.requests.ChallengeTestStartRequestEvent;
import com.education.corsalite.models.socket.requests.ChallengeTestUpdateRequestEvent;
import com.education.corsalite.models.socket.response.ChallengeTestRequestEvent;
import com.education.corsalite.models.socket.response.ChallengeTestStartEvent;
import com.education.corsalite.models.socket.response.ChallengeTestUpdateEvent;
import com.education.corsalite.utils.Gson;
import com.education.corsalite.utils.L;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.client.Response;

public class ChallengeTestRequestDialogFragment extends BaseDialogFragment {

    @Bind(R.id.start_btn) ImageButton startBtn;
    @Bind(R.id.accept_btn) ImageButton acceptBtn;
    @Bind(R.id.reject_btn) ImageButton rejectBtn;
    @Bind(R.id.course_txt) TextView courseTxt;
    @Bind(R.id.subject_txt) TextView subjectTxt;
    @Bind(R.id.chapter_txt) TextView chapterTxt;
    @Bind(R.id.questions_txt) TextView questionsTxt;
    @Bind(R.id.time_txt) TextView timeTxt;
    @Bind(R.id.virtual_currency_txt) TextView virtualCurrencyTxt;
    @Bind(R.id.status_txt) TextView statusTxt;
    @Bind(R.id.accepted_count_txt) TextView acceptedTxt;
    @Bind(R.id.declined_count_txt) TextView declinedTxt;
    @Bind(R.id.initiated_count_txt) TextView initiatedTxt;

    private List<ChallengeUser> mChallengeUsers;
    private ChallengeTestRequestEvent mChallengeTestRequestEvent;
    private String displayName = "";
    private String examId;
    private String mTestQuestionPaperId = "";
    private ChallengeUser mCurrentUser;

    public static ChallengeTestRequestDialogFragment newInstance(ChallengeTestRequestEvent event) {
        ChallengeTestRequestDialogFragment fragment = null;
        if(event != null) {
            fragment = new ChallengeTestRequestDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("message", Gson.get().toJson(event));
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_challenge_test_request, container, false);
        ButterKnife.bind(this, v);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mChallengeTestRequestEvent = Gson.get().fromJson(getArguments().getString("message"), ChallengeTestRequestEvent.class);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChallengeTestDetails();
    }

    private void loadChallengeTestDetails() {
        showProgress();
        ApiManager.getInstance(getActivity()).getChallengeTestDetails(
                mChallengeTestRequestEvent.challengeTestParentId, AbstractBaseActivity.getSelectedCourseId(),
            new ApiCallback<ChallengeUserListResponse>(getActivity()) {
                @Override
                public void success(ChallengeUserListResponse challengeUserListResponse, Response response) {
                    super.success(challengeUserListResponse, response);
                    if(getActivity() != null) {
                        closeProgress();
                        if (challengeUserListResponse != null && challengeUserListResponse.challengeUsersList != null) {
                            mChallengeUsers = challengeUserListResponse.challengeUsersList;
                            examId = challengeUserListResponse.examId;
                            fillData();
                            updateUI();
                            L.info("Challenge Users : " + Gson.get().toJson(mChallengeUsers));
                        } else {
                            ChallengeTestRequestDialogFragment.this.dismiss();
                        }
                    }
                }

                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    if(getActivity() != null) {
                        closeProgress();
                        ChallengeTestRequestDialogFragment.this.dismiss();
                    }
                }
            });
    }

    private void fillData() {
        for (ChallengeUser user : mChallengeUsers) {
            if(user.idStudent.equals(LoginUserCache.getInstance().getLongResponse().studentId)) {
                mCurrentUser = user;
                ((ChallengeActivity)getActivity()).challengeTestTimeDuration = mCurrentUser.duration;
                if(LoginUserCache.getInstance().getLongResponse() != null) {
                    LoginUserCache.getInstance().getLongResponse().displayName = mCurrentUser.displayName;
                }
                courseTxt.setText(user.course);
                subjectTxt.setText(user.subject);
                chapterTxt.setText(user.chapter);
                questionsTxt.setText(user.questionCount);
                if(!TextUtils.isEmpty(user.duration)) {
                    timeTxt.setText(Integer.valueOf(user.duration)/60 + "");
                }
                virtualCurrencyTxt.setText(user.virtualCurrencyChallenged);
            }
        }
    }

    private void updateChallengeStatus(final String status) {
        showProgress();
        ChallengeStatusRequest request = new ChallengeStatusRequest(LoginUserCache.getInstance().getStudentId(),
                mChallengeTestRequestEvent.challengeTestParentId, status);
        ApiManager.getInstance(getActivity()).postChallengeStatus(Gson.get().toJson(request),
                new ApiCallback<CommonResponseModel>(getActivity()) {
                    @Override
                    public void success(CommonResponseModel commonResponseModel, Response response) {
                        super.success(commonResponseModel, response);
                        if(getActivity() != null) {
                            postStatusEvent(status);
                            closeProgress();
                            if (status.equals("Canceled")) {
                                showToast("Challenge has been canceled");
                                getActivity().finish();
                            } else {
                                loadChallengeTestDetails();
                            }
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if(getActivity() != null) {
                            showToast(error.message);
                            closeProgress();
                        }
                    }
                });
    }

    private void postStatusEvent(String status) {
        ChallengeTestUpdateRequestEvent event = new ChallengeTestUpdateRequestEvent();
        event.setChallengeTestRequestEvent(mChallengeTestRequestEvent);
        event.challengerName = mCurrentUser.displayName;
        event.challengerStatus = status;
        WebSocketHelper.get(getActivity()).sendChallengeUpdateEvent(event);

    }

    @OnClick(R.id.accept_btn)
    public void acceptTest() {
        acceptBtn.setVisibility(View.GONE);
        rejectBtn.setVisibility(View.VISIBLE);
        updateChallengeStatus("Accepted");
    }

    @OnClick(R.id.reject_btn)
    public void rejectTest() {
        rejectBtn.setVisibility(View.GONE);
        acceptBtn.setVisibility(View.VISIBLE);
        if(mCurrentUser != null && mCurrentUser.role.equalsIgnoreCase("Challenger")) {
            updateChallengeStatus("Canceled");
        } else {
            updateChallengeStatus("Declined");
        }
    }

    @OnClick(R.id.refresh_btn)
    public void refreshTest() {
        ChallengeTestUpdateRequestEvent event = new ChallengeTestUpdateRequestEvent();
        event.setChallengeTestRequestEvent(mChallengeTestRequestEvent);
        if(!TextUtils.isEmpty(event.challengerName) && mCurrentUser != null && !TextUtils.isEmpty(mCurrentUser.displayName)) {
            event.challengerName = mCurrentUser.displayName;
        }
        WebSocketHelper.get(getActivity()).sendChallengeUpdateEvent(event);
    }

    @OnClick(R.id.start_btn)
    public void startTest() {
        navigateToExam();
    }

    private void navigateToExam() {
        mTestQuestionPaperId = mCurrentUser.idTestQuestionPaper;
        ChallengeTestUpdateRequestEvent event = new ChallengeTestUpdateRequestEvent();
        event.setChallengeTestRequestEvent(mChallengeTestRequestEvent);
        event.challengerName = mCurrentUser.displayName;
        event.challengerStatus = "Started";
        sendChallengeStartRequestEvent();
        WebSocketHelper.get(getActivity()).sendChallengeUpdateEvent(event);
        updateChallengeStatus("In Test");
        ((ChallengeActivity) getActivity()).startChallengeTest(mCurrentUser.idTestQuestionPaper, mCurrentUser.challengeTestParentId);
    }

    /**
     *
     */
    @Deprecated
    private void startChallenge() {
        ChallengestartRequest request = new ChallengestartRequest(mCurrentUser.challengeTestParentId, examId);
        ApiManager.getInstance(getActivity()).postChallengeStart(Gson.get().toJson(request),
                new ApiCallback<ChallengeStartResponseModel>(getActivity()) {
                    @Override
                    public void success(ChallengeStartResponseModel challengeStartResponseModel, Response response) {
                        super.success(challengeStartResponseModel, response);
                        if(challengeStartResponseModel != null && !TextUtils.isEmpty(challengeStartResponseModel.testQuestionPaperId)) {
                            mTestQuestionPaperId = mCurrentUser.idTestQuestionPaper;
                            ChallengeTestUpdateRequestEvent event = new ChallengeTestUpdateRequestEvent();
                            event.setChallengeTestRequestEvent(mChallengeTestRequestEvent);
                            event.challengerName = mCurrentUser.displayName;
                            event.challengerStatus = "Started";
                            sendChallengeStartRequestEvent();
                            WebSocketHelper.get(getActivity()).sendChallengeUpdateEvent(event);
                            updateChallengeStatus("In Test");
                            ((ChallengeActivity) getActivity()).startChallengeTest(mTestQuestionPaperId, mCurrentUser.challengeTestParentId);
                        } else {
                            showToast("could not start exam");
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                    }
                });
    }

    private void sendChallengeStartRequestEvent() {
        ChallengeTestStartRequestEvent event = new ChallengeTestStartRequestEvent();
        event.challengerName = mCurrentUser.displayName;
        event.challengeStatus = "Accepted";
        event.challengerStatus = "Started";
        event.setChallengeTestParentId(mCurrentUser.challengeTestParentId);
        event.testQuestionPaperId = mCurrentUser.idTestQuestionPaper;
        WebSocketHelper.get(getActivity()).sendChallengeStartEvent(event);
    }

    public void updateUI() {
        if(getActivity() == null) {
            return;
        }
        int accepted = 0;
        int declined = 0;
        int initiated = 0;
        for(ChallengeUser friend : mChallengeUsers) {
            if(friend.idStudent.equals(mCurrentUser.idStudent)) {
                if(friend.status.equalsIgnoreCase("Accepted")) {
                    statusTxt.setText("Accepted");
                    statusTxt.setBackgroundColor(getResources().getColor(R.color.green));
                    acceptBtn.setVisibility(View.GONE);
                    rejectBtn.setVisibility(View.VISIBLE);
                } else if(friend.status.equalsIgnoreCase("Declined")) {
                    statusTxt.setText("Declined");
                    statusTxt.setBackgroundColor(getResources().getColor(R.color.red));
                    acceptBtn.setVisibility(View.VISIBLE);
                    rejectBtn.setVisibility(View.GONE);
                } else {
                    statusTxt.setText("Initiated");
                    statusTxt.setBackgroundColor(getResources().getColor(R.color.blue));
                }
            }
            if(friend.role.equalsIgnoreCase("challenger")) {
                initiated++;
            } else if(friend.status.equalsIgnoreCase("Accepted")) {
                accepted++;
            } else if(friend.status.equalsIgnoreCase("Declined")) {
                declined++;
            }
        }
        acceptedTxt.setText(String.valueOf(accepted));
        declinedTxt.setText(String.valueOf(declined));
        initiatedTxt.setText(String.valueOf(initiated));
        startBtn.setVisibility(mCurrentUser.role.equalsIgnoreCase("Challenger") ? View.VISIBLE : View.GONE);
        startBtn.setEnabled(initiated + accepted >= 1);
    }

    public void onEventMainThread(ChallengeTestUpdateEvent event) {
        if(getActivity() != null) {
            for (ChallengeUser user : mChallengeUsers) {
                if (user.displayName.equals(event.challengerName)) {
                    user.status = event.challengerStatus;
                }
            }
            updateUI();
        }
    }

    public void onEventMainThread(ChallengeTestStartEvent event) {
        if(getActivity() != null) {
            mTestQuestionPaperId = event.testQuestionPaperId;
            sendChallengeStartRequestEvent();
            ((ChallengeActivity) getActivity()).startChallengeTest(mTestQuestionPaperId, event.challengeTestParentId);
        }
    }

}