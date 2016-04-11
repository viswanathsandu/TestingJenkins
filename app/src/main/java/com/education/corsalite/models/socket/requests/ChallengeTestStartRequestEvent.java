package com.education.corsalite.models.socket.requests;

import com.education.corsalite.models.socket.response.ChallengeTestRequestEvent;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/1/16.
 */
public class ChallengeTestStartRequestEvent {
    public String event = "ChallengeTestStart";
    @SerializedName("ChallengeTestParentID")
    public String challengeTestParentId;
    @SerializedName("ChallengerName")
    public String challengerName;
    @SerializedName("ChallengerStatus")
    public String challengerStatus;
    @SerializedName("TestQuestionPaperId")
    public String testQuestionPaperId;

    public void setChallengeTestRequestEvent(ChallengeTestRequestEvent event) {
        this.challengeTestParentId = event.challengeTestParentId;
        this.challengerName = event.challengerName;
    }
}
