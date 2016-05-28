package com.corsalite.tabletapp.cache;

import com.corsalite.tabletapp.models.responsemodels.LoginResponse;
import com.corsalite.tabletapp.models.responsemodels.WelcomeDetails;
import com.squareup.okhttp.Request;

/**
 * Created by vissu on 9/12/15.
 */
public class LoginUserCache {

    private static LoginUserCache instance;
    public LoginResponse loginResponse;
    public WelcomeDetails welcomeDetails;
    // This will be used in okhttp interceptor
    public Request loginRequest;

    private LoginUserCache() {
    }

    public static LoginUserCache getInstance() {
        if (instance == null) {
            instance = new LoginUserCache();
        }
        return instance;
    }

    public LoginResponse getLongResponse() {
        return loginResponse;
    }

    public void setLoginResponse(LoginResponse response) {
        this.loginResponse = response;
    }

    public void setWelcomeDetails(WelcomeDetails welcomeDetails) {
        this.welcomeDetails = welcomeDetails;
    }

    public void clearCache() {
        this.loginResponse = null;
    }
}
