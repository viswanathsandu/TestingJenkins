package com.education.corsalite.models.requestmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Girish on 30/12/2015.
 */
public class FlaggedQuestionModel {

    @SerializedName("UpdateTime")
    public String updateTime;
    @SerializedName("idTestQuestion")
    public String idTestQuestion;
    @SerializedName("idTestAnswerPaper")
    public String idTestAnswerPaper;
    @SerializedName("FlaggedYN")
    public String flaggedYN;
}
