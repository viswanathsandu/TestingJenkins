package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/11/15.
 */
public class LoginResponse extends BaseResponseModel {
    @SerializedName("idUser")
    public String userId;
    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("idEntity")
    public String entitiyId;
    @SerializedName("AuthToken")
    public String authtoken;

    // Is used for challenge test
    public String displayName = "";
}