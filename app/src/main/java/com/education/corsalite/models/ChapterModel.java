package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Girish on 03/10/15.
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

    @Ignore
    @SerializedName("topics")
    public List<TopicModel> topicMap;

    @Override
    public int compareTo(ChapterModel another) {
        try {
            return Integer.valueOf(this.chapterSortOrder) - Integer.valueOf(another.chapterSortOrder);
        } catch (NullPointerException e) {
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
