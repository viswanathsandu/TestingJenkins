package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

public class Chapter extends BaseModel {
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
    @SerializedName("IdCourseSubjectchapter")
    public String idCourseSubjectchapter;
    @SerializedName("PendingExerciseTopics")
    public String pendingExerciseTopics;
    @SerializedName("CompletedTopics")
    public String completedTopics;
    @SerializedName("ScoreRed")
    public String scoreRed;
    @SerializedName("TimeSpent")
    public String timeSpent;
    @SerializedName("PassedComplexity")
    public Integer passedComplexity;
    @SerializedName("ScoreAmber")
    public String scoreAmber;
    @SerializedName("NotesCount")
    public String notesCount;

    public boolean isChapterOffline = true;

    public Chapter() {}

}