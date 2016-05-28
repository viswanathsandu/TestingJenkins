package com.education.corsalite.cache;

import com.education.corsalite.models.db.reqres.ContentIndexReqRes;
import com.education.corsalite.models.db.reqres.ContentReqRes;
import com.education.corsalite.models.db.reqres.CoursesReqRes;
import com.education.corsalite.models.db.reqres.LoginReqRes;
import com.education.corsalite.models.db.reqres.StudyCenterReqRes;
import com.education.corsalite.models.db.reqres.UserProfileReqRes;
import com.education.corsalite.models.db.reqres.VirtualCurrencyBalanceReqRes;
import com.education.corsalite.models.db.reqres.requests.ContentIndexRequest;
import com.education.corsalite.models.db.reqres.requests.ContentRequest;
import com.education.corsalite.models.db.reqres.requests.CourseRequest;
import com.education.corsalite.models.db.reqres.requests.LoginRequest;
import com.education.corsalite.models.db.reqres.requests.StudyCenterRequest;
import com.education.corsalite.models.db.reqres.requests.UserProfileRequest;
import com.education.corsalite.models.db.reqres.requests.VirtualCurrencyBalanceRequest;
import com.education.corsalite.models.responsemodels.Content;
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

    public LoginRequest loginRequest = null;
    public LoginReqRes login = null;
    public UserProfileRequest userProfileRequest = null;
    public UserProfileReqRes userProfile;
    public CourseRequest courseRequest = null;
    public CoursesReqRes courses = null;
    public StudyCenterRequest studyCenterRequest = null;
    public StudyCenterReqRes studyCenter = null;
    public ContentIndexRequest contentIndexRequest = null;
    public ContentIndexReqRes contentIndex = null;
    public ContentRequest contentRequest = null;
    public ContentReqRes contentReqIndex;
    public VirtualCurrencyBalanceRequest virtualCurrencyBalanceRequest = null;
    public VirtualCurrencyBalanceReqRes virtualCurrencyBalance = null;

    public static ApiCacheHolder getInstance() {
        if(instance == null) {
            instance = new ApiCacheHolder();
        }
        return instance;
    }

    public void setLoginRequest(String loginId, String passwordHash) {
        loginRequest = new LoginRequest(loginId, passwordHash);
    }

    public void setLoginResponse(LoginResponse response) {
        if(loginRequest != null && response != null) {
            login = new LoginReqRes();
            login.request = loginRequest;
            login.response = response;
        }
    }

    public void setUserProfileRequest(String studentId) {
        userProfileRequest = new UserProfileRequest(studentId);
    }

    public void setUserProfileRespose(UserProfileResponse response) {
        if(userProfileRequest != null && response != null) {
            userProfile = new UserProfileReqRes();
            userProfile.request = userProfileRequest;
            userProfile.response = response;
        }
    }

    public void setCoursesRequest(String studentId) {
        courseRequest= new CourseRequest(studentId);
    }

    public void setCoursesResponse(List<Course> courseList) {
        if(courseRequest != null && courseList != null) {
            courses = new CoursesReqRes();
            courses.request = courseRequest;
            courses.response = courseList;
        }
    }

    public void setStudyCenterRequest(String studentId, String courseId) {
        studyCenterRequest = new StudyCenterRequest(studentId, courseId);
    }

    public void setStudyCenterResponse(List<StudyCenter> response) {
        if(studyCenterRequest != null && response != null) {
            studyCenter = new StudyCenterReqRes();
            studyCenter.request = studyCenterRequest;
            studyCenter.response = response;
        }
    }


    public void setContentIndexRequest(String studentId, String courseId) {
        contentIndexRequest = new ContentIndexRequest(studentId, courseId);
    }

    public void setcontentIndexResponse(List<ContentIndex> response) {
        if(contentIndexRequest != null && response != null) {
            contentIndex = new ContentIndexReqRes();
            contentIndex.request = contentIndexRequest;
            contentIndex.response = response;
        }
    }

    public void setContentRequest(String idContents, String updateTime) {
        contentRequest = new ContentRequest(idContents, updateTime);
    }

    public void setContentResponse(List<Content> response) {
        if(contentRequest != null && response != null) {
            contentReqIndex = new ContentReqRes();
            contentReqIndex.request = contentRequest;
            contentReqIndex.response = response;
        }
    }

    public void setVirtualCurrencyBalanceRequest(String studentId) {
        virtualCurrencyBalanceRequest = new VirtualCurrencyBalanceRequest(studentId);
    }

    public void setVirtualCurrencyBalanceResponse(VirtualCurrencyBalanceResponse response) {
        if(virtualCurrencyBalanceRequest != null && response != null) {
            virtualCurrencyBalance = new VirtualCurrencyBalanceReqRes();
            virtualCurrencyBalance.request = virtualCurrencyBalanceRequest;
            virtualCurrencyBalance.response = response;
        }
    }
}
