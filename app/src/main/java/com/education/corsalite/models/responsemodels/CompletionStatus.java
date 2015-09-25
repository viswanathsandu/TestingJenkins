package com.education.corsalite.models.responsemodels;

public class CompletionStatus {
    private String chaptername;
    private String pendingexercisetopics;
    private String timespent;
    private String completedtopics;
    private String subjectname;
    private String idcoursesubjectchapter;
    private String idcoursesubject;

    public void setChaptername(String chaptername) {
        this.chaptername = chaptername;
    }

    public String getChaptername() {
        return chaptername;
    }

    public void setPendingexercisetopics(String pendingexercisetopics) {
        this.pendingexercisetopics = pendingexercisetopics;
    }

    public String getPendingexercisetopics() {
        return pendingexercisetopics;
    }


    public void setTimespent(String timespent) {
        this.timespent = timespent;
    }

    public String getTimespent() {
        return timespent;
    }

    public void setCompletedtopics(String completedtopics) {
        this.completedtopics = completedtopics;
    }

    public String getCompletedtopics() {
        return completedtopics;
    }

    public void setSubjectname(String subjectname) {
        this.subjectname = subjectname;
    }

    public String getSubjectname() {
        return subjectname;
    }

    public void setIdcoursesubject(String idcoursesubject) {
        this.idcoursesubject = idcoursesubject;
    }

    public String getIdcoursesubject() {
        return idcoursesubject;
    }


    public void setIdcoursesubjectchapter(String idcoursesubjectchapter) {
        this.idcoursesubjectchapter = idcoursesubjectchapter;
    }

    public String getIdcoursesubjectchapter() {
        return idcoursesubjectchapter;
    }

}