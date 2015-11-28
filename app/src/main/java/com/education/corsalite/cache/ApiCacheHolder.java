package com.education.corsalite.cache;

import com.education.corsalite.models.db.reqres.CoursesReqRes;
import com.education.corsalite.models.db.reqres.LoginReqRes;
import com.education.corsalite.models.db.reqres.StudyCenterReqRes;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.StudyCenter;

import java.util.List;

/**
 * Created by vissu on 11/27/15.
 */
public class ApiCacheHolder {

    private static ApiCacheHolder instance;

    public LoginReqRes login;
    public CoursesReqRes courses;
    public StudyCenterReqRes studyCenter;

    public static ApiCacheHolder getInstance() {
        if(instance == null) {
            instance = new ApiCacheHolder();
        }
        return instance;
    }

    public void setLoginRequest(String loginId, String passwordHash) {
        login = new LoginReqRes();
        login.loginId = loginId;
        login.passwordHash = passwordHash;
    }

    public void setLoginResponse(LoginResponse response) {
        if(login != null) {
            login.response = response;
        }
    }

    public void setCoursesRequest(String studentId) {
        courses = new CoursesReqRes();
        courses.studentId = studentId;
    }

    public void setCoursesResponse(List<Course> response) {
        if(courses != null) {
            courses.response = response;
        }
    }

    public void setStudyCenterRequest(String studentId, String courseId) {
        studyCenter = new StudyCenterReqRes();
        studyCenter.studentId = studentId;
        studyCenter.courseId = courseId;
    }

    public void setStudyCenterResponse(List<StudyCenter> response) {
        studyCenter.response = response;
    }
}
