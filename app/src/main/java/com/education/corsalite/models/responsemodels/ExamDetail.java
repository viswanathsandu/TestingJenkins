package com.education.corsalite.models.responsemodels;

import android.util.Log;

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

    public boolean isExamDetailsAvailable() {
        Log.v("","Date1:"+examDate +":::"+hallTicketNumber);
        if (hallTicketNumber != null && examDate!=null){
            Log.v("","Date2:"+examDate +":::"+hallTicketNumber);
            if(hallTicketNumber.isEmpty() || examDate.isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
