package com.education.corsalite.models.responsemodels;

import com.education.corsalite.utils.L;
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
    @SerializedName("idTestQuestionPaper")
    public String idTestQuestionPaper;
    @SerializedName("DueDateTime")
    public String dueDate;

    public String getTime() {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
            return new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(date);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return "";
        }
    }
}
