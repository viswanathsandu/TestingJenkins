package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/12/15.
 */
public abstract class BaseResponseModel extends BaseModel {
    @SerializedName("Status")
    public String status;
    @SerializedName("Message")
    public String message;

    public boolean isSuccessful() {
        return (status == null || !status.equalsIgnoreCase("ERROR"));
    }
}
