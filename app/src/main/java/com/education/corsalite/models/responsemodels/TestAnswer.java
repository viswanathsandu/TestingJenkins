package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 3/7/16.
 */
public class TestAnswer {

    @SerializedName("idTestQuestion")
    public String testQuestionId;
    @SerializedName("idAnswerKey")
    public String answerKeyId;
    @SerializedName("AnswerText")
    public String answerText;
    @SerializedName("Status")
    public String status;
    @SerializedName("sortOrder")
    public String sortOrder = "1";
    @SerializedName("TimeTaken")
    public String timeTaken;
}
