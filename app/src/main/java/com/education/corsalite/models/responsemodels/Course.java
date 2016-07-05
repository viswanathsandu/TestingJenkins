package com.education.corsalite.models.responsemodels;

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
    @SerializedName("EndDate")
    public String endDate;
    @SerializedName("IsCourseEnded")
    public String isCourseEnded;

    public boolean isDefault() {
        return (!TextUtils.isEmpty(isDefault) && isDefault.equals("Y"));
    }

    public boolean isEnded() {
        return (!TextUtils.isEmpty(isCourseEnded) && isCourseEnded.equalsIgnoreCase("Y"));
    }

    @Override
    public String toString() {
        return name;
    }
}
