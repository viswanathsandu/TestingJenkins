package com.education.corsalite.api;

import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.models.responsemodels.CorsaliteError;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vissu on 9/11/15.
 */
public abstract class ApiCallback<T> implements Callback<T> {
    public abstract void failure(CorsaliteError error);

    @Override
    public void success(T t, Response response) {
        if(response != null) {
            AbstractBaseActivity.saveSessionCookie(response);
        }
    }

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