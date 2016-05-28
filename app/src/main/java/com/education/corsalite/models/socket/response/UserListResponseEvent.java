package com.education.corsalite.models.socket.response;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 3/30/16.
 */
public class UserListResponseEvent extends BaseModel {

    public final String event = "Userslist";
    @SerializedName("Users")
    public String usersTxt;
}
