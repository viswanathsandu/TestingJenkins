package com.education.corsalite.models.responsemodels;

import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by ayush on 08/10/15.
 */
public class CourseData {
    @Ignore
    public List<StudyCenter> StudyCenter;
}
