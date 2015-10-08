package com.education.corsalite.models.responsemodels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayush on 08/10/15.
 */
public class CourseData {
    public List<StudyCenter> StudyCenter;
    public List<Chapters> redListChapters = new ArrayList<>();
    public List<Chapters> amberListChapters = new ArrayList<>();
    public List<Chapters> greenListChapters = new ArrayList<>();
    public List<Chapters> blueListChapters = new ArrayList<>();

}
