package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 4/27/16.
 */
public class RecommendedModel extends BaseResponseModel {

    @SerializedName("idSubject")
    public String subjectId;
    @SerializedName("SubjectName")
    public String subjectName;
    @SerializedName("idChapter")
    public String chapterId;
    @SerializedName("ChapterName")
    public String chapterName;
    @SerializedName("idTopic")
    public String topicId;
    @SerializedName("TopicName")
    public String topicName;
    @SerializedName("RecentTestDate")
    public String recentTestDate;
    @SerializedName("TotalTestedMarks")
    public String totalTestedMarks;
    @SerializedName("Accuracy")
    public String accuracy;
    @SerializedName("Speed")
    public String speed;

    public RecommendedModel() {
    }
}
