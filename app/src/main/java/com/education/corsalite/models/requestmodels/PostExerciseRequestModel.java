package com.education.corsalite.models.requestmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Girish on 21/11/15.
 */
public class PostExerciseRequestModel {

    @SerializedName("UpdateTime")
    public String updateTime;
    @SerializedName("idStudent")
    public String idStudent;
    @SerializedName("idQuestion")
    public String idQuestion;
    @SerializedName("StudentAnswerChoice")
    public String studentAnswerChoice;
    @SerializedName("Score")
    public String score;
}
