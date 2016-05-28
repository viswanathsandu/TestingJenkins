package com.education.corsalite.models.db;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vissu on 5/23/16.
 */
public class ScheduledTestsArray extends BaseModel implements Serializable {
    @SerializedName("ScheduledDueDate")
    public String dueDate;
    @SerializedName("ExamName")
    public String examName;
    @SerializedName("ScheduleStartTime")
    public String startTime;
    @SerializedName("idTestQuestionPaper")
    public String testQuestionPaperId;

    public ScheduledTestsArray() {}
}