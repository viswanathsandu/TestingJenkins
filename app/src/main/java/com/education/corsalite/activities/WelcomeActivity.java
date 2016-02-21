package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.education.corsalite.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeActivity extends AbstractBaseActivity implements View.OnClickListener {

    @Bind(R.id.redeem_btn) Button redeemBtn;
    @Bind(R.id.studycenter_btn) Button studyCenterBtn;
    @Bind(R.id.messages_btn) Button messagesBtn;
    @Bind(R.id.scheduled_tests_btn) Button scheduledTestsBtn;
    @Bind(R.id.recommended_reading_btn) Button recommendedReadingBtn;

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
            case R.id.redeem_btn:
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
