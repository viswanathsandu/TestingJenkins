package com.corsalite.tabletapp.models.responsemodels;

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
    // TODO : default value has to be modified. It should be Unattended. Once the issue is fixed on server end, this can be changed
    @SerializedName("Status")
    public String status = "Unattempted";
    @SerializedName("sortOrder")
    public String sortOrder = "1";
    @SerializedName("TimeTaken")
    public String timeTaken = "0";
}
