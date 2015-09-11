package com.education.corsalite.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/11/15.
 */
public class LoginResponse extends BaseModel{
    @SerializedName("idUser")
    public String userId;
    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("idEntity")
    public String entitiyId;
    @SerializedName("AuthToken")
    public String authtoken;


    @Override
    public String toString() {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}