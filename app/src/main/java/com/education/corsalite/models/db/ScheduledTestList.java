package com.education.corsalite.models.db;

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

    public ScheduledTestList() {
    }

}

