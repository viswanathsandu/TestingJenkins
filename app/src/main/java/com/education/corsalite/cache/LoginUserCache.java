package com.education.corsalite.cache;

import android.text.TextUtils;

import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.WelcomeDetails;
import com.squareup.okhttp.Request;

/**
 * Created by vissu on 9/12/15.
 */
public class LoginUserCache {

    private static LoginUserCache instance;
    private LoginResponse loginResponse;
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

    public String getStudentId() {
        if(loginResponse != null && !TextUtils.isEmpty(loginResponse.studentId)) {
            return loginResponse.studentId;
        }
        return "";
    }

    public String getEntityId() {
        if(loginResponse != null && !TextUtils.isEmpty(loginResponse.entitiyId)) {
            return loginResponse.entitiyId;
        }
        return "";
    }

    public String getAuthtoken() {
        if(loginResponse != null && !TextUtils.isEmpty(loginResponse.authtoken)) {
            return loginResponse.authtoken;
        }
        return "";
    }

    public String getDisplayName() {
        if(loginResponse != null && !TextUtils.isEmpty(loginResponse.displayName)) {
            return loginResponse.displayName;
        }
        return "";
    }
}
