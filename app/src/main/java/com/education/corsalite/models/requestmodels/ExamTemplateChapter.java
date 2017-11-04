package com.education.corsalite.models.requestmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Girish on 19/12/15.
 */
public class ExamTemplateChapter {

    @SerializedName("ChapterID")
    public String chapterID;
    @SerializedName("TopicIDs")
    public String topicIDs;
    @SerializedName("QuestionCount")
    public String questionCount;
    @SerializedName("ComplexityFrom")
    public String complexityFrom;
    @SerializedName("ComplexityTo")
    public String complexityTo;

    public ExamTemplateChapter() {
    }

    public ExamTemplateChapter(String chapterId, String questionCount) {
        this.chapterID = chapterId;
        this.questionCount = questionCount;
    }

}
