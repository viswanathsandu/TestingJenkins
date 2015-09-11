package com.education.corsalite.requestmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/12/15.
 */
public class LogoutModel {
    @SerializedName("Action")
    public final String action = "LOGOUT";
    public String AuthToken;
}
