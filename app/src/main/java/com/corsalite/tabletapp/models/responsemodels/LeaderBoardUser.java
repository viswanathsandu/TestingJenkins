package com.corsalite.tabletapp.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/12/16.
 */
public class LeaderBoardUser extends BaseModel {
    @SerializedName("DisplayName")
    public String displayName;
    @SerializedName("PhotoUrl")
    public String photoUrl;
    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("stuScore")
    public String studentScore;
    @SerializedName("answerdQues")
    public String answeredQuestions;
}
