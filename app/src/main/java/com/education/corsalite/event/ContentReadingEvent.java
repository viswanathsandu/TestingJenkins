package com.education.corsalite.event;

import com.education.corsalite.models.responsemodels.BaseModel;

/**
 * Created by Madhuri on 24-01-2016.
 */
public class ContentReadingEvent extends BaseModel{
    public String id;
    public String pageView;
    public String eventStartTime;
    public String eventEndTime;
}
