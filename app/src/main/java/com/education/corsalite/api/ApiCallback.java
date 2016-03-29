package com.education.corsalite.api;

import android.content.Context;
import android.widget.Toast;

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
    private Context mContext;

    public ApiCallback(Context context) {
        this.mContext = context;
    }

    public void failure(CorsaliteError error) {
        if(error != null && error.message != null && error.message.equalsIgnoreCase("Unathorized session.")) {
           /*
                ApiManager.getInstance(mContext).login(ApiCacheHolder.getInstance().loginRequest.loginId, ApiCacheHolder.getInstance().loginRequest.passwordHash,
                    new ApiCallback<LoginResponse>(mContext) {
                        @Override
                        public void success(LoginResponse loginResponse, Response response) {
                            super.success(loginResponse, response);
                            ApiCacheHolder.getInstance().setLoginResponse(loginResponse);
                            AbstractBaseActivity.saveSessionCookie(response);
                            DbManager.getInstance(mContext).saveReqRes(ApiCacheHolder.getInstance().login);
                            AppPref.getInstance(mContext).save("loginId", ApiCacheHolder.getInstance().loginRequest.loginId);
                            AppPref.getInstance(mContext).save("passwordHash", ApiCacheHolder.getInstance().loginRequest.passwordHash);
                            Toast.makeText(mContext, "Please try again", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        @Override
                        public void failure(CorsaliteError error) {
                            super.failure(error);
                            AppPref.getInstance(mContext).remove("loginId");
                            AppPref.getInstance(mContext).remove("passwordHash");
                            Toast.makeText(mContext, "Session expired. Please login to continue", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    }, false);
                    */
            Toast.makeText(mContext, "Session expired. Please login to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void success(T t, Response response) {
        if(response != null) {
            AbstractBaseActivity.saveSessionCookie(response);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        try {
            CorsaliteError restError = (CorsaliteError) error.getBodyAs(CorsaliteError.class);
            if (restError != null) {
                failure(restError);
            } else if (error.getResponse().getStatus() == 401) { // Unauthentication
                CorsaliteError corsaliteError = new CorsaliteError();
                corsaliteError.message = "Unathorized session.";
                failure(corsaliteError);
            } else {
                CorsaliteError corsaliteError = new CorsaliteError();
                corsaliteError.message = "something went wrong";
                failure(corsaliteError);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            CorsaliteError corsaliteError = new CorsaliteError();
            corsaliteError.message = "Please try again";
            failure(corsaliteError);
        }
    }
}