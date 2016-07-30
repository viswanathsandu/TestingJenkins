package com.education.corsalite.api;

import com.education.corsalite.models.db.MockTest;
import com.education.corsalite.models.db.ScheduledTestList;
import com.education.corsalite.models.responsemodels.BaseResponseModel;
import com.education.corsalite.models.responsemodels.ChallengeCompleteResponseModel;
import com.education.corsalite.models.responsemodels.ChallengeStartResponseModel;
import com.education.corsalite.models.responsemodels.ChallengeUser;
import com.education.corsalite.models.responsemodels.ChallengeUserListResponse;
import com.education.corsalite.models.responsemodels.CommonResponseModel;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.models.responsemodels.CourseAnalysisPercentile;
import com.education.corsalite.models.responsemodels.CreateChallengeResponseModel;
import com.education.corsalite.models.responsemodels.CurriculumResponseModel;
import com.education.corsalite.models.responsemodels.DefaultCourseResponse;
import com.education.corsalite.models.responsemodels.DefaultForumResponse;
import com.education.corsalite.models.responsemodels.DefaultNoteResponse;
import com.education.corsalite.models.responsemodels.EditProfileModel;
import com.education.corsalite.models.responsemodels.Exam;
import com.education.corsalite.models.responsemodels.ExamHistory;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.ForumPost;
import com.education.corsalite.models.responsemodels.FourmCommentPostModel;
import com.education.corsalite.models.responsemodels.FriendsData;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.LogoutResponse;
import com.education.corsalite.models.responsemodels.Message;
import com.education.corsalite.models.responsemodels.Note;
import com.education.corsalite.models.responsemodels.PartTestModel;
import com.education.corsalite.models.responsemodels.PostExamTemplate;
import com.education.corsalite.models.responsemodels.PostExercise;
import com.education.corsalite.models.responsemodels.PostFlaggedQuestions;
import com.education.corsalite.models.responsemodels.PostQuestionPaper;
import com.education.corsalite.models.responsemodels.RecommendedModel;
import com.education.corsalite.models.responsemodels.ScheduledTest;
import com.education.corsalite.models.responsemodels.StudyCenter;
import com.education.corsalite.models.responsemodels.TestAnswerPaperResponse;
import com.education.corsalite.models.responsemodels.TestCoverage;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.models.responsemodels.TestQuestionPaperResponse;
import com.education.corsalite.models.responsemodels.UpdateExamDetailsResponse;
import com.education.corsalite.models.responsemodels.UsageAnalysis;
import com.education.corsalite.models.responsemodels.UserEventsResponse;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencySummaryResponse;
import com.education.corsalite.models.responsemodels.WelcomeDetails;
import com.education.corsalite.models.db.AppConfig;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.mime.TypedString;

/**
 * Created by vissu on 9/11/15.
 */
public interface ICorsaliteApi {

