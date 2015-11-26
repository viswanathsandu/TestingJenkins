package com.education.corsalite.db;

import android.content.Context;

import com.db4o.ObjectContainer;
import com.education.corsalite.models.db.ContentIndexResponse;
import com.education.corsalite.models.db.CourseList;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Iterator;
import java.util.List;

/**
 * Created by vissu on 9/16/15.
 */
public class DbManager extends  Db4oHelper{

    private static DbManager instance;
    private Context context;

    private DbManager(Context context) {
        super(context);
        this.context = context;
    }

    public static DbManager getInstance(Context context) {
        if(instance == null) {
            instance = new DbManager(context);
        }
        return instance;
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
            contentIndexResponses.courseId = courseId;
            contentIndexResponses.studentId = studentId;
            contentIndexResponses.contentIndexesJson = contentIndexJson;
        }else {
            contentIndexResponses = new ContentIndexResponse(contentIndexJson, courseId, studentId);
        }
        contentIndexResponses.save();
    }


    /**
     * Db4o implemnetation for db management
     */

    //This method is used to store the object into the database.
    public void saveLoginResponse(LoginResponse loginResponse) {
        ObjectContainer db = db();
        db.store(loginResponse);
    }

    //This method is used to delete the object into the database.
    public void delete(LoginResponse loginResponse) {
        db().delete(loginResponse);
    }

    //This method is used to retrive all object from database.
    public LoginResponse getLoginResponse() {
        LoginResponse response = null;
        while(db().query(LoginResponse.class).hasNext()) {
            response = db().query(LoginResponse.class).next();
        }
        return response;
    }

    //This method is used to retrive matched object from database.
    public List<LoginResponse> getRecord(LoginResponse loginResponse) {
        return db().queryByExample(loginResponse);
    }

}
