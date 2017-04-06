package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/5/17.
 */

public class TestSeriesMockData extends BaseModel {

    @SerializedName("TestType")
    public String testType;
    @SerializedName("TestName")
    public String testName;
    @SerializedName("idExamTemplate")
    public String idExamTemplate;
    @SerializedName("idCourseSubject")
    public String subjectId;
    @SerializedName("TestCountTaken")
    public String testCountTaken;
    @SerializedName("TestCountAllowed")
    public String testCountAllowed;
    @SerializedName("NumberOfQuestionsPerTestLimit")
    public String numberOfQuestionsPerTestLimit;
    @SerializedName("EarnedScore")
    public String earnedScore;
    @SerializedName("TotalTestedMarks")
    public String totalTestedMarks;
    @SerializedName("TimeTaken")
    public String timeTaken;

}
