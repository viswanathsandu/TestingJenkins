package com.corsalite.tabletapp.models.responsemodels;

import com.corsalite.tabletapp.utils.L;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Aastha on 09/11/15.
 */
public class ExamHistory {

    @SerializedName("idTestAnswerPaper")
    public String idTestAnswerPaper;
    @SerializedName("StartTime")
    private String dateTime;
    @SerializedName("TestName")
    public String examName;
    @SerializedName("TemplateType")
    public String testType;
    @SerializedName("Rank")
    public String rank;
    @SerializedName("Status")
    public String status;
    @SerializedName("TotalScore")
    public String totalScore;

    public String getTime() {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
            return new SimpleDateFormat("dd/MM/yyyy\nhh:mm a").format(date);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return "";
        }
    }
}
