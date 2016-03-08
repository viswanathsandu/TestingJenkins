package com.education.corsalite.models;

/**
 * Created by KANDAGATLAS on 14-02-2016.
 */

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created on 23/01/16.
 *
 * @author Meeth D Jain
 */
public class ScheduledTestList extends BaseModel implements Serializable {

    public ArrayList<ScheduledTestsArray> MockTest;
    @SerializedName("Status")
    public String Status;
    @SerializedName("Message")
    public String Message;

    public ScheduledTestList() {}

    public class ScheduledTestsArray extends BaseModel implements Serializable {
        @SerializedName("ScheduledDueDate")
        public String dueDate;
        @SerializedName("ExamName")
        public String examName;
        @SerializedName("ScheduleStartTime")
        public String startTime;
        @SerializedName("idTestQuestionPaper")
        public String testQuestionPaperId;

        public ScheduledTestsArray() {}
    }

}

