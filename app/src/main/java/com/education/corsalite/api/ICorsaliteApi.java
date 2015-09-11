package com.education.corsalite.api;

import com.education.corsalite.requestmodels.LoginUser;
import com.education.corsalite.responsemodels.LoginResponse;
import com.education.corsalite.responsemodels.LogoutResponse;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by vissu on 9/11/15.
 */
public interface ICorsaliteApi {

    @GET("/AuthToken")
    void login(@Query("LoginID") String loginId, @Query("PasswordHash") String passwordHash, ApiCallback<LoginResponse> callback);

    @POST("/AuthToken")
    void logout(@Query("Update") String update, ApiCallback<LogoutResponse> callback);
}
