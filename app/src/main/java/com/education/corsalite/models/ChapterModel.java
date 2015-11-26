package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by mt0060 on 03/10/15.
 */
public class ChapterModel extends BaseModel implements Comparable<ChapterModel>, Serializable{

    @SerializedName("idCourseSubjectChapter")
    public String idChapter;
    @SerializedName("ChapterName")
    public String chapterName;
    @SerializedName("ChapterStatus")
    public String chapterStatus;
    @SerializedName("ChapterSortOrder")
    public String chapterSortOrder;

    @SerializedName("topics")
    public List<TopicModel> topicMap;

    @Override
    public int compareTo(ChapterModel another) {
        return Integer.valueOf(this.chapterSortOrder) - Integer.valueOf(another.chapterSortOrder);
    }
}
