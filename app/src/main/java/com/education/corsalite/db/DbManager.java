package com.education.corsalite.db;

import android.content.Context;

import com.education.corsalite.models.db.ContentIndexResponse;
import com.education.corsalite.models.db.CourseList;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
        CourseList list = CourseList.findById(CourseList.class, 1l);
        if(list != null) {
            courseList.defaultCourseIndex = list.defaultCourseIndex;
        }
        // clear sourselist before adding new
        CourseList.deleteAll(CourseList.class);
        courseList.save();
    }

    public void saveDefaultCourse(int defaultCourseIndex) {
        Iterator<CourseList> iterator = CourseList.findAll(CourseList.class);
        if(iterator != null && iterator.hasNext()) {
            CourseList list = iterator.next();
            list.defaultCourseIndex = defaultCourseIndex;
            list.save();
        }
    }

    public void deleteCourseList() {
        CourseList.deleteAll(CourseList.class);
    }

    public CourseList getCourseList() {
        Iterator<CourseList> iterator = CourseList.findAll(CourseList.class);
        return (iterator!= null && iterator.hasNext()) ? iterator.next() : null;
    }

    /**
     *
     * @param courseId
     * @param studentId
     * @return ContentIndexResponse
     */
    public ContentIndexResponse getContentIndexList(String courseId, String studentId) {
        Select specificAuthorQuery = Select.from(ContentIndexResponse.class)
                .where(Condition.prop("course_id").eq(courseId),
                        Condition.prop("student_id").eq(studentId))
                .limit("1");
        ContentIndexResponse contentIndexResponses = (ContentIndexResponse) specificAuthorQuery.first();
        return contentIndexResponses;
    }

    /**
     * Saved List of Content Index to db as a string with courseID and student ID
     * @param contentIndexJson
     * @param courseId
     * @param studentId
     */
    public void saveContentIndexList(String contentIndexJson, String courseId, String studentId) {
        Select specificAuthorQuery = Select.from(ContentIndexResponse.class)
                .where(Condition.prop("course_id").eq(courseId),
                        Condition.prop("student_id").eq(studentId))
                .limit("1");
        ContentIndexResponse contentIndexResponses = (ContentIndexResponse) specificAuthorQuery.first();
        if (contentIndexResponses != null) {
            return;
        }
        contentIndexResponses = new ContentIndexResponse(contentIndexJson, courseId, studentId);
        contentIndexResponses.save();
    }

}
