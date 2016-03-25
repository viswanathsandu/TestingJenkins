package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 3/24/16.
 */
public class PartTestGridElement {
    public String idCourseSubjectChapter;
    @SerializedName("ChapterName")
    public String chapterName;
    @SerializedName("IsRecommended")
    public String isRecommended;
    @SerializedName("QuestionCount")
    public int questionCount;
    @SerializedName("RecommendedQuestionCount")
    public int recommendedQuestionCount;
}
