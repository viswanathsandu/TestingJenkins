package com.education.corsalite.models.socket.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/1/16.
 */
public class ChallengeTestCompletedEvent {
    public String event = "ChallengeTestComplete";
    @SerializedName("ChallengeTestParentID")
    public String challengeTestParentId;
    @SerializedName("ChallengerName")
    public String challengerName;

    public ChallengeTestCompletedEvent(String challengeTestParentId, String challengeName) {
        this.challengeTestParentId = challengeTestParentId;
        this.challengerName = challengeName;
    }
}
