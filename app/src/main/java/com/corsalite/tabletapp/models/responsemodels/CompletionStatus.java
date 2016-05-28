package com.corsalite.tabletapp.models.responsemodels;

public class CompletionStatus {
    private String ChapterName;
    private String PendingExerciseTopics;
    private String TimeSpent;
    private String CompletedTopics;
    private String SubjectName;
    private String idCourseSubjectChapter;
    private String idCourseSubject;
    public int statusColor;

    public void setChapterName(String chapterName) {
        this.ChapterName = chapterName;
    }

    public String getChapterName() {
        return ChapterName;
    }

    public void setPendingExerciseTopics(String pendingExerciseTopics) {
        this.PendingExerciseTopics = pendingExerciseTopics;
    }

    public String getPendingExerciseTopics() {
        return PendingExerciseTopics;
    }


    public void setTimeSpent(String timeSpent) {
        this.TimeSpent = timeSpent;
    }

    public String getTimeSpent() {
        return TimeSpent;
    }

    public void setCompletedTopics(String completedTopics) {
        this.CompletedTopics = completedTopics;
    }

    public String getCompletedTopics() {
        return CompletedTopics;
    }

    public void setSubjectName(String subjectName) {
        this.SubjectName = subjectName;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setIdCourseSubject(String idCourseSubject) {
        this.idCourseSubject = idCourseSubject;
    }

    public String getIdCourseSubject() {
        return idCourseSubject;
    }


    public void setIdCourseSubjectChapter(String idCourseSubjectChapter) {
        this.idCourseSubjectChapter = idCourseSubjectChapter;
    }

    public String getIdCourseSubjectChapter() {
        return idCourseSubjectChapter;
    }

}