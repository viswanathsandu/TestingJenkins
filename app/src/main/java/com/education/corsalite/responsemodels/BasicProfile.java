package com.education.corsalite.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vissu on 9/12/15.
 */
public class BasicProfile {
    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("PhotoUrl")
    public String photoUrl;
    @SerializedName("DisplayName")
    public String displayName;
    @SerializedName("GivenName")
    public String givenName;
    @SerializedName("SurName")
    public String surName;
    @SerializedName("EmailID")
    public String emailId;
    @SerializedName("EnrolledCourses")
    public String enrolledCourses;
}
