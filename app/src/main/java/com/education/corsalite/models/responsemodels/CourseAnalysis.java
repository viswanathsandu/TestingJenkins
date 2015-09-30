package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aastha on 30/09/15.
 */
public class CourseAnalysis {

    @SerializedName("ScoreRed")
    public String scoreRed;

    @SerializedName("ScoreAmber")
    public String scoreAmber;

    @SerializedName("idCourseSubject")
    public String idCourseSubject;

    @SerializedName("SubjectName1")
    public String subjectName;

    @SerializedName("idCourseSubjectChapter")
    public String idCourseSubjectChapter;

    @SerializedName("ChapterName1")
    public String chapterName;

    @SerializedName("idTopic")
    public String idTopic;

    @SerializedName("Topic1")
    public String topic;

    @SerializedName("Date1")
    public String date;

    @SerializedName("TotalTopics")
    public String totalTopics;

    @SerializedName("RecentTestDate")
    public String recentTestDate;

    @SerializedName("TotalTestedMarks")
    public String totalTestedMarks;

    @SerializedName("EarnedMarks")
    public String earnedMarks;

    @SerializedName("TimeTaken")
    public String timeTaken;

    @SerializedName("Accuracy")
    public String accuracy;
    @SerializedName("Speed")
    public String speed;

}
