package com.corsalite.tabletapp.interceptors;

import com.corsalite.tabletapp.services.ApiClientService;

import retrofit.RequestInterceptor;

/**
 * Created by vissu on 9/11/15.
 */
public class SessionRequestInterceptor implements RequestInterceptor {
    private static final String TAG = SessionRequestInterceptor.class.getSimpleName();

    @Override
    public void intercept(RequestFacade request) {
        String setcookie = ApiClientService.getSetCookie();
        if(setcookie != null && !setcookie.isEmpty()) {
            request.addHeader("cookie", setcookie);
        }
    }
}