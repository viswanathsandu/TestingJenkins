package com.corsalite.tabletapp.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/11/15.
 */
public class CorsaliteError {
    @SerializedName("Status")
    public String status;
    @SerializedName("Message")
    public String message;
}
