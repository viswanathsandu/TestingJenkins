package com.education.corsalite.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 9/12/15.
 */
public class ExamDetail extends BaseModel{
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
