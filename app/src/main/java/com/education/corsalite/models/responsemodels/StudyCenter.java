package com.education.corsalite.models.responsemodels;

import java.util.ArrayList;
import java.util.List;

public class StudyCenter {
    public String SubjectName;
    public Integer idCourseSubject;
    public List<Chapters> Chapters;
    public List<Chapters> redListChapters = new ArrayList<>();
    public List<Chapters> amberListChapters = new ArrayList<>();
    public List<Chapters> greenListChapters = new ArrayList<>();
    public List<Chapters> blueListChapters = new ArrayList<>();

    public void resetColoredLists() {
        redListChapters.clear();
        greenListChapters.clear();
        amberListChapters.clear();
        blueListChapters.clear();
    }

}