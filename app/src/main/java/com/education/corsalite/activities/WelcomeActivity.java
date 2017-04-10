package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.WelcomeDetails;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.services.ContentDownloadService;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class WelcomeActivity extends AbstractBaseActivity implements View.OnClickListener {

    @Bind(R.id.course_ended_txt)
    TextView courseEndedTxt;
    @Bind(R.id.redeem_welcome_btn)
    Button redeemBtn;
    @Bind(R.id.studycenter_btn)
    Button studyCenterBtn;
    @Bind(R.id.messages_btn)
    Button messagesBtn;
    @Bind(R.id.scheduled_tests_btn)
    Button scheduledTestsBtn;
    @Bind(R.id.recommended_reading_btn)
    Button recommendedReadingBtn;
    @Bind(R.id.name_txt)
    TextView fullName;
    @Bind(R.id.lastvisit_date)
    TextView lastVisitDate;
    @Bind(R.id.lastvisit_time)
    TextView lastVisitTime;
    @Bind(R.id.profile_pic)
    ImageView profilePic;
    @Bind(R.id.vc_totalcount)
    TextView vcTotal;
    @Bind(R.id.vc_lastsessioncount)
    TextView vcLastSession;
    private boolean loadContent = true;
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

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (LoginUserCache.getInstance().getLoginResponse().isRewardRedeemEnabled()) {
                redeemBtn.setVisibility(View.VISIBLE);
            } else {
                redeemBtn.setVisibility(View.GONE);
            }
            messagesBtn.setVisibility(SystemUtils.isNetworkConnected(this) ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    @Override
    public void onEvent(Course course) {
        super.onEvent(course);
        if (isCourseEnded(course)) {
            courseEndedTxt.setVisibility(View.VISIBLE);
            courseEndedTxt.setText("This course ended on " + course.endDate);
        } else {
            courseEndedTxt.setVisibility(View.GONE);
        }
        if(course.isTestSeries()) {
            studyCenterBtn.setText("Test Series");
        } else {
            studyCenterBtn.setText("Study Center");
        }
        getContentIndex(getSelectedCourse(), LoginUserCache.getInstance().getStudentId());
        // Start download service if its not started
        if (loadContent) {
            stopService(new Intent(getApplicationContext(), ContentDownloadService.class));
            startService(new Intent(getApplicationContext(), ContentDownloadService.class));
            loadContent = false;
        }
    }

    private void getWelcomeDetails() {
        try {
            ApiManager.getInstance(this).getWelcomeDetails(LoginUserCache.getInstance().getStudentId(), new ApiCallback<WelcomeDetails>(WelcomeActivity.this) {
                @Override
                public void success(WelcomeDetails welcomeDetails, Response response) {
                    super.success(welcomeDetails, response);
                    if (welcomeDetails != null) {
                        ApiCacheHolder.getInstance().setWelcomeRespose(welcomeDetails);
                        dbManager.saveReqRes(ApiCacheHolder.getInstance().welcome);
                        LoginUserCache.getInstance().setWelcomeDetails(welcomeDetails);
                        if (!isDestroyed() && !TextUtils.isEmpty(welcomeDetails.photoUrl)) {
                            Picasso.with(WelcomeActivity.this)
                                    .load(ApiClientService.getBaseUrl() + welcomeDetails.photoUrl.replaceFirst("./", ""))
                                    .placeholder(getResources().getDrawable(R.drawable.profile_pic))
                                    .error(getResources().getDrawable(R.drawable.profile_pic))
                                    .into(profilePic);
                        }
                        if (!TextUtils.isEmpty(welcomeDetails.firstName) && !TextUtils.isEmpty(welcomeDetails.lastName)) {
                            fullName.setText(welcomeDetails.firstName + " " + welcomeDetails.lastName);
                        }
                        if (!TextUtils.isEmpty(welcomeDetails.userLastLoginDate)) {
                            String[] dateStr = welcomeDetails.userLastLoginDate.split(" ");
                            lastVisitDate.setText(dateStr[0]);
                            lastVisitTime.setText(dateStr[1]);
                        }
                        if (!TextUtils.isEmpty(welcomeDetails.vcCount)) {
                            vcTotal.setText(welcomeDetails.vcCount);
                        }
                        if (!TextUtils.isEmpty(welcomeDetails.vcInLastSession)) {
                            vcLastSession.setText(welcomeDetails.vcInLastSession);
                        }
                    }
                }
            });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void setListeners() {
        profilePic.setOnClickListener(this);
        redeemBtn.setOnClickListener(this);
        studyCenterBtn.setOnClickListener(this);
        messagesBtn.setOnClickListener(this);
        scheduledTestsBtn.setOnClickListener(this);
        recommendedReadingBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isCourseEnded(AbstractBaseActivity.getSelectedCourse())) {
            showToast("Please select a different course");
            return;
        }
        switch (v.getId()) {
            case R.id.redeem_welcome_btn:
                redeem();
                break;
            case R.id.profile_pic:
                Intent myprofile = new Intent(this, UserProfileActivity.class);
                startActivity(myprofile);
                break;
            case R.id.studycenter_btn:
                loadStudyCenterScreen();
                break;
            case R.id.messages_btn:
                Intent messagesIntent = new Intent(this, MessagesActivity.class);
                startActivity(messagesIntent);
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
