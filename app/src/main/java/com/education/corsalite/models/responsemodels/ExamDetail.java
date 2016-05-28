package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vissu on 9/12/15.
 */
public class ExamDetail extends BaseModel implements Serializable {
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
