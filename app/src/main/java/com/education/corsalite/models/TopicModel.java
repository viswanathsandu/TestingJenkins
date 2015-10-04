package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.Content;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by Girish on 02/10/15.
 */
public class TopicModel extends BaseModel implements Comparable<TopicModel> {

    public String idTopic ;
    public String topicName;
    public String topicStatus;
    public String topicSortOrder;
    public HashMap<String, SortedSet<ContentModel>> contentMap;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopicModel that = (TopicModel) o;

        if (idTopic != null ? !idTopic.equals(that.idTopic) : that.idTopic != null) return false;
        if (topicName != null ? !topicName.equals(that.topicName) : that.topicName != null)
            return false;
        if (topicStatus != null ? !topicStatus.equals(that.topicStatus) : that.topicStatus != null)
            return false;
        if (topicSortOrder != null ? !topicSortOrder.equals(that.topicSortOrder) : that.topicSortOrder != null)
            return false;
        return !(contentMap != null ? !contentMap.equals(that.contentMap) : that.contentMap != null);

    }

    @Override
    public int hashCode() {
        int result = idTopic != null ? idTopic.hashCode() : 0;
        result = 31 * result + (topicName != null ? topicName.hashCode() : 0);
        result = 31 * result + (topicStatus != null ? topicStatus.hashCode() : 0);
        result = 31 * result + (topicSortOrder != null ? topicSortOrder.hashCode() : 0);
        result = 31 * result + (contentMap != null ? contentMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TopicModel{" +
                "idTopic='" + idTopic + '\'' +
                ", topicName='" + topicName + '\'' +
                ", topicStatus='" + topicStatus + '\'' +
                ", topicSortOrder='" + topicSortOrder + '\'' +
                ", contentMap=" + contentMap +
                '}';
    }

    @Override
    public int compareTo(TopicModel another) {
        return Integer.valueOf(this.topicSortOrder) - Integer.valueOf(another.topicSortOrder);
    }
}
