package com.education.corsalite.models.responsemodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/11/15.
 */
public class LoginResponse extends BaseResponseModel {
    @SerializedName("idUser")
    public String idUser;
    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("idEntity")
    public String entitiyId;
    @SerializedName("AuthToken")
    public String authtoken;
    @SerializedName("disableRewardRedeem")
    public String disableRewardRedeem;

    // Is used for challenge test
    public String displayName = "";

    public LoginResponse() {}

    public boolean isRewardRedeemEnabled() {
        return !TextUtils.isEmpty(disableRewardRedeem) && disableRewardRedeem.equalsIgnoreCase("N");
    }
}