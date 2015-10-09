package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

public class Chapters {
    @SerializedName("ChapterName")
    public String chapterName;
    @SerializedName("TotalTopics")
    public String totalTopics;
    @SerializedName("TimeTaken")
    public String timeTaken;
    @SerializedName("TotalTestedMarks")
    public Integer totalTestedMarks;
    public String idCourseSubject;
    public String idCourseSubjectChapter;
    @SerializedName("EarnedMarks")
    public Integer earnedMarks;
    @SerializedName("PendingExerciseTopics")
    public Integer pendingExerciseTopics;
    @SerializedName("CompletedTopics")
    public Integer completedTopics;
    @SerializedName("ScoreRed")
    public Integer scoreRed;
    @SerializedName("TimeSpent")
    public Integer timeSpent;
    @SerializedName("PassedComplexity")
    public Integer passedComplexity;
    @SerializedName("ScoreAmber")
    public Integer scoreAmber;
    @SerializedName("notesCount")
    public Integer notesCount;

}