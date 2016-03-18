package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Girish on 19/12/15.
 */
public class Exam {

    @SerializedName("idExam")
    public String examId;
    @SerializedName("Name")
    public String examName;

}
