package com.corsalite.tabletapp.models.responsemodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by vissu on 4/2/16.
 */
public class ChallengeUserListResponse extends BaseModel {
    @SerializedName("idExam")
    public String examId;
    @Ignore
    public List<ChallengeUser> challengeUsersList;
}
