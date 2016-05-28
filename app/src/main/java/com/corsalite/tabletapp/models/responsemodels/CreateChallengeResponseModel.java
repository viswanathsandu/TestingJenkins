package com.corsalite.tabletapp.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 3/16/16.
 */
public class CreateChallengeResponseModel extends BaseResponseModel {

    @SerializedName("idChallengeTest")
    public String challengeTestId;
    @SerializedName("idTestQuestionPaper")
    public String testQuestionPaperId;

    public CreateChallengeResponseModel() {
    }
}
