package com.corsalite.tabletapp.models.socket.requests;

import com.corsalite.tabletapp.models.socket.response.ChallengeTestRequestEvent;
import com.corsalite.tabletapp.utils.L;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/1/16.
 */
public class ChallengeTestStartRequestEvent {
    public String event = "ChallengeTestStart";
    @SerializedName("ChallengeTestParentID")
    private Integer challengeTestParentId;
    @SerializedName("ChallengerName")
    public String challengerName;
    @SerializedName("ChallengerStatus")
    public String challengerStatus;
    @SerializedName("ChallengeStatus")
    public String challengeStatus;
    @SerializedName("TestQuestionPaperId")
    public String testQuestionPaperId;

    public void setChallengeTestRequestEvent(ChallengeTestRequestEvent event) {
        setChallengeTestParentId(event.challengeTestParentId);
        this.challengerName = event.challengerName;
    }

    public void setChallengeTestParentId(String challengeId) {
        try {
            challengeTestParentId = Integer.valueOf(challengeId);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            challengeTestParentId = null;
        }
    }
}
