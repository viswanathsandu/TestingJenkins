package com.education.corsalite.api;

import com.education.corsalite.responsemodels.CorsaliteError;
import com.education.corsalite.services.ApiClientService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by vissu on 9/11/15.
 */
public abstract class ApiCallback<T> implements Callback<T> {
    public abstract void failure(CorsaliteError error);

    @Override
    public void failure(RetrofitError error) {
        CorsaliteError restError = (CorsaliteError) error.getBodyAs(CorsaliteError.class);
        if (restError != null) {
            failure(restError);
        } else {
            CorsaliteError corsaliteError = new CorsaliteError();
            corsaliteError.message = error.getMessage();
            failure(corsaliteError);
        }
    }
}