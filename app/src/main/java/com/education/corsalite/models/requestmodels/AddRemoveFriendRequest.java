package com.education.corsalite.models.requestmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 5/31/16.
 */

public class AddRemoveFriendRequest {

    @SerializedName("idUser")
    public String userId;
    @SerializedName("idFriendUser")
    public String friendUserId;
    @SerializedName("Request")
    public String request;

    public AddRemoveFriendRequest() {}

    public AddRemoveFriendRequest(String userId, String friendUserId, String request) {
        this.userId = userId;
        this.friendUserId = friendUserId;
        this.request = request;
    }

}
