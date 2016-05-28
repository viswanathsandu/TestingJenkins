package com.corsalite.tabletapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.api.ApiCallback;
import com.corsalite.tabletapp.api.ApiManager;
import com.corsalite.tabletapp.cache.ApiCacheHolder;
import com.corsalite.tabletapp.cache.LoginUserCache;
import com.corsalite.tabletapp.models.responsemodels.CorsaliteError;
import com.corsalite.tabletapp.models.responsemodels.LoginResponse;
import com.corsalite.tabletapp.services.ApiClientService;
import com.corsalite.tabletapp.utils.AppConfig;
import com.corsalite.tabletapp.utils.AppPref;

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
        AppConfig.loadAppconfig(SplashActivity.this);
        checkProduction();
        new CountDownTimer(AppConfig.getInstance().getSplashDuration(), 100) {
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

    private void checkProduction() {
        final String enableProduction = AppPref.getInstance(SplashActivity.this).getValue("enable_production");
        AppConfig config = AppConfig.getInstance();
        if(config != null) {
            if (TextUtils.isEmpty(enableProduction)) {
                AppPref.getInstance(SplashActivity.this).save("enable_production", config.enableProduction + "");
            } else {
                config.enableProduction = enableProduction.equalsIgnoreCase("true");
            }
            ApiClientService.setBaseUrl(config.enableProduction ? config.productionUrl : config.stageUrl);
            ApiClientService.setSocketUrl(config.enableProduction ? config.productionSocketUrl : config.stageSocketUrl);
        }
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
                    onLoginsuccess(loginResponse, fetchLocal);
                    dbManager.saveReqRes(ApiCacheHolder.getInstance().login);
                    appPref.save("loginId", username);
                    appPref.save("passwordHash", password);
                    startWebSocket();
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
            // Uncomment it after fixing server issues
//            AppConfig.loadAppConfigFromService(SplashActivity.this, "1624");
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
