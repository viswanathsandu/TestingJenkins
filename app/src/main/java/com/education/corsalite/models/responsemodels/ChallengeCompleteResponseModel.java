package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by vissu on 4/12/16.
 */
public class ChallengeCompleteResponseModel extends BaseResponseModel {
    @Ignore
    @SerializedName("leaderBoardData")
    public List<LeaderBoardUser> leaderBoardUsers;
    public Integer examRemainUsers;
    public String idTestAnswerPaper;
    @SerializedName("challengeParentId")
    public String challengeTestId;

    public ChallengeCompleteResponseModel() {
    }
}
