package com.corsalite.tabletapp.models.socket.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 3/30/16.
 */
public class SubscribeEvent {

    public final String event = "subscribe";
    @SerializedName("idStudent")
    public String studentId;

    public SubscribeEvent(String studentId) {
        this.studentId = studentId;
    }
}
