package com.education.corsalite.models.requestmodels;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vissu on 10/15/15.
 */
public class Defaultcourserequest {

    @SerializedName("UpdateTime")
    public String updateTime;
    @SerializedName("idStudent")
    public String studentId;
    @SerializedName("idCourse")
    public String courseId;
    @SerializedName("DefaultYN")
    public String isDefault;

    public Defaultcourserequest() {
    }

    public Defaultcourserequest(String studentId, String courseId) {
        this.updateTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.studentId = studentId;
        this.courseId = courseId;
        isDefault = "Y";
    }

}
