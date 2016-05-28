package com.corsalite.tabletapp.models.responsemodels;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vissu on 9/12/15.
 */
public class Course extends BaseModel  implements Serializable {

    @SerializedName("idCourse")
    public Integer courseId;
    @SerializedName("Name")
    public String name;
    @SerializedName("DefaultYN")
    public String isDefault;

    public boolean isDefault() {
        return (!TextUtils.isEmpty(isDefault) && isDefault.equals("Y"));
    }

    @Override
    public String toString() {
        return name;
    }
}
