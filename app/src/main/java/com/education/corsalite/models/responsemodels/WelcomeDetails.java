package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 2/27/16.
 */
public class WelcomeDetails {
    public String photoUrl;
    @SerializedName("FirstName")
    public String firstName;
    @SerializedName("LastName")
    public String lastName;
    public String userLastLoginDate;
    public String vcInLastSession;
    public String vcCount;
}
