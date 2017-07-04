package com.education.corsalite.utils;

import android.app.Activity;
import android.content.Intent;

import com.education.corsalite.activities.CrashHandlerActivity;
import com.education.corsalite.activities.StudyCenterActivity;
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
    private final static String COMPUTER_ADAPTIVE_TEST_URL = "examination/loadExam/0/0/%s";
    private final static String HANDLER_URL_PATTERN = "handler";
    private final static String STUDY_CENTER_URL_PATTERN = "student";
    private final static String RECOMMENDED_READING_URL_PATTERN = "dashboard/courseAnalysis/%s/recommendedreading";
    private final static String  PROGRESS_REPORT_URL_PATTERN = "dashboard/courseAnalysis/%s/progressreport";
    private final static String  TIME_MANAGEMENT_URL_PATTERN = "dashboard/courseAnalysis/13/timemanagementbysubject";

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

    public static String getComputerAdaptiveTestUrl(String examTemplateId) {
        return ApiClientService.getBaseUrl()+String.format(COMPUTER_ADAPTIVE_TEST_URL, examTemplateId);
    }

    public static boolean isHandler(String url) {
        try {
            return url.contains(ApiClientService.getBaseUrl() + HANDLER_URL_PATTERN);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isStudyCenter(String url) {
        try {
            return url.contains(ApiClientService.getBaseUrl() + STUDY_CENTER_URL_PATTERN);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getProgressReportUrl(String courseId) {
        return ApiClientService.getBaseUrl() + String.format(PROGRESS_REPORT_URL_PATTERN, courseId);
    }

    public static String getRecommendedReadingUrl(String courseId) {
        return ApiClientService.getBaseUrl() + String.format(RECOMMENDED_READING_URL_PATTERN, courseId);
    }

    public static String getTimeManagementUrl(String courseId) {
        return ApiClientService.getBaseUrl() + String.format(TIME_MANAGEMENT_URL_PATTERN, courseId);
    }

    public static Intent getIntent(Activity activity, String url) {
        try {
            if (isHandler(url)) {
                return new Intent(activity, CrashHandlerActivity.class);
            } else if(isStudyCenter(url)) {
                return new Intent(activity, StudyCenterActivity.class);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
