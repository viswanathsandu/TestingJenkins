package com.education.corsalite.models.requestmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/11/15.
 */
public class LoginUser {
    @SerializedName("LoginID")
    public String loginId;
    @SerializedName("PasswordHash")
    public String passwordHash;
}
