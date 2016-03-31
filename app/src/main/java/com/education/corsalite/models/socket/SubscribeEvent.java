package com.education.corsalite.models.socket;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 3/30/16.
 */
public class SubscribeEvent extends BaseModel {

    public final String event = "subscribe";
    @SerializedName("idStudent")
    public String studentId;

    public SubscribeEvent(String studentId) {
        this.studentId = studentId;
    }
}
