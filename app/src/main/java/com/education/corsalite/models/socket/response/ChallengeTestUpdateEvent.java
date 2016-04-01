package com.education.corsalite.models.socket.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/1/16.
 */
public class ChallengeTestUpdateEvent {
    public String event;
    @SerializedName("ChallengeTestParentID")
    public String challengeTestParentId;
    @SerializedName("ChallengerName")
    public String challengerName;
    @SerializedName("ChallengerStatus")
    public String challengerStatus;

    public ChallengeTestUpdateEvent(String event, String challengeTestParentId, String challengeName, String challengerStatus) {
        this.event = event;
        this.challengeTestParentId = challengeTestParentId;
        this.challengerName = challengeName;
        this.challengerStatus = challengerStatus;
    }
}
