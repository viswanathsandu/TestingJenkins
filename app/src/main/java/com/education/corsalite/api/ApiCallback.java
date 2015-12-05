package com.education.corsalite.api;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.LoginActivity;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.utils.AppPref;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vissu on 9/11/15.
 */
public abstract class ApiCallback<T> implements Callback<T> {
    private Context mContext;

    public ApiCallback(Context context) {
        this.mContext = context;
    }

    public void failure(CorsaliteError error) {
        AppPref.getInstance(mContext).remove("loginId");
        AppPref.getInstance(mContext).remove("passwordHash");
        Toast.makeText(mContext, "Session expired... \nPlease login to continue...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

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