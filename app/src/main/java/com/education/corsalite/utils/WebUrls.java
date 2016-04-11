package com.education.corsalite.utils;

import com.education.corsalite.services.ApiClientService;

/**
 * Created by vissu on 4/11/16.
 */
public class WebUrls {

    private final static String FORGOT_PASSWORD_URL = "login/forgotPassword";
    private final static String ADD_GUARDIAN_URL = "profile/guardianInfo";
    private final static String ADD_COURSES_URL = "profile/courseInfo";
    private final static String REDEEM_URL = "redeem";
    private final static String SMART_CLASS_URL = "smartclass/index";
    private final static String EXAM_RESULTS_SUMMARY_URL = "examination/examResultSummary/";

    public static String getForgotPasswordUrl() {
        return ApiClientService.getBaseUrl()+FORGOT_PASSWORD_URL;
    }

    public static String getAddGuardianUrl() {
        return ApiClientService.getBaseUrl()+ADD_GUARDIAN_URL;
    }

    public static String getAddCoursesUrl() {
        return ApiClientService.getBaseUrl()+ADD_COURSES_URL;
    }

    public static String getRedeemUrl() {
        return ApiClientService.getBaseUrl()+REDEEM_URL;
    }

    public static String getSmartClassUrl() {
        return ApiClientService.getBaseUrl()+SMART_CLASS_URL;
    }

    public static String getExamResultsSummaryUrl() {
        return ApiClientService.getBaseUrl()+EXAM_RESULTS_SUMMARY_URL;
    }
}
