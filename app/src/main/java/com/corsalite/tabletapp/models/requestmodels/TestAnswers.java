package com.corsalite.tabletapp.models.requestmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mt0060 on 20/12/15.
 */
public class TestAnswers {

    public String idTestQuestion;
    public String idAnswerKey;
    @SerializedName("AnswerText")
    public String answerText;
    @SerializedName("Status")
    public String status;
    public int sortOrder;
    @SerializedName("TimeTaken")
    public String timeTaken;


}
