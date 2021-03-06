package com.education.corsalite.models.requestmodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

/**
 * Created by vissu on 10/15/15.
 */
public class UserProfileModel {
    @SerializedName("UpdateTime")
    public String updateTime;
    @SerializedName("idUser")
    public String userId;
    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("PhotoBase64Data")
    public String photoBase64Data;
    @SerializedName("DisplayName")
    public String displayName;
    @SerializedName("GivenName")
    public String givenName;
    @SerializedName("SurName")
    public String surName;
    @SerializedName("EmailID")
    public String emailId;
    @SerializedName("Gender")
    public String gender;
    @SerializedName("DOB")
    public String dob;
    @SerializedName("Mobile")
    public String mobile;
    @Ignore
    @SerializedName("Password")
    public String password;
}
