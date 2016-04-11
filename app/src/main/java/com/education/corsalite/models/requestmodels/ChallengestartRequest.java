package com.education.corsalite.models.requestmodels;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/11/16.
 */
public class ChallengestartRequest extends BaseModel {
    @SerializedName("idChallengeTest")
    public String challengeTestId;
    @SerializedName("idExam")
    public String examId;

    public ChallengestartRequest(String challengeTestId, String idExam) {
        this.challengeTestId = challengeTestId;
        this.examId = examId;
    }
}
