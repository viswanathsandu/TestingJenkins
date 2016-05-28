package com.corsalite.tabletapp.models.socket.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/1/16.
 */
public class NewChallengeTestRequestEvent {
    public String event = "ChallengeTestRequest";
    @SerializedName("ChallengeTestParentID")
    public String challengeTestParentId;
    @SerializedName("ChallengerName")
    public String challengerName;
}
