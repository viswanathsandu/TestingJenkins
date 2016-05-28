package com.corsalite.tabletapp.models.socket.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/1/16.
 */
public class ResponseEvent {
    public String event;
    @SerializedName("Users")
    public String users; // seperated with comma(,)
    @SerializedName("ChallengeTestParentID")
    public String challengeTestParentId;
    @SerializedName("ChallengerName")
    public String challengerName;
    @SerializedName("ChallengerStatus")
    public String challengerStatus;
    @SerializedName("challengeParentStudentId")
    public String challengeParentStudentId;
    @SerializedName("TestQuestionPaperId")
    public String testQuestionPaperId;
    @SerializedName("isOtherChallengers")
    public String isOtherChallengers;
    @SerializedName("LeaderBoardTxt")
    public String leaderBoardTxt;
}
