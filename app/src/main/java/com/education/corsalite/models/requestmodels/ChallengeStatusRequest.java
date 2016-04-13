package com.education.corsalite.models.requestmodels;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/10/16.
 */
public class ChallengeStatusRequest extends BaseModel {

    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("idChallengeTest")
    public String challengeTestId;
    @SerializedName("challengestatus")
    public String challengeStatus;

    public ChallengeStatusRequest(String studentId, String challengeTestId, boolean accepted) {
        this.studentId = studentId;
        this.challengeTestId = challengeTestId;
        this.challengeStatus = accepted ? "Accepted" : "Declined";
    }
}
