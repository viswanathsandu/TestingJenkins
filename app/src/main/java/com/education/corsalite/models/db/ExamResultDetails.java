package com.education.corsalite.models.db;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.utils.L;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vissu on 11-08-2017.
 */

public class ExamResultDetails extends BaseModel {

    @SerializedName("TestName")
    public String examName;
    @SerializedName("TemplateType")
    public String templateType;
    @SerializedName("TimeTaken")
    public String timeTaken;
    @SerializedName("examDate")
    public String examDate;
    @SerializedName("Rank")
    public String rank;

    public String getMonthYear() {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(examDate);
            return new SimpleDateFormat("MMM yyyy").format(date);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return "";
        }
    }

    public String getDate() {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(examDate);
            return new SimpleDateFormat("dd").format(date);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return "";
        }
    }

    public String getTime() {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(examDate);
            return new SimpleDateFormat("hh:mm a").format(date);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return "";
        }
    }

}
