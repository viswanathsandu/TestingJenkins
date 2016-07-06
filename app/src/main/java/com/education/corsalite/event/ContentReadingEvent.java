package com.education.corsalite.event;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Madhuri on 24-01-2016.
 */
public class ContentReadingEvent extends BaseModel{
    public String idContent;
    public String idStudent;
    @SerializedName("StartTime")
    public String eventStartTime;
    @SerializedName("EndTime")
    public String eventEndTime;
    @SerializedName("UpdateTime")
    public String updatetime;
}
