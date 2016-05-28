package com.corsalite.tabletapp.models.requestmodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by Girish on 19/12/15.
 */
public class PostTestAnswerPaper {

    public String idTestQuestionPaper;
    public String idStudent;
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
    public List<TestAnswers> testAnswersList;
}
