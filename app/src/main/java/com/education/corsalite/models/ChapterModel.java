package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by mt0060 on 03/10/15.
 */
public class ChapterModel extends BaseModel implements Comparable<ChapterModel>{

    public String idChapter;
    public String chapterName;
    public String chapterStatus;
    public String chapterSortOrder;
    public HashMap<String, SortedSet<TopicModel>> topicMap;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChapterModel that = (ChapterModel) o;

        if (idChapter != null ? !idChapter.equals(that.idChapter) : that.idChapter != null)
            return false;
        if (chapterName != null ? !chapterName.equals(that.chapterName) : that.chapterName != null)
            return false;
        if (chapterStatus != null ? !chapterStatus.equals(that.chapterStatus) : that.chapterStatus != null)
            return false;
        if (chapterSortOrder != null ? !chapterSortOrder.equals(that.chapterSortOrder) : that.chapterSortOrder != null)
            return false;
        return !(topicMap != null ? !topicMap.equals(that.topicMap) : that.topicMap != null);

    }

    @Override
    public int hashCode() {
        int result = idChapter != null ? idChapter.hashCode() : 0;
        result = 31 * result + (chapterName != null ? chapterName.hashCode() : 0);
        result = 31 * result + (chapterStatus != null ? chapterStatus.hashCode() : 0);
        result = 31 * result + (chapterSortOrder != null ? chapterSortOrder.hashCode() : 0);
        result = 31 * result + (topicMap != null ? topicMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChapterModel{" +
                "idChapter='" + idChapter + '\'' +
                ", chapterName='" + chapterName + '\'' +
                ", chapterStatus='" + chapterStatus + '\'' +
                ", chapterSortOrder='" + chapterSortOrder + '\'' +
                ", topicMap=" + topicMap +
                '}';
    }

    @Override
    public int compareTo(ChapterModel another) {
        return Integer.valueOf(this.chapterSortOrder) - Integer.valueOf(another.chapterSortOrder);
    }
}
