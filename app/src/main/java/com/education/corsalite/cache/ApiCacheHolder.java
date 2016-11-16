package com.education.corsalite.cache;

import com.education.corsalite.models.db.ScheduledTestList;
import com.education.corsalite.models.db.reqres.AppConfigReqRes;
import com.education.corsalite.models.db.reqres.AppentityconfigReqRes;
import com.education.corsalite.models.db.reqres.ContentIndexReqRes;
import com.education.corsalite.models.db.reqres.ContentReqRes;
import com.education.corsalite.models.db.reqres.CoursesReqRes;
import com.education.corsalite.models.db.reqres.LoginReqRes;
import com.education.corsalite.models.db.reqres.ScheduleTestsReqRes;
import com.education.corsalite.models.db.reqres.StudyCenterReqRes;
import com.education.corsalite.models.db.reqres.UserProfileReqRes;
import com.education.corsalite.models.db.reqres.VirtualCurrencyBalanceReqRes;
import com.education.corsalite.models.db.reqres.WelcomeReqRes;
import com.education.corsalite.models.db.reqres.requests.AppConfigRequest;
import com.education.corsalite.models.db.reqres.requests.AppEntityConfigRequest;
import com.education.corsalite.models.db.reqres.requests.ContentIndexRequest;
import com.education.corsalite.models.db.reqres.requests.ContentRequest;
import com.education.corsalite.models.db.reqres.requests.CourseRequest;
import com.education.corsalite.models.db.reqres.requests.LoginRequest;
import com.education.corsalite.models.db.reqres.requests.ScheduleTestsRequest;
import com.education.corsalite.models.db.reqres.requests.StudyCenterRequest;
import com.education.corsalite.models.db.reqres.requests.UserProfileRequest;
import com.education.corsalite.models.db.reqres.requests.VirtualCurrencyBalanceRequest;
import com.education.corsalite.models.db.reqres.requests.WelcomeRequest;
import com.education.corsalite.models.responsemodels.ClientEntityAppConfig;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.StudyCenter;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.models.responsemodels.WelcomeDetails;
import com.education.corsalite.models.db.AppConfig;

import java.util.List;

/**
 * Created by vissu on 11/27/15.
 */
public class ApiCacheHolder {

    private static ApiCacheHolder instance;

    public AppentityconfigReqRes appentityconfigReqRes = null;
    public AppEntityConfigRequest appentityconfigRequest = null;
    public AppConfigRequest appConfigRequest = null;
    public AppConfigReqRes appConfigReqRes = null;
    public LoginRequest loginRequest = null;
    public LoginReqRes login = null;
    public WelcomeRequest welcomeRequest = null;
    public WelcomeReqRes welcome;
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
    public ScheduleTestsRequest scheduleTestsRequest = null;
    public ScheduleTestsReqRes scheduleTests;
    public VirtualCurrencyBalanceRequest virtualCurrencyBalanceRequest = null;
    public VirtualCurrencyBalanceReqRes virtualCurrencyBalance = null;

    public static ApiCacheHolder getInstance() {
        if(instance == null) {
            instance = new ApiCacheHolder();
        }
        return instance;
    }

    public void setAppConfigResponse(AppConfig response) {
        if(appConfigRequest != null && response != null) {
            appConfigReqRes = new AppConfigReqRes();
            appConfigReqRes.request = appConfigRequest;
            appConfigReqRes.response = response;
        }
    }

    public void setAppEntityConfigResponse(ClientEntityAppConfig response) {
        if(appentityconfigRequest != null && response != null) {
            appentityconfigReqRes = new AppentityconfigReqRes();
            appentityconfigReqRes.request = appentityconfigRequest;
            appentityconfigReqRes.response = response;
        }
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

    public void setWelcomeRequest(String studentId) {
        welcomeRequest = new WelcomeRequest(studentId);
    }

    public void setWelcomeRespose(WelcomeDetails response) {
        if(welcomeRequest != null && response != null) {
            welcome = new WelcomeReqRes();
            welcome.request = welcomeRequest;
            welcome.response = response;
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

    public void setScheduleTestsRequest(String studentId) {
        scheduleTestsRequest = new ScheduleTestsRequest(studentId);
    }

    public void setScheduleTestsResponse(ScheduledTestList response) {
        if(scheduleTestsRequest != null && response != null) {
            scheduleTests = new ScheduleTestsReqRes();
            scheduleTests.request = scheduleTestsRequest;
            scheduleTests.response = response;
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
