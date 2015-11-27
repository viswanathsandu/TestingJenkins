package com.education.corsalite.api;

import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.models.responsemodels.CourseAnalysisPercentile;
import com.education.corsalite.models.responsemodels.DefaultCourseResponse;
import com.education.corsalite.models.responsemodels.DefaultNoteResponse;
import com.education.corsalite.models.responsemodels.EditProfileModel;
import com.education.corsalite.models.responsemodels.ExerciseModel;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.LogoutResponse;
import com.education.corsalite.models.responsemodels.Message;
import com.education.corsalite.models.responsemodels.Note;
import com.education.corsalite.models.responsemodels.PostExercise;
import com.education.corsalite.models.responsemodels.StudyCenter;
import com.education.corsalite.models.responsemodels.TestCoverage;
import com.education.corsalite.models.responsemodels.UpdateExamDetailsResponse;
import com.education.corsalite.models.responsemodels.UsageAnalysis;
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

    @POST("/ExerciseAnswer")
    void postExerciseAnswer(@Query("Insert") String insert, ApiCallback<PostExercise> callback);

    @GET("/StudentCourseList")
    void getCourses(@Query("idStudent") String studentId, ApiCallback<List<Course>> callback);

    @GET("/StudentProfile")
    void getUserProfile(@Query("idStudent") String studentId, ApiCallback<UserProfileResponse> callback);

    @GET("/VirtualCurrencyBalance")
    void getVirtualCurrencyBalance(@Query("idStudent") String studentId, ApiCallback<VirtualCurrencyBalanceResponse> callback);

    @GET("/VirtualCurrencyTransaction")
    void getVirtualCurrencyTransactions(@Query("idStudent") String studentId, ApiCallback<VirtualCurrencySummaryResponse> callback);

    @GET("/Message")
    void getMessages(@Query("idStudent") String studentId, ApiCallback<List<Message>> callback);

    @GET("/CourseStudyCenterData")
    void getCourseStudyCenterData(@Query("idStudent") String studentId, @Query("idCourse") String courseId, ApiCallback<List<StudyCenter>> callback);

    @GET("/CourseStudyCenterData")
    void getNotesData(ApiCallback<List<Note>> callback);

    //http://staging.corsalite.com/v1/webservices/GetCourseAnalysis?idStudent=36&idCourse=13&idSubject=51&GroupLevel=Chapter&BreakupByDate=Month&DurationInDays=365&ReturnAllRowsWithoutPerfDataAlso=true
    @GET("/GetCourseAnalysis")
    void getCourseAnalysis(@Query("idStudent") String studentId,
                           @Query("idCourse") String courseId,
                           @Query("idSubject") String subjectID,
                           @Query("GroupLevel") String groupLevel,
                           @Query("BreakupByDate") String breakUpByDate,
                           @Query("DurationInDays") String durationInDays,
                           @Query("ReturnAllRowsWithoutPerfDataAlso") String returnAllRowsWithourPerfData,
                           ApiCallback<List<CourseAnalysis>> callback);

    @GET("/GetCourseAnalysisAsPercentile")
    void getCourseAnalysisPercentile(@Query("idStudent") String studentId,
                           @Query("idCourse") String courseId,
                           @Query("idSubject")String subjectID,
                           @Query("GroupLevel")String groupLevel,
                           @Query("BreakupByDate")String breakUpByDate,
                           @Query("DurationInDays")String durationInDays,
                           @Query("ReturnAllRowsWithoutPerfDataAlso")String returnAllRowsWithourPerfData,
                           ApiCallback<List<CourseAnalysisPercentile>> callback);

    @GET("/GetTestCoverage")
    void getTestCoverage(@Query("idStudent") String studentId,
                         @Query("idCourse") String courseId,
                         ApiCallback<List<TestCoverage>> callback);

    @GET("/ContentIndex")
    void getContentIndexData(@Query("idCourse") String courseId, @Query("idStudent") String studentId, ApiCallback<List<ContentIndex>> callback);

    @GET("/Content")
    void getContentData(@Query("idContents") String idContents, @Query("UpdateTime") String UpdateTime, ApiCallback<List<Content>> callback);

    @GET("/Exercise")
    void getExerciseData(@Query("idTopic") String idTopics, @Query("idCourse") String idCourse , @Query("idStudent") String idStudent, @Query("UpdateTime") String UpdateTime, ApiCallback<List<ExerciseModel>> callback);

    @POST("/UserProfile")
    void updateUserProfile(@Query("Update") String userProfile, ApiCallback<EditProfileModel> callback);

    @POST("/StudentCourseList")
    void updateDefaultCourse(@Query("Update") String update, ApiCallback<DefaultCourseResponse> callback);

    @POST("/StudentExamDetails")
    void updateExamDetails(@Query("Update") String update, ApiCallback<UpdateExamDetailsResponse> callback);

    @GET("/Note")
    void getNotes(@Query("idStudent") String studentId, @Query("idSubject") String subjectId , @Query("idChapter") String chapterId, @Query("idTopic") String topicId, ApiCallback<List<Note>> callback);

    @POST("/Note")
    void addNote(@Query("Insert") String insert, ApiCallback<DefaultNoteResponse> callback);

    @POST("/Note")
    void updateNote(@Query("Update") String insert, ApiCallback<DefaultNoteResponse> callback);

    @GET("/UsageAnalysis")
    void getUsageAnalysis(@Query("idUser") String userId, ApiCallback<UsageAnalysis> callback);
}