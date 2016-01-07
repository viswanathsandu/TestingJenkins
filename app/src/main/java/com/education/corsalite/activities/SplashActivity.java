package com.education.corsalite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.AppConfig;

import retrofit.client.Response;

/**
 * Created by Madhuri on 03-01-2016.
 */
public class SplashActivity extends AbstractBaseActivity {

    public boolean isTimerFinished = false;
    public boolean isLoginApiFinished = false;
    public boolean isLoginSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkAutoLogin();
        new CountDownTimer(2000, 100) {
            @Override
            public void onFinish() {
                AppConfig.loadAppconfig(SplashActivity.this);
                ApiClientService.setBaseUrl(AppConfig.getInstance().baseUrl);
                isTimerFinished = true;
                navigateToNextScreen();
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

    private void checkAutoLogin() {
        String username = appPref.getValue("loginId");
        String passwordHash =  appPref.getValue("passwordHash");
        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(passwordHash)) {
            login(username, passwordHash, false);
        }
    }
    private void login(final String username, final String password, final boolean fetchLocal) {
        showProgress();
        ApiManager.getInstance(this).login(username, password, new ApiCallback<LoginResponse>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                closeProgress();
                if (error != null && !TextUtils.isEmpty(error.message)) {
                    showToast(error.message);
                    navigateToNextScreen();
                }
            }

            @Override
            public void success(LoginResponse loginResponse, Response response) {
                super.success(loginResponse, response);
                closeProgress();
                isLoginApiFinished = true;
                if (loginResponse.isSuccessful()) {
                    ApiCacheHolder.getInstance().setLoginResponse(loginResponse);
                    dbManager.saveReqRes(ApiCacheHolder.getInstance().login);
                    appPref.save("loginId", username);
                    appPref.save("passwordHash", password);
                    onLoginsuccess(loginResponse, fetchLocal);
                } else {
                    showToast(getResources().getString(R.string.login_failed));
                }
                navigateToNextScreen();
            }
        }, fetchLocal);
    }

    private void onLoginsuccess(LoginResponse response, boolean fetchLocal) {
        if(response != null) {
            LoginUserCache.getInstance().setLoginResponse(response);
            if(!fetchLocal) {
                isLoginSuccess = true;
                showToast(getResources().getString(R.string.login_successful));
            }
        } else {
            showToast(getResources().getString(R.string.login_failed));
        }
    }

    private void navigateToNextScreen() {
        if(isTimerFinished && isLoginApiFinished) {
            startActivity(new Intent(SplashActivity.this, isLoginSuccess ? StudyCentreActivity.class : LoginActivity.class));
            finish();
        }
    }
}
