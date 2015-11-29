package com.education.corsalite.cache;

import com.education.corsalite.models.db.reqres.ContentIndexReqRes;
import com.education.corsalite.models.db.reqres.CoursesReqRes;
import com.education.corsalite.models.db.reqres.LoginReqRes;
import com.education.corsalite.models.db.reqres.StudyCenterReqRes;
import com.education.corsalite.models.db.reqres.UserProfileReqRes;
import com.education.corsalite.models.db.reqres.VirtualCurrencyBalanceReqRes;
import com.education.corsalite.models.db.reqres.requests.ContentIndexRequest;
import com.education.corsalite.models.db.reqres.requests.CourseRequest;
import com.education.corsalite.models.db.reqres.requests.LoginRequest;
import com.education.corsalite.models.db.reqres.requests.StudyCenterRequest;
import com.education.corsalite.models.db.reqres.requests.UserProfileRequest;
import com.education.corsalite.models.db.reqres.requests.VirtualCurrencyBalanceRequest;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.StudyCenter;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;

import java.util.List;

/**
 * Created by vissu on 11/27/15.
 */
public class ApiCacheHolder {

    private static ApiCacheHolder instance;

    public LoginReqRes login;
    public UserProfileReqRes userProfile;
    public CoursesReqRes courses;
    public StudyCenterReqRes studyCenter;
    public ContentIndexReqRes contentIndex;
    public VirtualCurrencyBalanceReqRes virtualCurrencyBalance;

    public static ApiCacheHolder getInstance() {
        if(instance == null) {
            instance = new ApiCacheHolder();
        }
        return instance;
    }

    public void setLoginRequest(String loginId, String passwordHash) {
        login = new LoginReqRes();
        login.request = new LoginRequest(loginId, passwordHash);
    }

    public void setLoginResponse(LoginResponse response) {
        if(login != null) {
            login.response = response;
        }
    }

    public void setUserProfileRequest(String studentId) {
        userProfile = new UserProfileReqRes();
        userProfile.request = new UserProfileRequest(studentId);
    }

    public void setUserProfileRespose(UserProfileResponse response) {
        if(userProfile != null) {
            userProfile.response = response;
        }
    }

    public void setCoursesRequest(String studentId) {
        courses = new CoursesReqRes();
        courses.request = new CourseRequest(studentId);
    }

    public void setCoursesResponse(List<Course> response) {
        if(courses != null) {
            courses.response = response;
        }
    }

    public void setStudyCenterRequest(String studentId, String courseId) {
        studyCenter = new StudyCenterReqRes();
        studyCenter.request = new StudyCenterRequest(studentId, courseId);
    }

    public void setStudyCenterResponse(List<StudyCenter> response) {
        if(studyCenter != null) {
            studyCenter.response = response;
        }
    }


    public void setContentIndexRequest(String studentId, String courseId) {
        contentIndex = new ContentIndexReqRes();
        contentIndex.request = new ContentIndexRequest(studentId, courseId);
    }

    public void setcontentIndexResponse(List<ContentIndex> response) {
        if(contentIndex != null) {
            contentIndex.response = response;
        }
    }

    public void setVirtualCurrencyBalanceRequest(String studentId) {
        virtualCurrencyBalance = new VirtualCurrencyBalanceReqRes();
        virtualCurrencyBalance.request = new VirtualCurrencyBalanceRequest(studentId);
    }

    public void setVirtualCurrencyBalanceResponse(VirtualCurrencyBalanceResponse response) {
        if(virtualCurrencyBalance != null) {
            virtualCurrencyBalance.response = response;
        }
    }
}
