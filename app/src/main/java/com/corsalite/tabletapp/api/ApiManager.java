package com.corsalite.tabletapp.api;

import android.content.Context;
import android.content.res.AssetManager;

import com.corsalite.tabletapp.cache.ApiCacheHolder;
import com.corsalite.tabletapp.config.AppConfig;
import com.corsalite.tabletapp.db.SugarDbManager;
import com.corsalite.tabletapp.enums.NetworkMode;
import com.corsalite.tabletapp.models.db.MockTest;
import com.corsalite.tabletapp.models.db.ScheduledTestList;
import com.corsalite.tabletapp.models.db.reqres.LoginReqRes;
import com.corsalite.tabletapp.models.db.reqres.StudyCenterReqRes;
import com.corsalite.tabletapp.models.requestmodels.Bookmark;
import com.corsalite.tabletapp.models.requestmodels.CreateChallengeRequest;
import com.corsalite.tabletapp.models.requestmodels.ForumLikeRequest;
import com.corsalite.tabletapp.models.requestmodels.ForumModel;
import com.corsalite.tabletapp.models.responsemodels.ChallengeCompleteResponseModel;
import com.corsalite.tabletapp.models.responsemodels.ChallengeStartResponseModel;
import com.corsalite.tabletapp.models.responsemodels.ChallengeUser;
import com.corsalite.tabletapp.models.responsemodels.ChallengeUserListResponse;
import com.corsalite.tabletapp.models.responsemodels.CommonResponseModel;
import com.corsalite.tabletapp.models.responsemodels.Content;
import com.corsalite.tabletapp.models.responsemodels.ContentIndex;
import com.corsalite.tabletapp.models.responsemodels.Course;
import com.corsalite.tabletapp.models.responsemodels.CourseAnalysis;
import com.corsalite.tabletapp.models.responsemodels.CourseAnalysisPercentile;
import com.corsalite.tabletapp.models.responsemodels.CreateChallengeResponseModel;
import com.corsalite.tabletapp.models.responsemodels.DefaultCourseResponse;
import com.corsalite.tabletapp.models.responsemodels.DefaultForumResponse;
import com.corsalite.tabletapp.models.responsemodels.DefaultNoteResponse;
import com.corsalite.tabletapp.models.responsemodels.EditProfileModel;
import com.corsalite.tabletapp.models.responsemodels.Exam;
import com.corsalite.tabletapp.models.responsemodels.ExamHistory;
import com.corsalite.tabletapp.models.responsemodels.ExamModel;
import com.corsalite.tabletapp.models.responsemodels.ForumPost;
import com.corsalite.tabletapp.models.responsemodels.FourmCommentPostModel;
import com.corsalite.tabletapp.models.responsemodels.FriendsData;
import com.corsalite.tabletapp.models.responsemodels.LoginResponse;
import com.corsalite.tabletapp.models.responsemodels.LogoutResponse;
import com.corsalite.tabletapp.models.responsemodels.Message;
import com.corsalite.tabletapp.models.responsemodels.Note;
import com.corsalite.tabletapp.models.responsemodels.PartTestModel;
import com.corsalite.tabletapp.models.responsemodels.PostExamTemplate;
import com.corsalite.tabletapp.models.responsemodels.PostExercise;
import com.corsalite.tabletapp.models.responsemodels.PostFlaggedQuestions;
import com.corsalite.tabletapp.models.responsemodels.PostQuestionPaper;
import com.corsalite.tabletapp.models.responsemodels.RecommendedModel;
import com.corsalite.tabletapp.models.responsemodels.ScheduledTest;
import com.corsalite.tabletapp.models.responsemodels.StudyCenter;
import com.corsalite.tabletapp.models.responsemodels.TestAnswerPaper;
import com.corsalite.tabletapp.models.responsemodels.TestAnswerPaperResponse;
import com.corsalite.tabletapp.models.responsemodels.TestCoverage;
import com.corsalite.tabletapp.models.responsemodels.TestPaperIndex;
import com.corsalite.tabletapp.models.responsemodels.UpdateExamDetailsResponse;
import com.corsalite.tabletapp.models.responsemodels.UsageAnalysis;
import com.corsalite.tabletapp.models.responsemodels.UserEventsResponse;
import com.corsalite.tabletapp.models.responsemodels.UserProfileResponse;
import com.corsalite.tabletapp.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.corsalite.tabletapp.models.responsemodels.VirtualCurrencySummaryResponse;
import com.corsalite.tabletapp.models.responsemodels.WelcomeDetails;
import com.corsalite.tabletapp.services.ApiClientService;
import com.corsalite.tabletapp.utils.SystemUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit.mime.TypedString;

