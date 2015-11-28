package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aastha on 09/11/15.
 */
public class ExamHistory {

    @SerializedName("idTestAnswerPaper")
    public String idTestAnswerPaper;
    @SerializedName("StartTime")
    public String dateTime;
    @SerializedName("TestName")
    public String examName;

    @SerializedName("TestType")
    public String testType;
    @SerializedName("Rank")
    public String rank;
    @SerializedName("Status")
    public String status;
}
