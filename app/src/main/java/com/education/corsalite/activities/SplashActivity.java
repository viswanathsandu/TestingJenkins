package com.education.corsalite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.AppConfig;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class SplashActivity extends AbstractBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CountDownTimer(2000, 100) {
            @Override
            public void onFinish() {
                AppConfig.loadAppconfig(SplashActivity.this);
                ApiClientService.setBaseUrl(AppConfig.getInstance().baseUrl);
                redirectToLogin();
            }
            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }

    private void redirectToLogin(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
