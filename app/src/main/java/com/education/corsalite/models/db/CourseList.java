package com.education.corsalite.models.db;

import android.content.Context;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by vissu on 9/17/15.
 */
public class CourseList extends SugarRecord<CourseList>{
    public List<String> courses;
    public Integer defaultCourseIndex;

    public CourseList() {
        super();
    }

    public CourseList(List<String> courses) {
        this.courses = courses;
        defaultCourseIndex = 0;
    }
}
