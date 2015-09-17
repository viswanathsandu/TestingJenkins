package com.education.corsalite.db;

import android.content.Context;

import com.education.corsalite.models.db.CourseList;

import java.util.List;

/**
 * Created by vissu on 9/16/15.
 */
public class DbManager {

    private DbManager instance;
    private Context context;

    private DbManager(Context context) {
        this.context = context;
    }

    public static DbManager getInstance(Context context) {
        return new DbManager(context);
    }

    public void saveCourseList(CourseList courseList) {
        // clear sourselist before adding new
        CourseList.deleteAll(CourseList.class);
        courseList.save();
    }

    public void saveDefaultCourse(int defaultCourseIndex) {
        CourseList list = CourseList.findById(CourseList.class, 1l);
        if(list != null) {
            list.defaultCourseIndex = defaultCourseIndex;
            list.save();
        }
    }

    public void deleteCourseList() {
        CourseList.deleteAll(CourseList.class);
    }

    public CourseList getCourseList() {
        CourseList list = CourseList.findById(CourseList.class, 1l);
        return list;
    }

}
