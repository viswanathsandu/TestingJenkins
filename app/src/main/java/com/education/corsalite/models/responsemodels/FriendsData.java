package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Phani on 20/02/16.
 */
public class FriendsData extends BaseModel implements Serializable{

    @SerializedName("friendsList")
    public ArrayList<Friends> friendsList;
    @SerializedName("maxVCForChallenge")
    public String maxVCForChallenge;

    public class Friends extends BaseModel implements Serializable{
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