/**
 * Created by vissu on 9/17/15.
 */
public class ApiManager {

    private static ApiManager instance;
    private Context context;
    private AssetManager assets;
    private ApiCacheHolder apiCacheHolder;

    public static ApiManager getInstance(Context context) {
        if (instance == null) {
            instance = new ApiManager();
            instance.apiCacheHolder = ApiCacheHolder.getInstance();
        }
        instance.context = context;
        instance.assets = context.getAssets();
        return instance;
    }

    public boolean isApiOnline() {
        return isNetworkConnected() && AppConfig.NETWORK_MODE == NetworkMode.ONLINE;
    }

    private boolean isNetworkConnected() {
        return SystemUtils.isNetworkConnected(context);
    }

    public void login(String loginId, String passwordHash, ApiCallback<LoginResponse> callback, boolean fetchFromDb) {
        apiCacheHolder.setLoginRequest(loginId, passwordHash);
        if (fetchFromDb || !isNetworkConnected()) {
            LoginReqRes reqRes = new LoginReqRes();
            reqRes.request = apiCacheHolder.loginRequest;
            SugarDbManager.get(context).getResponse(reqRes, callback);
        } else if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().login(loginId, passwordHash, callback);
        }
    }

    public void logout(String update, ApiCallback<LogoutResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().logout(new TypedString("Update=" + update), callback);
        }
    }

    public void getCourses(String studentId, ApiCallback<List<Course>> callback) {
        apiCacheHolder.setCoursesRequest(studentId);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getCourses(studentId, callback);
        } else if (!isNetworkConnected()) {
            SugarDbManager.get(context).getResponse(apiCacheHolder.courses, callback);
        }
    }

    public void getUsageAnalysis(String userId, ApiCallback<UsageAnalysis> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getUsageAnalysis(userId, callback);
        }
    }

    public void getCourseAnalysisData(String studentId, String courseId, String subjectId,
                                      String groupLevel, String breakUpByDate,
                                      String durationInDays, String returnAllRowsWithourPerfData,
                                      ApiCallback<List<CourseAnalysis>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getCourseAnalysis(studentId, courseId, subjectId, groupLevel, breakUpByDate, durationInDays, returnAllRowsWithourPerfData, callback);
        }
    }

    public void getCourseAnalysisAsPercentile(String studentId, String courseId, String subjectId,
                                              String groupLevel, String breakUpByDate,
                                              String durationInDays, String returnAllRowsWithourPerfData,
                                              ApiCallback<List<CourseAnalysisPercentile>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getCourseAnalysisPercentile(studentId, courseId, subjectId, groupLevel, breakUpByDate, durationInDays, returnAllRowsWithourPerfData, callback);
        }

    }

    public void getTestCoverage(String studentId, String courseId, ApiCallback<List<TestCoverage>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getTestCoverage(studentId, courseId, callback);
        }
    }

    public void getTestCoverage(String studentId, String courseId, String subjectId, String chapterId, ApiCallback<List<TestCoverage>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getTestCoverage(studentId, courseId, subjectId, chapterId, callback);
        }
    }

    public void getPostDetails(String userId, String postId, ApiCallback<FourmCommentPostModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getPostDetails(userId, postId, callback);
        }
    }
    public void getUserProfile(String studentId, ApiCallback<UserProfileResponse> callback) {
        apiCacheHolder.setUserProfileRequest(studentId);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getUserProfile(studentId, callback);
        } else if (!isNetworkConnected()) {
            SugarDbManager.get(context).getResponse(apiCacheHolder.userProfile, callback);
        }
    }

    public void getVirtualCurrencyBalance(String studentId, ApiCallback<VirtualCurrencyBalanceResponse> callback) {
        apiCacheHolder.setVirtualCurrencyBalanceRequest(studentId);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getVirtualCurrencyBalance(studentId, callback);
        } else if (!isNetworkConnected()) {
            SugarDbManager.get(context).getResponse(apiCacheHolder.virtualCurrencyBalance, callback);
        }
    }

    public void getVirtualCurrencyTransactions(String studentId, ApiCallback<VirtualCurrencySummaryResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getVirtualCurrencyTransactions(studentId, callback);
        }
    }

    public void getMessages(String studentId, ApiCallback<List<Message>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getMessages(studentId, callback);
        }
    }

    public void getExamHistory(String studentId, String beginRowNumber, String numRows, ApiCallback<List<ExamHistory>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getExamHistory(studentId, beginRowNumber, numRows, callback);
        }
    }

    public void getStudyCentreData(String studentId, String courseID, ApiCallback<List<StudyCenter>> callback) {
        apiCacheHolder.setStudyCenterRequest(studentId, courseID);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getCourseStudyCenterData(studentId, courseID, callback);
        } else if (!isNetworkConnected()) {
            StudyCenterReqRes reqRes = new StudyCenterReqRes();
            reqRes.request = apiCacheHolder.studyCenterRequest;
            SugarDbManager.get(context).getResponse(reqRes, callback);
        }
    }

    public void getContentIndex(String courseID, String studentId, ApiCallback<List<ContentIndex>> callback) {
        apiCacheHolder.setContentIndexRequest(studentId, courseID);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getContentIndexData(courseID, studentId, callback);
        } else if (!isNetworkConnected()) {
            SugarDbManager.get(context).getResponse(apiCacheHolder.contentIndex, callback);
        }
    }

    public void getContent(String idContents, String UpdateTime, ApiCallback<List<Content>> callback) {
        apiCacheHolder.setContentRequest(idContents, UpdateTime);
        if (isApiOnline()) {
            ApiClientService.get().getContentData(idContents, UpdateTime, callback);
        }
    }

    public List<Content> getContent(String idContent, String updateTime) {
        apiCacheHolder.setContentRequest(idContent, updateTime);
        if (isApiOnline()) {
            return ApiClientService.get().getContentData(idContent, updateTime);
        }
        return new ArrayList<>();
    }

    public void getExercise(String topicId, String courseId, String idStudent, String UpdateTime, ApiCallback<List<ExamModel>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getExerciseData(topicId, courseId, idStudent, UpdateTime, callback);
        }
    }

    public List<ExamModel> getExercise(String topicId, String courseId, String idStudent, String UpdateTime) {
        if (isApiOnline()) {
            return ApiClientService.get().getExerciseData(topicId, courseId, idStudent, UpdateTime);
        }
        return new ArrayList<>();
    }

    public void getFlaggedQuestions(String studentId, String subjectId, String chapterId, String UpdateTime, ApiCallback<List<ExamModel>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getFlaggedQuestions(studentId, subjectId, chapterId, UpdateTime, callback);
        }
    }

    public void getScheduledTests(String studentId, ApiCallback<List<ScheduledTest>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getScheduledTests(studentId, callback);
        }
    }

    public void getScheduledTestsList(String studentId, ApiCallback<ScheduledTestList> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getScheduledTestsList(studentId, callback);
        }
    }

    public void getFriendsList(String userId, String courseId, ApiCallback<FriendsData> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getFriendsList(userId, courseId, callback);
        }
    }

    public void getTestQuestionPaper(String testQuestionPaperId, String testAnswerPaperId, ApiCallback<List<ExamModel>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, callback);
        }
    }

    public void submitTestAnswerPaper(TestAnswerPaper testAnswerPaper, ApiCallback<TestAnswerPaperResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().submitTestAnswerPaper(new TypedString("Upsert=" + new Gson().toJson(testAnswerPaper)), callback);
        }
    }

    public void getStandardExamsByCourse(String courseId, String entityId, ApiCallback<List<Exam>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getStandardExamsByCourse(courseId, entityId, callback);
        }
    }

    public void getNotes(String studentId, String mSubjectId, String mChapterId, String mTopicId, ApiCallback<List<Note>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getNotes(studentId, mSubjectId, mChapterId, mTopicId, callback);
        }
    }

    public void addNote(String insertNote, ApiCallback<DefaultNoteResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().addNote(new TypedString("Insert=" + insertNote), callback);
        }
    }

    public void updateNote(String updateNote, ApiCallback<DefaultNoteResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().updateNote(new TypedString("Update=" + updateNote), callback);
        }
    }

    public void deleteNote(String deleteNote, ApiCallback<DefaultNoteResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().updateNote(new TypedString("Delete=" + deleteNote), callback);
        }
    }

    public void updateUserProfile(String userProfile, ApiCallback<EditProfileModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().updateUserProfile(new TypedString("Update=" + userProfile), callback);
        }
    }

    public void updateDefaultCourse(String defaultCourse, ApiCallback<DefaultCourseResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().updateDefaultCourse(new TypedString("Update=" + defaultCourse), callback);
        }
    }

    public void updateExamDetails(String examDetails, ApiCallback<UpdateExamDetailsResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().updateExamDetails(new TypedString("Update=" + examDetails), callback);
        }
    }

    public void postExerciseAnswer(String insert, ApiCallback<PostExercise> callback) {
        if (isApiOnline()) {
            ApiClientService.get().postExerciseAnswer(new TypedString("Insert=" + insert), callback);
        }
    }

    public void postCustomExamTemplate(String insert, ApiCallback<PostExamTemplate> callback) {
        if (isApiOnline()) {
            ApiClientService.get().postCustomExamTemplate(new TypedString("Insert=" + insert), callback);
        }
    }

    public void postQuestionPaper(String insert, ApiCallback<PostQuestionPaper> callback) {
        if (isApiOnline()) {
            ApiClientService.get().postQuestionPaper(new TypedString("Insert=" + insert), callback);
        }
    }

    public void postFlaggedQuestions(String update, ApiCallback<PostFlaggedQuestions> callback) {
        if (isApiOnline()) {
            ApiClientService.get().postFlaggedQuestions(new TypedString("Update=" + update), callback);
        }
    }

    public void getAllPosts(String courseID, String userID, String type, String BeginRowNumber, String mRowcount, ApiCallback<ArrayList<ForumPost>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getAllPosts(courseID, userID, type, BeginRowNumber, mRowcount, callback);
        }
    }

    public void getMyPosts(String courseID, String userID, ApiCallback<ArrayList<ForumPost>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getMyPosts(courseID, userID, callback);
        }
    }

    public void getMyComments(String courseID, String userID, String type, ApiCallback<ArrayList<ForumPost>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getForumPosts(courseID, userID, type, callback);
        }
    }

    public void addForumLike(ForumLikeRequest forumLikeRequest, ApiCallback<CommonResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().addForumLike(new TypedString("Insert=" + new Gson().toJson(forumLikeRequest)), callback);
        }
    }

    public void addEditForumPost(ForumModel forumPost, ApiCallback<DefaultForumResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().addPostToForum(new TypedString("Update=" + new Gson().toJson(forumPost)), callback);
        }
    }


    public void addComment(ForumModel forumPost, ApiCallback<CommonResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().addComment(new TypedString("Update=" + new Gson().toJson(forumPost)), callback);
        }
    }

    public void deleteForum(ForumLikeRequest forumdeleteRequest, ApiCallback<CommonResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().deleteForum(new TypedString("Delete=" + new Gson().toJson(forumdeleteRequest)), callback);
        }
    }

    public void postUserEvents(String insert, ApiCallback<UserEventsResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().postUserEvents(new TypedString("Insert=" + insert), callback);
        }
    }

    public void getMockTests(String courseId, String studentId, ApiCallback<List<MockTest>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getMockTests(courseId, studentId, callback);
        }
    }

    public void getTestPaperIndex(String questionPaperId, String answerPpaerId, String allPosts, ApiCallback<TestPaperIndex> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getTestPaperIndex(questionPaperId, answerPpaerId, allPosts, callback);
        }
    }

    public void getWelcomeDetails(String idStudent, ApiCallback<WelcomeDetails> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getWelcomeDetails(idStudent, callback);
        }
    }

    public void createChallenge(CreateChallengeRequest insert, ApiCallback<CreateChallengeResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().createChallenge(new TypedString("Insert=" + new Gson().toJson(insert)), callback);
        }
    }

    public void getChallengeTestDetails(String challengeTestId, String courseId, ApiCallback<ChallengeUserListResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getchallengeTestDetails(challengeTestId, courseId, callback);
        }
    }

    public void postChallengeStatus(String update, ApiCallback<CommonResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().updateChallengStatus(new TypedString("Update=" + update), callback);
        }
    }

    public void postChallengeStart(String update, ApiCallback<ChallengeStartResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().startChallenge(new TypedString("Update=" + update), callback);
        }
    }

    public void completeChallengeTest(String challengeTestId, String testQuestionPaperId, String studentId, ApiCallback<ChallengeCompleteResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().completeChallenge(challengeTestId, testQuestionPaperId, studentId, callback);
        }
    }

    public void getChallengeResults(String challengeTestId, ApiCallback<List<ChallengeUser>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getChallengeResults(challengeTestId, callback);
        }
    }

    public void getPartTestGrid(String studentId, String courseId, String subjectId, String idExam, ApiCallback<PartTestModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getPartTestGrid(studentId, courseId, subjectId, idExam, callback);
        }
    }

    public void postBookmark(Bookmark bookmark, ApiCallback<CommonResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().addDeleteBookmark(new TypedString(("Update=" + new Gson().toJson(bookmark))), callback);
        }
    }

    public void getAppConfig(String idUser, ApiCallback<com.corsalite.tabletapp.utils.AppConfig> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getAppConfig(idUser, callback);
        }
    }

    public void getRecommendedReading(String studentId, String courseId, String beginRowNumber, String rowCount, ApiCallback<List<RecommendedModel>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getRecommendedReading(studentId, courseId, beginRowNumber, rowCount, callback);
        }
    }
}
