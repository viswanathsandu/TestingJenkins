package com.education.corsalite.api;

import android.content.Context;

import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.utils.L;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vissu on 9/11/15.
 */
public abstract class ApiCallback<T> implements Callback<T> {
    public Context mContext;

    public ApiCallback(Context context) {
        this.mContext = context;
    }

    public void failure(CorsaliteError error) {
        if(error != null && error.message != null && error.message.equalsIgnoreCase("Unathorized session.")) {
//            Toast.makeText(mContext, "Session expired. Please login to continue", Toast.LENGTH_SHORT).show();
            error.message = "";
        }
    }

    @Override
    public void success(T t, Response response) {
        if(response != null && response.getStatus() == 200) {
            AbstractBaseActivity.saveSessionCookie(response);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        try {
            CorsaliteError restError = (CorsaliteError) error.getBodyAs(CorsaliteError.class);
            if (restError != null) {
                failure(restError);
            } else if (error.getResponse() != null && error.getResponse().getStatus() == 401) { // Unauthentication
                CorsaliteError corsaliteError = new CorsaliteError();
                corsaliteError.message = "Unathorized session.";
                failure(corsaliteError);
            } else {
                CorsaliteError corsaliteError = new CorsaliteError();
                corsaliteError.message = "";//"something went wrong";
                failure(corsaliteError);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            CorsaliteError corsaliteError = new CorsaliteError();
            failure(corsaliteError);
        }
    }
}