    @GET("/AuthToken")
    void login(@Query("LoginID") String loginId, @Query("PasswordHash") String passwordHash, ApiCallback<LoginResponse> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/AuthToken")
    void logout(@Body TypedString update, ApiCallback<LogoutResponse> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/ExerciseAnswer")
    void postExerciseAnswer(@Body TypedString insert, ApiCallback<PostExercise> callback);

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
                                     @Query("idSubject") String subjectID,
                                     @Query("GroupLevel") String groupLevel,
                                     @Query("BreakupByDate") String breakUpByDate,
                                     @Query("DurationInDays") String durationInDays,
                                     @Query("ReturnAllRowsWithoutPerfDataAlso") String returnAllRowsWithourPerfData,
                                     ApiCallback<List<CourseAnalysisPercentile>> callback);

    @GET("/GetTestCoverage")
    void getTestCoverage(@Query("idStudent") String studentId,
                         @Query("idCourse") String courseId,
                         ApiCallback<List<TestCoverage>> callback);

    @GET("/ViewPost")
    void getPostDetails(@Query("idUser") String userId,
                        @Query("postId") String postId,
                        ApiCallback<FourmCommentPostModel> callback);


    @GET("/FriendsList")
    void getFriendsList(@Query("idUser") String userId,
                        @Query("idCourse") String courseId,
                        ApiCallback<FriendsData> callback);

    @GET("/SearchFriends")
    void searchFriends(@Query("idUser") String userId,
                        @Query("idCourse") String courseId,
                        @Query("searchKey") String searchKey,
                        ApiCallback<List<FriendsData.Friend>> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/FriendRequest")
    void addRemoveFriend(@Body TypedString update, ApiCallback<CommonResponseModel> callback);

    @GET("/GetTestCoverage")
    void getTestCoverage(@Query("idStudent") String studentId,
                         @Query("idCourse") String courseId,
                         @Query("idSubject") String subjectId,
                         @Query("idChapter") String chapterId,
                         ApiCallback<List<TestCoverage>> callback);

    @GET("/ContentIndex")
    void getContentIndexData(@Query("idCourse") String courseId, @Query("idStudent") String studentId, ApiCallback<List<ContentIndex>> callback);

    @GET("/Content")
    void getContentData(@Query("idContents") String idContents, @Query("UpdateTime") String UpdateTime, ApiCallback<List<Content>> callback);

    @GET("/Content")
    List<Content> getContentData(@Query("idContents") String idContents, @Query("UpdateTime") String UpdateTime);

    @GET("/Exercise")
    void getExerciseData(@Query("idTopic") String idTopics, @Query("idCourse") String idCourse, @Query("idStudent") String idStudent, @Query("UpdateTime") String UpdateTime, ApiCallback<List<ExamModel>> callback);

    @GET("/Exercise")
    List<ExamModel> getExerciseData(@Query("idTopic") String idTopics, @Query("idCourse") String idCourse, @Query("idStudent") String idStudent, @Query("UpdateTime") String UpdateTime);

    @GET("/FlaggedQuestions")
    void getFlaggedQuestions(@Query("idStudent") String idStudent, @Query("idSubject") String idSubject, @Query("idChapter") String idChapter, @Query("UpdateTime") String UpdateTime, ApiCallback<List<ExamModel>> callback);

    @GET("/ScheduledTest")
    void getScheduledTests(@Query("idStudent") String idStudent, ApiCallback<List<ScheduledTest>> callback);

    @GET("/ScheduledTest")
    void getScheduledTestsList(@Query("idStudent") String idStudent, ApiCallback<ScheduledTestList> callback);

    @GET("/TestQuestionPaper")
    TestQuestionPaperResponse getTestQuestionPaper(@Query("idTestQuestionPaper") String idTestQuestionPaper, @Query("idTestAnswerPaper") String idTestAnswerPaper);

    @GET("/TestQuestionPaper")
    void getTestQuestionPaper(@Query("idTestQuestionPaper") String idTestQuestionPaper, @Query("idTestAnswerPaper") String idTestAnswerPaper, ApiCallback<TestQuestionPaperResponse> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/TestAnswerPaper")
    void submitTestAnswerPaper(@Body TypedString testAnswerPaper, ApiCallback<TestAnswerPaperResponse> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/TestAnswerPaper")
    TestAnswerPaperResponse submitTestAnswerPaper(@Body TypedString testAnswerPaper);

    @GET("/StandardExamsByCourse")
    void getStandardExamsByCourse(@Query("idCourse") String idCourse, @Query("idEntity") String idEntity, ApiCallback<List<Exam>> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/CustomExamTemplate")
    void postCustomExamTemplate(@Body TypedString insert, ApiCallback<PostExamTemplate> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/QuestionPaper")
    void postQuestionPaper(@Body TypedString insert, ApiCallback<PostQuestionPaper> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/FlaggedQuestions")
    void postFlaggedQuestions(@Body TypedString update, ApiCallback<PostFlaggedQuestions> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/UserProfile")
    void updateUserProfile(@Body TypedString userProfile, ApiCallback<EditProfileModel> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/StudentCourseList")
    void updateDefaultCourse(@Body TypedString update, ApiCallback<DefaultCourseResponse> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/StudentExamDetails")
    void updateExamDetails(@Body TypedString update, ApiCallback<UpdateExamDetailsResponse> callback);

    @GET("/Note")
    void getNotes(@Query("idStudent") String studentId, @Query("idSubject") String subjectId, @Query("idChapter") String chapterId, @Query("idTopic") String topicId, ApiCallback<List<Note>> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/Note")
    void addNote(@Body TypedString insert, ApiCallback<DefaultNoteResponse> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/Note")
    void updateNote(@Body TypedString update, ApiCallback<DefaultNoteResponse> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/Note")
    void deleteNote(@Body TypedString delete, ApiCallback<DefaultNoteResponse> callback);

    @GET("/ExamHistory")
    void getExamHistory(@Query("idStudent") String studentId, @Query("BeginRowNumber") String beginRowNum, @Query("RowCount") String rowCount, ApiCallback<List<ExamHistory>> callback);

    @GET("/UsageAnalysis")
    void getUsageAnalysis(@Query("idUser") String userId, ApiCallback<UsageAnalysis> callback);

    @GET("/Forums")
    void getAllPosts(@Query("idCourse") String courseID, @Query("idUser") String userID, @Query("type") String type, @Query("BeginRowNumber") String beginTowNumber, @Query("RowCount") String rowCount, ApiCallback<ArrayList<ForumPost>> callback);

    @GET("/Forums")
    void getMyPosts(@Query("idCourse") String courseID, @Query("idUser") String userID, ApiCallback<ArrayList<ForumPost>> callback);

    @GET("/Forums")
    void getForumPosts(@Query("idCourse") String courseID, @Query("idUser") String userID, @Query("type") String type, ApiCallback<ArrayList<ForumPost>> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/Forums")
    void addPostToForum(@Body TypedString update, ApiCallback<DefaultForumResponse> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/Forums")
    void addComment(@Body TypedString update, ApiCallback<CommonResponseModel> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/ForumDelete")
    void deleteForum(@Body TypedString delete, ApiCallback<CommonResponseModel> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/Bookmark")
    void addDeleteBookmark(@Body TypedString update, ApiCallback<CommonResponseModel> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/ContentUsage")
    void postContentUsage(@Body TypedString insert, ApiCallback<BaseResponseModel> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/ContentUsage")
    BaseResponseModel postContentUsage(@Body TypedString insert);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/EventLog")
    void postUserEvents(@Body TypedString update, ApiCallback<UserEventsResponse> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/EventLog")
    UserEventsResponse postUserEvents(@Body TypedString update);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/LikeForum")
    void addForumLike(@Body TypedString insert, ApiCallback<CommonResponseModel> callback);

    @GET("/MockTest")
    void getMockTests(@Query("idCourse") String courseID, @Query("idStudent") String studentId, ApiCallback<List<MockTest>> callback);

    @GET("/TestPaperIndex")
    TestPaperIndex getTestPaperIndex(@Query("idTestQuestionPaper") String questionPaperId, @Query("idTestAnswerPaper") String answerPaperId, @Query("doGetAllStage") String allStage);


    @GET("/TestPaperIndex")
    void getTestPaperIndex(@Query("idTestQuestionPaper") String questionPaperId, @Query("idTestAnswerPaper") String answerPaperId, @Query("doGetAllStage") String allStage, ApiCallback<TestPaperIndex> callback);

    @GET("/Welcome")
    void getWelcomeDetails(@Query("idStudent") String idStudent, ApiCallback<WelcomeDetails> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/CreateChallenge")
    void createChallenge(@Body TypedString insert, ApiCallback<CreateChallengeResponseModel> callback);

    @GET("/challengeTestDetails")
    void getchallengeTestDetails(@Query("idChallengeTest") String idChallengeTest, @Query("idCourse") String courseId, ApiCallback<ChallengeUserListResponse> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/ChallengeStatus")
    void updateChallengStatus(@Body TypedString update, ApiCallback<CommonResponseModel> callback);

    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("/challengeStart")
    void startChallenge(@Body TypedString update, ApiCallback<ChallengeStartResponseModel> callback);

    @GET("/challengeComplete")
    void completeChallenge(@Query("idChallengeTest") String idChallengeTest, @Query("idTestQuestionPaper") String testQuestionPaperId, @Query("idStudent") String studentId, ApiCallback<ChallengeCompleteResponseModel> callback);

    @GET("/challengeResults")
    void getChallengeResults(@Query("idChallengeTest") String idChallengeTest, ApiCallback<List<ChallengeUser>> callback);

    @GET("/PartTestGrid")
    void getPartTestGrid(@Query("idStudent") String studentId, @Query("idCourse") String courseId, @Query("idSubject") String subjectId, @Query("idExam") String idExam, ApiCallback<PartTestModel> callback);

    @GET("/ClientAppConfig")
    void getAppConfig(@Query("idUser") String studentId, ApiCallback<AppConfig> callback);

    @GET("/recommendedreading")
    void getRecommendedReading(@Query("idStudent") String studentId, @Query("idCourse") String courseId, @Query("BeginRowNumber") String beginRowNumber, @Query("RowCount") String rowCount, ApiCallback<List<RecommendedModel>> callback);

    @GET("/curriculum")
    void getCurriculumData(@Query("idStudent") String studentId, @Query("idCourse") String courseId, @Query("idEntity") String entityId, @Query("pageType") String pageType, @Query("soryBy") String sortBy, ApiCallback<CurriculumResponseModel> callback);
}