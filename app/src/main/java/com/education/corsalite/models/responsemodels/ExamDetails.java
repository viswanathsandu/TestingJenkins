package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 2/20/16.
 */
// TODO : need to change the db while upgrading
public class ExamDetails {
    @SerializedName("idExamTemplate")
    public String examTemplateId;
    @SerializedName("Name")
    public String examName;
    @SerializedName("ExamInstructions")
    public String examInstucation;
    @SerializedName("ScheduledTime")
    public String scheduledTime;
    @SerializedName("TimeToStart")
    public String timeTostart;
    @SerializedName("LastStartTime")
    public String lastStartTime;
    @SerializedName("DueDateTime")
    public String dueDateTime;
    @SerializedName("TotalTestDuration")
    public String totalTestDuration;
}
