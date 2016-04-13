package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/11/16.
 */
public class ChallengeStartResponseModel extends BaseResponseModel {
    @SerializedName("idTestQuestionPaper")
    public String testQuestionPaperId;
}
