package com.education.corsalite.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.education.corsalite.models.FeatureModel;
import com.education.corsalite.services.ApiClientService;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CountDownTimer(2000, 100) {
            @Override
            public void onFinish() {

                FeatureModel model =  new AppConfig().readFeatures(SplashActivity.this);
                String url = model.url;
                ApiClientService.setBaseUrl(url);
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
