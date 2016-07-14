package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aastha on 26/10/15.
 */
public class CourseAnalysisPercentile extends BaseModel {
    @SerializedName("idTestQuestionPaper")
    public String idTest;
    @SerializedName("SubjectName")
    public String subjectName;
    @SerializedName("StartTime")
    public String startTime;
    @SerializedName("Percentile")
    public String percentile;
    @SerializedName("Rank")
    public String rank;
    @SerializedName("HeadCount")
    public String headCount;
}
