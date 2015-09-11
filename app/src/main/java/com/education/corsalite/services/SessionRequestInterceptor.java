package com.education.corsalite.services;

import retrofit.RequestInterceptor;

/**
 * Created by vissu on 9/11/15.
 */
public class SessionRequestInterceptor implements RequestInterceptor {
    private static final String TAG = SessionRequestInterceptor.class.getSimpleName();

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("Content-Type", "application/json");
    }
}