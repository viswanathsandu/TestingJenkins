package com.corsalite.tabletapp.models.requestmodels;

import com.corsalite.tabletapp.models.responsemodels.BaseModel;
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

    public ChallengeStatusRequest(String studentId, String challengeTestId, String status) {
        this.studentId = studentId;
        this.challengeTestId = challengeTestId;
        this.challengeStatus = status;
    }
}
