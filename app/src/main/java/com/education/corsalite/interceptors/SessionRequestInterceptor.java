package com.education.corsalite.interceptors;

import android.util.Log;

import com.education.corsalite.services.ApiClientService;

import retrofit.RequestInterceptor;

/**
 * Created by vissu on 9/11/15.
 */
public class SessionRequestInterceptor implements RequestInterceptor {
    private static final String TAG = SessionRequestInterceptor.class.getSimpleName();

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("Content-Type", "application/json");
        String setcookie = ApiClientService.getSetCookie();
        if(setcookie != null && !setcookie.isEmpty()) {
            request.addHeader("cookie", setcookie);
        }
    }
}