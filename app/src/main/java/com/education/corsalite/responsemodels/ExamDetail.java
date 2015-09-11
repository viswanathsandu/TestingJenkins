package com.education.corsalite.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/12/15.
 */
public class ExamDetail {
    @SerializedName("idExam")
    public String examId;
    @SerializedName("Name")
    public String name;
    @SerializedName("HallTicketNumber")
    public String hallTicketNumber;
    @SerializedName("ExamDate")
    public String examDate;
    @SerializedName("DaysRemaining")
    public Integer daysRemaining;
}
