package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Phani on 20/02/16.
 */
public class FriendsData extends BaseModel {

    @SerializedName("friendsList")
    public ArrayList<Friend> friendsList;
    @SerializedName("maxVCForChallenge")
    public String maxVCForChallenge;

    public class Friend extends BaseModel {
        @SerializedName("studentVC")
        public String studentVC;
        @SerializedName("idUser")
        public String idUser;
        @SerializedName("PhotoUrl")
        public String photoUrl;
        @SerializedName("DisplayName")
        public String displayName;
        @SerializedName("EmailID")
        public String emailID;
    }
}
