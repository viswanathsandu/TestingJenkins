package com.education.corsalite.cache;

import com.education.corsalite.responsemodels.LoginResponse;

/**
 * Created by vissu on 9/12/15.
 */
public class LoginUserCache {

    private static LoginUserCache instance;
    public LoginResponse loginResponse;

    private LoginUserCache() {
    }

    public static LoginUserCache getInstance() {
        if(instance == null) {
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
}
