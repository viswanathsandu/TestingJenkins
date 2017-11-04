package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vissu on 9/12/15.
 */
public class BasicProfile implements Serializable {
    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("PhotoUrl")
    public String photoUrl;
    @SerializedName("DisplayName")
    public String displayName = "";
    @SerializedName("GivenName")
    public String givenName = "";
    @SerializedName("SurName")
    public String surName = "";
    @SerializedName("EmailID")
    public String emailId = "";
    @SerializedName("EnrolledCourses")
    public String enrolledCourses;
    @SerializedName("PhotoUrlEncoded")
    public String photoBase64EncodedString;

}
