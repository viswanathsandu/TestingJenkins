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
import com.education.corsalite.utils.AppUpdateUtils;
import com.education.corsalite.utils.SystemUtils;

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
        new AppUpdateUtils(this).update();
        new CountDownTimer(getAppConfig(this).getSplashDuration(), 100) {
            @Override
            public void onFinish() {
                isTimerFinished = true;
                navigateToNextScreen();
            }

            @Override
            public void onTick(long millisUntilFinished) {}
        }.start();
        checkAutoLogin();
    }

    private void checkAutoLogin() {
        String username = appPref.getValue("loginId");
        String passwordHash =  appPref.getValue("passwordHash");
        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(passwordHash)) {
            login(username, passwordHash, false);
        } else {
            isLoginApiFinished = true;
            navigateToNextScreen();
        }
    }

    private void login(final String username, final String password, final boolean fetchLocal) {
        ApiManager.getInstance(this).login(username, password, new ApiCallback<LoginResponse>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                closeProgress();
                isLoginSuccess = false;
                isLoginApiFinished = true;
                if (error != null && !TextUtils.isEmpty(error.message)) {
                    showToast(error.message);
                }
                navigateToNextScreen();
            }

            @Override
            public void success(LoginResponse loginResponse, Response response) {
                super.success(loginResponse, response);
                closeProgress();
                isLoginApiFinished = true;
                if (loginResponse.isSuccessful()) {
                    setCrashlyticsUserData();
                    onLoginsuccess(loginResponse, fetchLocal);
                    dbManager.saveReqRes(ApiCacheHolder.getInstance().login);
                    appPref.save("loginId", username);
                    appPref.save("passwordHash", password);
                    appPref.setUserId(loginResponse.idUser);
                    if(SystemUtils.isNetworkConnected(SplashActivity.this)) {
                        startWebSocket();
                        loadAppConfig();
                    }
                } else {
                    showToast(getResources().getString(R.string.login_failed));
                }
                navigateToNextScreen();
            }
        }, fetchLocal);
    }

    private void onLoginsuccess(LoginResponse response, boolean fetchLocal) {
        if(response != null && response.studentId != null) {
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
            startActivity(new Intent(SplashActivity.this, isLoginSuccess ? WelcomeActivity.class : LoginActivity.class));
            finish();
        }
    }
}
