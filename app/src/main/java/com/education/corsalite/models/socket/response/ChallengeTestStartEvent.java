package com.education.corsalite.models.socket.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/1/16.
 */
public class ChallengeTestStartEvent {
    public final String event = "ChallengeTestStart";
    @SerializedName("ChallengeTestParentID")
    public String challengeTestParentId;
    @SerializedName("ChallengerName")
    public String challengerName;
    @SerializedName("ChallengerStatus")
    public String challengerStatus;
    @SerializedName("TestQuestionPaperId")
    public String testQuestionPaperId;
}
