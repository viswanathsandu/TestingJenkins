package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

public class Chapters {
    @SerializedName("ChapterName")
    public String chapterName;
    @SerializedName("TotalTopics")
    public String totalTopics;
    @SerializedName("TimeTaken")
    public String timeTaken;
    @SerializedName("EarnedMarks")
    public String earnedMarks;
    @SerializedName("TotalTestedMarks")
    public String totalTestedMarks;
    public String idCourseSubject;
    public String idCourseSubjectChapter;
    @SerializedName("PendingExerciseTopics")
    public String pendingExerciseTopics;
    @SerializedName("CompletedTopics")
    public String completedTopics;
    @SerializedName("ScoreRed")
    public String scoreRed;
    @SerializedName("TimeSpent")
    public String timeSpent;
    @SerializedName("PassedComplexity")
    public String passedComplexity;
    @SerializedName("ScoreAmber")
    public String scoreAmber;
    @SerializedName("notesCount")
    public String notesCount;

}