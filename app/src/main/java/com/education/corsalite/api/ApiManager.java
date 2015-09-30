package com.education.corsalite.api;

import android.content.Context;
import android.content.res.AssetManager;

import com.education.corsalite.config.AppConfig;
import com.education.corsalite.enums.NetworkMode;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.models.responsemodels.CourseAnalysisResponse;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.LogoutResponse;
import com.education.corsalite.models.responsemodels.MessageResponse;
import com.education.corsalite.models.responsemodels.StudyCenter;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencySummaryResponse;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.FileUtils;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Created by vissu on 9/17/15.
 */
public class ApiManager {

    private static ApiManager instance;
    private AssetManager assets;

    public static ApiManager getInstance(Context context) {
        if(instance == null) {
            instance = new ApiManager();
        }
        instance.assets = context.getAssets();
        return instance;
    }

    public boolean isApiOnline() {
        L.info("Network Mode is "+AppConfig.NETWORK_MODE.getValue());
        return AppConfig.NETWORK_MODE == NetworkMode.ONLINE;
    }

    // Dummy response object
    private Response getRetrofitResponse() {
        Response response = new Response("http://corsalite.com", 200, "Success", new ArrayList<Header>(), null);
        return response;
    }

    public void login(String loginId, String passwordHash, ApiCallback<LoginResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().login(loginId, passwordHash, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/login.json");
            L.info("Response for 'api/login.json' is "+jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, LoginResponse.class), getRetrofitResponse());
        }
    }

    public void logout(String update, ApiCallback<LogoutResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().logout(update, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/logout.json");
            L.info("Response for 'api/logout.json' is "+jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, LogoutResponse.class), getRetrofitResponse());
        }
    }
    public void getCourseAnalysisData(String studentId,String courseId,String subjectId,
                                      String groupLevel,String breakUpByDate,
                                      String durationInDays,String returnAllRowsWithourPerfData,
                                      ApiCallback<CourseAnalysisResponse> callback){
        if(isApiOnline()) {
            ApiClientService.get().getCourseAnalysis(studentId,courseId,subjectId,groupLevel,breakUpByDate,durationInDays,returnAllRowsWithourPerfData, callback );
        } else {
            String jsonResponse=null;
            if(groupLevel.equalsIgnoreCase("chapter")) {
                jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/course_analysis_by_chapter.json");
            }else if(groupLevel.equalsIgnoreCase("dates")) {
                L.info("Response for 'api/course_analysis_by_dates.json' is " + jsonResponse);
            }
            JsonParser jsonParser = new JsonParser();
            JsonArray analyticsarray= jsonParser.parse(jsonResponse).getAsJsonArray();
            CourseAnalysisResponse response = new CourseAnalysisResponse();
            for ( JsonElement aUser : analyticsarray ) {
                CourseAnalysis courseAnalysis = new Gson().fromJson(aUser, CourseAnalysis.class);
                response.courseAnalysisList.add(courseAnalysis);
            }
            callback.success(response, getRetrofitResponse());
        }

    }

    public void getUserProfile(String studentId, ApiCallback<UserProfileResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().getUserProfile(studentId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/user_profile.json");
            L.info("Response for 'api/user_profile.json' is "+jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, UserProfileResponse.class), getRetrofitResponse());
        }
    }

    public void getVirtualCurrencyBalance(String studentId, ApiCallback<VirtualCurrencyBalanceResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().getVirtualCurrencyBalance(studentId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/virtual_currency_balance.json");
            L.info("Response for 'api/virtual_currency_balance.json' is "+jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, VirtualCurrencyBalanceResponse.class), getRetrofitResponse());
        }
    }

    public void getVirtualCurrencyTransactions(String studentId, ApiCallback<VirtualCurrencySummaryResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().getVirtualCurrencyTransactions(studentId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/virtual_currency_summary.json");
            L.info("Response for 'api/virtual_currency_summary.json' is "+jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, VirtualCurrencySummaryResponse.class), getRetrofitResponse());
        }
    }

    public void getMessages(String studentId, ApiCallback<MessageResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().getMessages(studentId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/messages.json");
            L.info("Response for 'api/messages.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, MessageResponse.class), getRetrofitResponse());
        }
    }

    public void getStudyCentreData(String studentId, String courseID, ApiCallback<StudyCenter> callback) {
        if(isApiOnline()) {
            ApiClientService.get().getCourseStudyCenterData(studentId, courseID, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/studycentre.json");
            System.out.print("Response for 'api/studycentre.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, StudyCenter.class), getRetrofitResponse());
        }
    }




}
