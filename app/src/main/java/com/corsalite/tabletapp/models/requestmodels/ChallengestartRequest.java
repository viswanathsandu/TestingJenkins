package com.corsalite.tabletapp.models.requestmodels;

import com.corsalite.tabletapp.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/11/16.
 */
public class ChallengestartRequest extends BaseModel {
    @SerializedName("idChallengeTest")
    public String challengeTestId;
    @SerializedName("idExam")
    public String examId;

    public ChallengestartRequest(String challengeTestId, String examId) {
        this.challengeTestId = challengeTestId;
        this.examId = examId;
    }
}
