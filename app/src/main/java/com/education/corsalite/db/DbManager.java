package com.education.corsalite.db;

import android.content.Context;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.db.ContentIndexResponse;
import com.education.corsalite.models.db.CourseList;
import com.education.corsalite.models.db.reqres.CoursesReqRes;
import com.education.corsalite.models.db.reqres.LoginReqRes;
import com.education.corsalite.models.db.reqres.ReqRes;
import com.education.corsalite.models.db.reqres.StudyCenterReqRes;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.StudyCenter;
import com.education.corsalite.utils.MockUtils;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Created by vissu on 9/16/15.
 */
public class DbManager extends  Db4oHelper{

    private static DbManager instance;
    private DbService dbService = null;
    private Context context;

    private DbManager(Context context) {
        super(context);
        this.context = context;
    }

    public static DbManager getInstance(Context context) {
        if(instance == null) {
            instance = new DbManager(context);
            instance.dbService = new DbService();
        }
        return instance;
    }

    public void saveLoginResponse(LoginReqRes loginReqRes) {
        List<LoginReqRes> loginReqResList = getLoginRequestResponseList();
        if(loginReqResList != null && !loginReqResList.isEmpty()) {
            for(LoginReqRes reqRes : loginReqResList) {
                if(reqRes.loginId.equalsIgnoreCase(loginReqRes.loginId) && reqRes.passwordHash.equals(loginReqRes.passwordHash)) {
                    reqRes.response = loginReqRes.response;
                    dbService.Save(reqRes);
                    return;
                }
            }
        }
        dbService.Save(loginReqRes);
    }

    public void getLoginResponse(String loginId, String passwordHash, ApiCallback<LoginResponse> callback) {
        List<LoginReqRes> loginReqResList = getLoginRequestResponseList();
        if(loginReqResList != null && !loginReqResList.isEmpty()) {
            for(LoginReqRes reqRes : loginReqResList) {
                if(reqRes.loginId.equalsIgnoreCase(loginId) && reqRes.passwordHash.equals(passwordHash)) {
                    callback.success(reqRes.response, MockUtils.getRetrofitResponse());
                    return;
                }
            }
        } else {
            callback.failure(MockUtils.getCorsaliteError("Failure", "Notwork not available..."));
        }
        callback.failure(MockUtils.getCorsaliteError("Failure", "Invalid Authentication details..."));
    }

    public List<LoginReqRes> getLoginRequestResponseList() {
        List<LoginReqRes> responses = dbService.Get(LoginReqRes.class);
        if(responses != null && !responses.isEmpty()) {
             return responses;
        }
        return null;
    }

    public void saveCoursesResponse(CoursesReqRes coursesReqRes) {
        List<CoursesReqRes> courseReqResList = getCourseRequestResponseList();
        if(courseReqResList != null && !courseReqResList.isEmpty()) {
            for(CoursesReqRes reqRes : courseReqResList) {
                if(reqRes.studentId.equalsIgnoreCase(coursesReqRes.studentId)) {
                    reqRes.response = coursesReqRes.response;
                    dbService.Save(reqRes);
                    return;
                }
            }
        }
        dbService.Save(coursesReqRes);
    }

    public void getCoursesResponse(String studentId, ApiCallback<List<Course>> callback) {
        List<CoursesReqRes> courseReqResList = getCourseRequestResponseList();
        if(courseReqResList != null && !courseReqResList.isEmpty()) {
            for(CoursesReqRes reqRes : courseReqResList) {
                if(reqRes.studentId.equalsIgnoreCase(studentId)) {
                    callback.success(reqRes.response, MockUtils.getRetrofitResponse());
                    return;
                }
            }
        } else {
            callback.failure(MockUtils.getCorsaliteError("Failure", "Notwork not available..."));
        }
        callback.failure(MockUtils.getCorsaliteError("Failure", "No data found..."));
    }

    public List<CoursesReqRes> getCourseRequestResponseList() {
        List<CoursesReqRes> responses = dbService.Get(CoursesReqRes.class);
        if(responses != null && !responses.isEmpty()) {
            return responses;
        }
        return null;
    }


