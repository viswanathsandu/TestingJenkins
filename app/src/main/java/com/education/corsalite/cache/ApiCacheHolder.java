package com.education.corsalite.cache;

import com.education.corsalite.models.db.reqres.LoginReqRes;
import com.education.corsalite.models.responsemodels.LoginResponse;

/**
 * Created by vissu on 11/27/15.
 */
public class ApiCacheHolder {

    public static LoginReqRes login;

    public static void setLoginRequest(String loginId, String passwordHash) {
        ApiCacheHolder.login = new LoginReqRes();
        login.loginId = loginId;
        login.passwordHash = passwordHash;
    }

    public static void setLoginResponse(LoginResponse response) {
        if(login != null) {
            login.response = response;
        }
    }
}
