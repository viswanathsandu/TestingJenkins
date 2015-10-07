package com.education.corsalite.models.responsemodels;

import java.util.List;

public class StudyCenter {
    private String SubjectName;
    private int idCourseSubject;
    private List<Chapters> Chapters;

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
    }

    public int getIdCourseSubject() {
        return idCourseSubject;
    }

    public void setIdCourseSubject(int idCourseSubject) {
        this.idCourseSubject = idCourseSubject;
    }

    public List<Chapters> getChapters() {
        return Chapters;
    }

    public void setChapters(List<Chapters> chapters) {
        this.Chapters = chapters;
    }
}