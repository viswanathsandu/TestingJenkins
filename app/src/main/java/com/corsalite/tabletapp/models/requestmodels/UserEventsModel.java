package com.corsalite.tabletapp.models.requestmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Madhuri on 24-01-2016.
 */
public class UserEventsModel {
    @SerializedName("UserID")
    public String userId;
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
