package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.WelcomeDetails;
import com.education.corsalite.services.ApiClientService;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class WelcomeActivity extends AbstractBaseActivity implements View.OnClickListener {

    @Bind(R.id.redeem_welcome_btn) Button redeemBtn;
    @Bind(R.id.studycenter_btn) Button studyCenterBtn;
    @Bind(R.id.messages_btn) Button messagesBtn;
    @Bind(R.id.scheduled_tests_btn) Button scheduledTestsBtn;
    @Bind(R.id.recommended_reading_btn) Button recommendedReadingBtn;
    @Bind(R.id.name_txt)TextView fullName;
    @Bind(R.id.lastvisit_date)TextView lastVisitDate;
    @Bind(R.id.lastvisit_time)TextView lastVisitTime;
    @Bind(R.id.profile_pic)ImageView profilePic;
    @Bind(R.id.vc_totalcount)TextView vcTotal;
    @Bind(R.id.vc_lastsessioncount)TextView vcLastSession;

    private boolean closeApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.welcome_activity, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        setToolbarForWelcomeScreen();
        setListeners();
        getWelcomeDetails();
    }

    private void getWelcomeDetails() {
        ApiManager.getInstance(this).getWelcomeDetails(LoginUserCache.getInstance().loginResponse.studentId, new ApiCallback<WelcomeDetails>(WelcomeActivity.this) {
            @Override
            public void success(WelcomeDetails welcomeDetails, Response response) {
                super.success(welcomeDetails, response);
                if(welcomeDetails != null){
                    if(!isDestroyed() && !TextUtils.isEmpty(welcomeDetails.photoUrl)) {
                        Glide.with(WelcomeActivity.this).load(ApiClientService.getBaseUrl() + welcomeDetails.photoUrl.replaceFirst("./", "")).into(profilePic);
                    }
                    if(!TextUtils.isEmpty(welcomeDetails.firstName) && !TextUtils.isEmpty(welcomeDetails.lastName)) {
                        fullName.setText(welcomeDetails.firstName + " " + welcomeDetails.lastName);
                    }
                    if(!TextUtils.isEmpty(welcomeDetails.userLastLoginDate)) {
                        String[] dateStr = welcomeDetails.userLastLoginDate.split(" ");
                        lastVisitDate.setText(dateStr[0]);
                        lastVisitTime.setText(dateStr[1]);
                    }
                    if(!TextUtils.isEmpty(welcomeDetails.vcCount)) {
                        vcTotal.setText(welcomeDetails.vcCount);
                    }
                    if(!TextUtils.isEmpty(welcomeDetails.vcInLastSession)) {
                        vcLastSession.setText(welcomeDetails.vcInLastSession);
                    }
                }
            }
        });
    }

    private void setListeners() {
        redeemBtn.setOnClickListener(this);
        studyCenterBtn.setOnClickListener(this);
        messagesBtn.setOnClickListener(this);
        scheduledTestsBtn.setOnClickListener(this);
        recommendedReadingBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.redeem_welcome_btn:
                redeem();
                break;
            case R.id.studycenter_btn:
                loadStudyCenterScreen();
                break;
            case R.id.messages_btn:
                break;
            case R.id.scheduled_tests_btn:
                showScheduledTestsDialog();
                break;
            case R.id.recommended_reading_btn:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (!closeApp) {
            closeApp = true;
            showToast(getString(R.string.app_close_alert));
        } else {
            finish();
        }
    }
}
