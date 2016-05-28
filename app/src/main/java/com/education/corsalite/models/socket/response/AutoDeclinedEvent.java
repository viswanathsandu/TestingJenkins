package com.education.corsalite.models.socket.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/1/16.
 */
public class AutoDeclinedEvent {
    public String event;
    @SerializedName("ChallengeTestParentID")
    public String challengeTestParentId;
    @SerializedName("isOtherChallengers")
    public String isOtherChallengers;

    public AutoDeclinedEvent(String event, String challengeTestParentId,String isOtherChallengers) {
        this.event = event;
        this.challengeTestParentId = challengeTestParentId;
        this.isOtherChallengers = isOtherChallengers;
    }
}
