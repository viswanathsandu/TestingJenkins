package com.education.corsalite.models.db;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by vissu on 9/17/15.
 */
public class CourseList extends BaseModel {
    @Ignore
    public List<String> courses;
    public Integer defaultCourseIndex;

    public CourseList() {}

    public CourseList(List<String> courses) {
        this.courses = courses;
        defaultCourseIndex = 0;
    }
}
