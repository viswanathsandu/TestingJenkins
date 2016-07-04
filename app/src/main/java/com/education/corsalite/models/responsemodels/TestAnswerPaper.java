package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vissu on 3/7/16.
 */
public class TestAnswerPaper extends BaseModel{

    @SerializedName("idEntity")
    public String entityId;
    @SerializedName("idTestQuestionPaper")
    public String testQuestionPaperId;
    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("TimeTaken")
    public String timeTaken;
    @SerializedName("StartTime")
    public String startTime;
    @SerializedName("EndTime")
    public String endTime;
    @SerializedName("Status")
    public String status;
    @Ignore
    @SerializedName("testanswers")
    public List<TestAnswer> testAnswers = new ArrayList<>();
}