    public void saveStudyCenterResponse(StudyCenterReqRes studyCenterReqRes) {
        List<StudyCenterReqRes> studyCenterReqResList = getStudyCenterRequestResponseList();
        if(studyCenterReqResList != null && !studyCenterReqResList.isEmpty()) {
            for(StudyCenterReqRes reqRes : studyCenterReqResList) {
                if(reqRes.studentId.equalsIgnoreCase(studyCenterReqRes.studentId) && reqRes.courseId.equalsIgnoreCase(studyCenterReqRes.courseId)) {
                    reqRes.response = studyCenterReqRes.response;
                    dbService.Save(reqRes);
                    return;
                }
            }
        }
        dbService.Save(studyCenterReqRes);
    }

    public void getStudyCenterResponse(String studentId, String courseId, ApiCallback<List<StudyCenter>> callback) {
        List<StudyCenterReqRes> studyCenterReqResList = getStudyCenterRequestResponseList();
        if(studyCenterReqResList != null && !studyCenterReqResList.isEmpty()) {
            for(StudyCenterReqRes reqRes : studyCenterReqResList) {
                if(reqRes.studentId.equalsIgnoreCase(studentId) && reqRes.courseId.equalsIgnoreCase(courseId)) {
                    callback.success(reqRes.response, MockUtils.getRetrofitResponse());
                    return;
                }
            }
        } else {
            callback.failure(MockUtils.getCorsaliteError("Failure", "Notwork not available..."));
        }
        callback.failure(MockUtils.getCorsaliteError("Failure", "No data found..."));
    }

    public List<StudyCenterReqRes> getStudyCenterRequestResponseList() {
        List<StudyCenterReqRes> responses = dbService.Get(StudyCenterReqRes.class);
        if(responses != null && !responses.isEmpty()) {
            return responses;
        }
        return null;
    }



    public void saveCourseList(CourseList courseList) {
        List<CourseList> list = dbService.Get(CourseList.class);
        if(list != null && !list.isEmpty()) {
            courseList.defaultCourseIndex = list.get(0).defaultCourseIndex;
        }
        dbService.Delete(CourseList.class);
        dbService.Save(courseList);
    }

    public void saveDefaultCourse(int defaultCourseIndex) {
        List<CourseList> list = dbService.Get(CourseList.class);
        if(list != null && !list.isEmpty()) {
            CourseList courseList = list.get(0);
            courseList.defaultCourseIndex = defaultCourseIndex;
            dbService.Save(courseList);
        }

    }

    public void deleteCourseList() {
        dbService.Delete(CourseList.class);
    }

    public CourseList getCourseList() {
        List<CourseList> list = dbService.Get(CourseList.class);
        if(list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public ContentIndexResponse getContentIndexList(String courseId, String studentId) {
        Select specificAuthorQuery = Select.from(ContentIndexResponse.class)
                .where(Condition.prop("course_id").eq(courseId),
                        Condition.prop("student_id").eq(studentId))
                .limit("1");
        ContentIndexResponse contentIndexResponses = (ContentIndexResponse) specificAuthorQuery.first();
        return contentIndexResponses;
    }

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
     * User Profile Db stuff
     */
    public <T> void saveReqRes(ReqRes<T> reqres) {
        List<? extends ReqRes> reqResList = dbService.Get(reqres.getClass());
        if(reqResList != null && !reqResList.isEmpty()) {
            for (ReqRes reqresItem: reqResList) {
                if(reqresItem.isRequestSame(reqres)) {
                    reqresItem.response = reqres.response;
                    dbService.Save(reqresItem);
                }
            }
        }
        dbService.Save(reqres);
    }

    public <T> void getResponse(ReqRes<T> reqres, ApiCallback<T> callback) {
        List<? extends ReqRes> reqResList = dbService.Get(reqres.getClass());
        if(reqResList != null && !reqResList.isEmpty()) {
            for (ReqRes reqresItem: reqResList) {
                if(reqresItem.isRequestSame(reqres)) {
                    callback.success((T)reqresItem.response, MockUtils.getRetrofitResponse());
                }
            }
        } else {
            callback.failure(MockUtils.getCorsaliteError("Failure", "Notwork not available..."));
        }
        callback.failure(MockUtils.getCorsaliteError("Failure", "No data found..."));
    }
}
