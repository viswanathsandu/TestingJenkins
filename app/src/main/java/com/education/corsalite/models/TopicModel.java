package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.Content;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by Girish on 02/10/15.
 */
public class TopicModel extends BaseModel implements Comparable<TopicModel>, Serializable {

    public String idTopic ;
    @SerializedName("TopicName")
    public String topicName;
    @SerializedName("TopicStatus")
    public String topicStatus;
    @SerializedName("TopicSortOrder")
    public String topicSortOrder;
    @SerializedName("contents")
    public List<ContentModel> contentMap;
    public boolean checked;
    public boolean htmlChecked;
    public boolean videoChecked;

    @Override
    public int compareTo(TopicModel another) {
        return Integer.valueOf(this.topicSortOrder) - Integer.valueOf(another.topicSortOrder);
    }
}
