package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vissu on 4/2/16.
 */
public class ChallengeUserListResponse extends BaseModel {
    @SerializedName("idExam")
    public String examId;
    public List<ChallengeUser> challengeUsersList;
}
