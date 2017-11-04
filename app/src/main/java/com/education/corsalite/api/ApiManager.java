package com.education.corsalite.api;

import android.content.Context;
import android.content.res.AssetManager;

import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.db.MockTest;
import com.education.corsalite.models.db.ScheduledTestList;
import com.education.corsalite.models.db.reqres.AppConfigReqRes;
import com.education.corsalite.models.db.reqres.AppentityconfigReqRes;
import com.education.corsalite.models.db.reqres.ContentIndexReqRes;
import com.education.corsalite.models.db.reqres.ContentReqRes;
import com.education.corsalite.models.db.reqres.CoursesReqRes;
import com.education.corsalite.models.db.reqres.LoginReqRes;
import com.education.corsalite.models.db.reqres.ScheduleTestsReqRes;
import com.education.corsalite.models.db.reqres.StudyCenterReqRes;
import com.education.corsalite.models.db.reqres.TestSeriesReqRes;
import com.education.corsalite.models.db.reqres.UserProfileReqRes;
import com.education.corsalite.models.db.reqres.WelcomeReqRes;
import com.education.corsalite.models.db.reqres.requests.AppConfigRequest;
import com.education.corsalite.models.db.reqres.requests.AppEntityConfigRequest;
import com.education.corsalite.models.requestmodels.AddRemoveFriendRequest;
import com.education.corsalite.models.requestmodels.Bookmark;
import com.education.corsalite.models.requestmodels.ClientEntityConfigRequest;
import com.education.corsalite.models.requestmodels.CreateChallengeRequest;
import com.education.corsalite.models.requestmodels.ForumLikeRequest;
import com.education.corsalite.models.requestmodels.ForumModel;
import com.education.corsalite.models.requestmodels.TestSeriesRequest;
import com.education.corsalite.models.responsemodels.BaseResponseModel;
import com.education.corsalite.models.responsemodels.ChallengeCompleteResponseModel;
import com.education.corsalite.models.responsemodels.ChallengeStartResponseModel;
import com.education.corsalite.models.responsemodels.ChallengeUser;
import com.education.corsalite.models.responsemodels.ChallengeUserListResponse;
import com.education.corsalite.models.responsemodels.ClientEntityAppConfig;
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
import com.education.corsalite.models.responsemodels.ExamResultSummaryResponse;
import com.education.corsalite.models.responsemodels.ForumPost;
import com.education.corsalite.models.responsemodels.FourmCommentPostModel;
import com.education.corsalite.models.responsemodels.FriendsData;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.LogoutResponse;
import com.education.corsalite.models.responsemodels.MessageResponse;
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
import com.education.corsalite.models.responsemodels.TestQuestionPaperResponse;
import com.education.corsalite.models.responsemodels.TestSeriesResponse;
import com.education.corsalite.models.responsemodels.UpdateExamDetailsResponse;
import com.education.corsalite.models.responsemodels.UsageAnalysis;
import com.education.corsalite.models.responsemodels.UserEventsResponse;
import com.education.corsalite.models.responsemodels.UserProfileResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencyBalanceResponse;
import com.education.corsalite.models.responsemodels.VirtualCurrencySummaryResponse;
import com.education.corsalite.models.responsemodels.WelcomeDetails;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.FileUtils;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.MockUtils;
import com.education.corsalite.utils.SystemUtils;

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
        return isNetworkConnected();
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
            ApiClientService.get().login(loginId, passwordHash, passwordHash, callback);
        }
    }

    public void logout(String update, ApiCallback<LogoutResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().logout(new TypedString("Update=" + update), callback);
        }
    }

    public void getCourses(String studentId, boolean offline, ApiCallback<List<Course>> callback) {
        apiCacheHolder.setCoursesRequest(studentId);
        if (!isNetworkConnected() || offline) {
            CoursesReqRes reqRes = new CoursesReqRes();
            reqRes.request = apiCacheHolder.courseRequest;
            SugarDbManager.get(context).getResponse(reqRes, callback);
        } else if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getCourses(studentId, callback);
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
            ApiClientService.get().getCourseAnalysis(studentId, courseId, subjectId, groupLevel, breakUpByDate, durationInDays, returnAllRowsWithourPerfData,
                    callback);
        }
    }

    public void getCourseAnalysisAsPercentile(String studentId, String courseId, String subjectId,
            String groupLevel, String breakUpByDate,
            String durationInDays, String returnAllRowsWithourPerfData,
            ApiCallback<List<CourseAnalysisPercentile>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getCourseAnalysisPercentile(studentId, courseId, subjectId, groupLevel, breakUpByDate, durationInDays,
                    returnAllRowsWithourPerfData, callback);
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

    public void getWelcomeDetails(String idStudent, ApiCallback<WelcomeDetails> callback) {
        apiCacheHolder.setWelcomeRequest(idStudent);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getWelcomeDetails(idStudent, callback);
        } else if (!isNetworkConnected()) {
            WelcomeReqRes reqRes = new WelcomeReqRes();
            reqRes.request = apiCacheHolder.welcomeRequest;
            SugarDbManager.get(context).getResponse(reqRes, callback);
        }
    }

    public void getUserProfile(String studentId, String entityId, ApiCallback<UserProfileResponse> callback) {
        apiCacheHolder.setUserProfileRequest(studentId);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getUserProfile(studentId, entityId, callback);
        } else if (!isNetworkConnected()) {
            UserProfileReqRes reqRes = new UserProfileReqRes();
            reqRes.request = apiCacheHolder.userProfileRequest;
            SugarDbManager.get(context).getResponse(reqRes, callback);
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

    public void getMessages(String studentId, String courseId, String entityId, ApiCallback<MessageResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getMessages(studentId, courseId, entityId, callback);
        }
    }

    public void getExamHistory(String courseId, String studentId, String beginRowNumber, String numRows, ApiCallback<List<ExamHistory>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getExamHistory(courseId, studentId, beginRowNumber, numRows, callback);
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
            ContentIndexReqRes reqRes = new ContentIndexReqRes();
            reqRes.request = apiCacheHolder.contentIndexRequest;
            SugarDbManager.get(context).getResponse(reqRes, callback);
        }
    }

    public void getContent(String idStudent, String idContents, String UpdateTime, ApiCallback<List<Content>> callback) {
        apiCacheHolder.setContentRequest(idContents, UpdateTime);
        if (isApiOnline()) {
            ApiClientService.get().getContentData(idStudent, idContents, UpdateTime, callback);
        } else if (!isNetworkConnected()) {
            ContentReqRes reqRes = new ContentReqRes();
            reqRes.request = apiCacheHolder.contentRequest;
            SugarDbManager.get(context).getResponse(reqRes, callback);
        }
    }

    public List<Content> getContent(String idStudent, String idContent, String updateTime) {
        apiCacheHolder.setContentRequest(idContent, updateTime);
        if (isApiOnline()) {
            try {
                return ApiClientService.get().getContentData(idStudent, idContent, updateTime);
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
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
            try {
                return ApiClientService.get().getExerciseData(topicId, courseId, idStudent, UpdateTime);
            } catch (Exception e) {
                L.error(e.getMessage(), e);
            }
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
        apiCacheHolder.setScheduleTestsRequest(studentId);
        if (isApiOnline()) {
            ApiClientService.get().getScheduledTestsList(studentId, callback);
        } else if (!isNetworkConnected()) {
            ScheduleTestsReqRes reqRes = new ScheduleTestsReqRes();
            reqRes.request = apiCacheHolder.scheduleTestsRequest;
            SugarDbManager.get(context).getResponse(reqRes, callback);
        }
    }

    public void getFriendsList(String userId, String courseId, String courseInstanceId, ApiCallback<FriendsData> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getFriendsList(userId, courseId, courseInstanceId, callback);
        }
    }

    public void searchFriends(String userId, String courseId, String searchKey, String courseInstanceId, ApiCallback<List<FriendsData.Friend>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().searchFriends(userId, courseId, searchKey, courseInstanceId, callback);
        }
    }

    public void addFriend(String userId, String friendUserId, ApiCallback<CommonResponseModel> callback) {
        if (isApiOnline()) {
            AddRemoveFriendRequest request = new AddRemoveFriendRequest(userId, friendUserId, "AddFriend");
            ApiClientService.get().addRemoveFriend(new TypedString("Update=" + Gson.get().toJson(request)), callback);
        }
    }

    public void unFriend(String userId, String friendUserId, ApiCallback<CommonResponseModel> callback) {
        if (isApiOnline()) {
            AddRemoveFriendRequest request = new AddRemoveFriendRequest(userId, friendUserId, "UnFriend");
            ApiClientService.get().addRemoveFriend(new TypedString("Update=" + Gson.get().toJson(request)), callback);
        }
    }

    public TestQuestionPaperResponse getTestQuestionPaper(String testQuestionPaperId, String testAnswerPaperId, String studentId) {
        if (isApiOnline()) {
            return ApiClientService.get().getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, studentId);
        }
        return null;
    }

    public void getTestQuestionPaper(String testQuestionPaperId, String testAnswerPaperId, String studentId, ApiCallback<TestQuestionPaperResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, studentId, callback);
        }
    }

    public TestAnswerPaperResponse submitTestAnswerPaper(TestAnswerPaper testAnswerPaper) {
        if (isApiOnline()) {
            return ApiClientService.get().submitTestAnswerPaper(new TypedString("Upsert=" + Gson.get().toJson(testAnswerPaper)));
        }
        return null;
    }

    public void submitTestAnswerPaper(TestAnswerPaper testAnswerPaper, ApiCallback<TestAnswerPaperResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().submitTestAnswerPaper(new TypedString("Upsert=" + Gson.get().toJson(testAnswerPaper)), callback);
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
            ApiClientService.get().addForumLike(new TypedString("Insert=" + Gson.get().toJson(forumLikeRequest)), callback);
        }
    }

    public void addEditForumPost(ForumModel forumPost, ApiCallback<DefaultForumResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().addPostToForum(new TypedString("Update=" + Gson.get().toJson(forumPost)), callback);
        }
    }


    public void addComment(ForumModel forumPost, ApiCallback<CommonResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().addComment(new TypedString("Update=" + Gson.get().toJson(forumPost)), callback);
        }
    }

    public void deleteForum(ForumLikeRequest forumdeleteRequest, ApiCallback<CommonResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().deleteForum(new TypedString("Delete=" + Gson.get().toJson(forumdeleteRequest)), callback);
        }
    }

    public void postUserEvents(String update, ApiCallback<UserEventsResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().postUserEvents(new TypedString("Insert=" + update), callback);
        }
    }

    public UserEventsResponse postUserEvents(String update) {
        if (isApiOnline()) {
            return ApiClientService.get().postUserEvents(new TypedString("Insert=" + update));
        }
        return null;
    }

    public void postContentUsage(String insert, ApiCallback<BaseResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().postContentUsage(new TypedString("Insert=" + insert), callback);
        }
    }

    public BaseResponseModel postContentUsage(String insert) {
        if (isApiOnline()) {
            return ApiClientService.get().postContentUsage(new TypedString("Insert=" + insert));
        }
        return null;
    }

    public void getMockTests(String courseId, String studentId, ApiCallback<List<MockTest>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getMockTests(courseId, studentId, callback);
        }
    }

    public TestPaperIndex getTestPaperIndex(String questionPaperId, String answerPaperId, String allPosts) {
        if (isApiOnline()) {
            return ApiClientService.get().getTestPaperIndex(questionPaperId, answerPaperId, allPosts);
        }
        return null;
    }

    public void getTestPaperIndex(String questionPaperId, String answerPpaerId, String allPosts, ApiCallback<TestPaperIndex> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getTestPaperIndex(questionPaperId, answerPpaerId, allPosts, callback);
        }
    }

    public void createChallenge(CreateChallengeRequest insert, ApiCallback<CreateChallengeResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().createChallenge(new TypedString("Insert=" + Gson.get().toJson(insert)), callback);
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

    public void completeChallengeTest(String challengeTestId, String testQuestionPaperId, String studentId,
            ApiCallback<ChallengeCompleteResponseModel> callback) {
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
            ApiClientService.get().addDeleteBookmark(new TypedString(("Update=" + Gson.get().toJson(bookmark))), callback);
        }
    }

    public void getAppConfig(ApiCallback<com.education.corsalite.models.db.AppConfig> callback) {
        apiCacheHolder.appConfigRequest = new AppConfigRequest();
        if (isApiOnline()) {
            ApiClientService.get().getAppConfig(callback);
        } else if (!isNetworkConnected()) {
            AppConfigReqRes reqRes = new AppConfigReqRes();
            reqRes.request = apiCacheHolder.appConfigRequest;
            SugarDbManager.get(context).getResponse(reqRes, callback);
        } else {
            String jsonResponse = FileUtils.get(context).loadJSONFromAsset(context.getAssets(), "config.json");
            com.education.corsalite.models.db.AppConfig config = Gson.get().fromJson(jsonResponse, com.education.corsalite.models.db.AppConfig.class);
            callback.success(config, MockUtils.getRetrofitResponse());
        }
    }

    public void getRecommendedReading(String studentId, String courseId, String beginRowNumber, String rowCount, ApiCallback<List<RecommendedModel>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getRecommendedReading(studentId, courseId, beginRowNumber, rowCount, callback);
        }
    }

    public void getCurriculumData(String studentId, String courseId, String entityId, String pageType, String sortBy,
            ApiCallback<CurriculumResponseModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getCurriculumData(studentId, courseId, entityId, pageType, sortBy, callback);
        }
    }

    public void getClientEntityAppConfig(String userId, String entityId, ApiCallback<ClientEntityAppConfig> callback) {
        apiCacheHolder.appentityconfigRequest = new AppEntityConfigRequest(userId, entityId);
        if (isApiOnline()) {
            ApiClientService.get().getClientEntityAppConfig(userId, entityId, callback);
        } else if (!isNetworkConnected()) {
            AppentityconfigReqRes reqRes = new AppentityconfigReqRes();
            reqRes.request = apiCacheHolder.appentityconfigRequest;
            SugarDbManager.get(context).getResponse(reqRes, callback);
        } else {
            String jsonResponse = FileUtils.get(context).loadJSONFromAsset(context.getAssets(), "config.json");
            ClientEntityAppConfig config = Gson.get().fromJson(jsonResponse, ClientEntityAppConfig.class);
            callback.success(config, MockUtils.getRetrofitResponse());
        }
    }

    public void postClientEntityAppConfig(String userId, String deviceId, ApiCallback<CommonResponseModel> callback) {
        if (isApiOnline()) {
            ClientEntityConfigRequest request = new ClientEntityConfigRequest(userId, deviceId);
            ApiClientService.get().postClientEntityAppConfig(new TypedString("Update=" + Gson.get().toJson(request)), callback);
        }
    }

    public void getTestSeries(String idStudent, String idCourse, String idCourseInstance, ApiCallback<TestSeriesResponse> callback) {
        TestSeriesRequest request = new TestSeriesRequest(idStudent, idCourse, idCourseInstance);
        apiCacheHolder.testSeriesRequest = request;
        if (isApiOnline()) {
            ApiClientService.get().getTestSeries(new TypedString("Update=" + Gson.get().toJson(request)), callback);
        } else if (!isNetworkConnected()) {
            TestSeriesReqRes reqRes = new TestSeriesReqRes();
            reqRes.request = apiCacheHolder.testSeriesRequest;
            SugarDbManager.get(context).getResponse(reqRes, callback);
        }
    }

    public void getExamResultSummaryData(String testAnswerPAperIds, String studentId, ApiCallback<ExamResultSummaryResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getExamResultSummary(testAnswerPAperIds, studentId, callback);
        }
    }
}
