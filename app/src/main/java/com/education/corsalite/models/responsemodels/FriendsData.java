package com.education.corsalite.models.responsemodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

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
        @Ignore
        @SerializedName("UserType")
        public String userType;
        @SerializedName("idStudent")
        public String idStudent;
        @SerializedName("PhotoUrl")
        public String photoUrl;
        @SerializedName("DisplayName")
        public String displayName;
        @SerializedName("EmailID")
        public String emailID;
        @SerializedName("friendRequest")
        public String friendRequest;
        public boolean isOnline = false;
        public String status = "";

        @Override
        public boolean equals(Object obj) {
            return idUser != null && obj != null && obj instanceof Friend
                    && ((Friend) obj).idUser.equalsIgnoreCase(idUser);
        }

        public boolean isOnline() {
            return isOnline || emailID.equalsIgnoreCase("corbot@corsalite.com");
        }

        public boolean isRobot() {
            return !TextUtils.isEmpty(userType) && userType.equalsIgnoreCase("Robot");
        }
    }
}
