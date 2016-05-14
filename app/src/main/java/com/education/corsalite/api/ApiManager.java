package com.education.corsalite.api;

import android.content.Context;
import android.content.res.AssetManager;

import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.config.AppConfig;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.enums.NetworkMode;
import com.education.corsalite.models.MockTest;
import com.education.corsalite.models.ScheduledTestList;
import com.education.corsalite.models.db.reqres.LoginReqRes;
import com.education.corsalite.models.db.reqres.StudyCenterReqRes;
import com.education.corsalite.models.requestmodels.Bookmark;
import com.education.corsalite.models.requestmodels.CreateChallengeRequest;
import com.education.corsalite.models.requestmodels.ForumLikeRequest;
import com.education.corsalite.models.requestmodels.ForumModel;
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
import com.education.corsalite.models.responsemodels.TestAnswerPaper;
import com.education.corsalite.models.responsemodels.TestAnswerPaperResponse;
import com.education.corsalite.models.responsemodels.TestCoverage;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.models.responsemodels.UpdateExamDetailsResponse;
import com.education.corsalite.models.responsemodels.UsageAnalysis;
import com.education.corsalite.models.responsemodels.UserEventsResponse;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencySummaryResponse;
import com.education.corsalite.models.responsemodels.WelcomeDetails;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.SystemUtils;
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

    public void getPartTestGrid(String studentId, String courseId, String subjectId, ApiCallback<PartTestModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getPartTestGrid(studentId, courseId, subjectId, callback);
        }
    }

    public void postBookmark(Bookmark bookmark, ApiCallback<CommonResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().addDeleteBookmark(new TypedString(("Update=" + new Gson().toJson(bookmark))), callback);
        }
    }

    public void getAppConfig(String idUser, ApiCallback<com.education.corsalite.utils.AppConfig> callback) {
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
