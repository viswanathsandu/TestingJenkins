package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 6/17/16.
 */

public class QuestionPaperExamDetails extends BaseModel {

    @SerializedName("idExamTemplate")
    public String examTemplateId;
    @SerializedName("Name")
    public String examName;
    @SerializedName("ExamInstructions")
    public String examInstructionsHtml;
    @SerializedName("Scheduled Time")
    public String scheduledTime;
    @SerializedName("TimeToStart")
    public String timeToStart;
    @SerializedName("LastStartTime")
    public String lastStartTime;
    @SerializedName("TotalTime")
    public String totalTime;

    public QuestionPaperExamDetails() {}
}
