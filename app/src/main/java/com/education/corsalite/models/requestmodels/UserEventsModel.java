package com.education.corsalite.models.requestmodels;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Madhuri on 24-01-2016.
 */
public class UserEventsModel extends BaseModel {
    @SerializedName("UserID")
    public String idUser;
    @SerializedName("EventStartTime")
    public String eventStartTime;
    @SerializedName("EventEndTime")
    public String eventEndTime;
    @SerializedName("EventName")
    public String eventName;
    @SerializedName("EventSourceId")
    public String eventSourceId;
    @SerializedName("PageView")
    public String pageView;
}
