package com.education.corsalite.models.socket.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 3/30/16.
 */
public class UserListEvent {

    public final String event = "getuserslist";
    @SerializedName("idStudent")
    public String studentId;

    public UserListEvent(String studentId) {
        this.studentId = studentId;
    }
}
