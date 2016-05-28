package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.List;

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
    @Ignore
    @SerializedName("contents")
    public List<ContentModel> contentMap;
    public boolean checked;
    public boolean htmlChecked;
    public boolean videoChecked;

    @Override
    public int compareTo(TopicModel another) {
        try {
            return Integer.valueOf(this.topicSortOrder) - Integer.valueOf(another.topicSortOrder);
        } catch (NullPointerException e) {
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
