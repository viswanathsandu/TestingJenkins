package com.education.corsalite.api;

import com.education.corsalite.models.requestmodels.EditProfileModel;
import com.education.corsalite.models.responsemodels.ContentIndexResponse;
import com.education.corsalite.models.responsemodels.ContentResponse;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.LogoutResponse;
import com.education.corsalite.models.responsemodels.MessageResponse;
import com.education.corsalite.models.responsemodels.StudyCenter;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencySummaryResponse;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by vissu on 9/11/15.
 */
public interface ICorsaliteApi {

    @GET("/AuthToken")
    void login(@Query("LoginID") String loginId, @Query("PasswordHash") String passwordHash, ApiCallback<LoginResponse> callback);

    @POST("/AuthToken")
    void logout(@Query("Update") String update, ApiCallback<LogoutResponse> callback);

    @GET("/StudentProfile")
    void getUserProfile(@Query("idStudent") String studentId, ApiCallback<UserProfileResponse> callback);

    @GET("/VirtualCurrencyBalance")
    void getVirtualCurrencyBalance(@Query("idStudent") String studentId, ApiCallback<VirtualCurrencyBalanceResponse> callback);

    @GET("/VirtualCurrencyTransaction")
    void getVirtualCurrencyTransactions(@Query("idStudent") String studentId, ApiCallback<VirtualCurrencySummaryResponse> callback);

    @GET("/Message")
    void getMessages(@Query("idStudent") String studentId, ApiCallback<MessageResponse> callback);

    @POST("/UserProfile")
    void updateUserProfile(@Query("Update")UserProfileResponse userProfile, ApiCallback<EditProfileModel> callback);

    @GET("/CourseStudyCenterData")
    void getCourseStudyCenterData(@Query("idStudent") String studentId, @Query("idCourse") String courseId, ApiCallback<StudyCenter> callback);

    //http://staging.corsalite.com/v1/webservices/GetCourseAnalysis?idStudent=36&idCourse=13&idSubject=51&GroupLevel=Chapter&BreakupByDate=Month&DurationInDays=365&ReturnAllRowsWithoutPerfDataAlso=true
    @GET("/GetCourseAnalysis")
    void getCourseAnalysis(@Query("idStudent") String studentId,
                                  @Query("idCourse") String courseId,
                                  @Query("idSubject")String subjectID,
                                  @Query("GroupLevel")String groupLevel,
                                  @Query("BreakupByDate")String breakUpByDate,
                                  @Query("DurationInDays")String durationInDays,
                                  @Query("ReturnAllRowsWithoutPerfDataAlso")String returnAllRowsWithourPerfData,
                                  ApiCallback<List<CourseAnalysis>> callback);

    @GET("/ContentIndex")
    void getContentIndexData(@Query("idCourse") String courseId, @Query("idStudent") String studentId, ApiCallback<ContentIndexResponse> callback);

    @GET("/Content")
    void getContentData(@Query("idContents") String idContents, @Query("UpdateTime") String UpdateTime, ApiCallback<ContentResponse> callback);
}