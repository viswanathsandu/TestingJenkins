package com.education.corsalite.api;

import android.content.Context;
import android.content.res.AssetManager;

import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.config.AppConfig;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.enums.NetworkMode;
import com.education.corsalite.models.MockTest;
import com.education.corsalite.models.ScheduledTestList;
import com.education.corsalite.models.requestmodels.ForumLikeRequest;
import com.education.corsalite.models.responsemodels.CommonResponseModel;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.CourseAnalysis;
import com.education.corsalite.models.responsemodels.CourseAnalysisPercentile;
import com.education.corsalite.models.responsemodels.DefaultCourseResponse;
import com.education.corsalite.models.responsemodels.DefaultNoteResponse;
import com.education.corsalite.models.responsemodels.EditProfileModel;
import com.education.corsalite.models.responsemodels.ExamHistory;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.ExamModels;
import com.education.corsalite.models.responsemodels.ForumPost;
import com.education.corsalite.models.responsemodels.FriendsData;
import com.education.corsalite.models.responsemodels.LoginResponse;
import com.education.corsalite.models.responsemodels.LogoutResponse;
import com.education.corsalite.models.responsemodels.Message;
import com.education.corsalite.models.responsemodels.Note;
import com.education.corsalite.models.responsemodels.PostExamTemplate;
import com.education.corsalite.models.responsemodels.PostExercise;
import com.education.corsalite.models.responsemodels.PostFlaggedQuestions;
import com.education.corsalite.models.responsemodels.PostQuestionPaper;
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
import com.education.corsalite.utils.FileUtils;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.MockUtils;
import com.education.corsalite.utils.SystemUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
        L.info("Network Mode is " + AppConfig.NETWORK_MODE.getValue());
        return AppConfig.NETWORK_MODE == NetworkMode.ONLINE;
    }

    public boolean isNetworkConnected() {
        return SystemUtils.isNetworkConnected(context);
    }

    public void login(String loginId, String passwordHash, ApiCallback<LoginResponse> callback, boolean fetchFromDb) {
        apiCacheHolder.setLoginRequest(loginId, passwordHash);
        if(fetchFromDb || !isNetworkConnected()) {
            DbManager.getInstance(context).getResponse(apiCacheHolder.login, callback);
        } else if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().login(loginId, passwordHash, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/login.json");
            L.info("Response for 'api/login.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, LoginResponse.class), MockUtils.getRetrofitResponse());
        }
    }

    public void logout(String update, ApiCallback<LogoutResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().logout(update, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/logout.json");
            L.info("Response for 'api/logout.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, LogoutResponse.class), MockUtils.getRetrofitResponse());
        }
    }

    public void getCourses(String studentId, ApiCallback<List<Course>> callback) {
        apiCacheHolder.setCoursesRequest(studentId);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getCourses(studentId, callback);
        } else if(!isNetworkConnected()) {
            DbManager.getInstance(context).getResponse(apiCacheHolder.courses, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/courses.json");
            L.info("Response for 'api/courses.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, List.class), MockUtils.getRetrofitResponse());
        }
    }

    public void getUsageAnalysis(String userId,ApiCallback<UsageAnalysis> callback){
        if(isApiOnline()){
            ApiClientService.get().getUsageAnalysis(userId, callback);
        }else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/usage_analysis.json");
            L.info("Response for 'api/usage_analysis.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, UsageAnalysis.class), MockUtils.getRetrofitResponse());
        }
    }

    public void getCourseAnalysisData(String studentId, String courseId, String subjectId,
                                      String groupLevel, String breakUpByDate,
                                      String durationInDays, String returnAllRowsWithourPerfData,
                                      ApiCallback<List<CourseAnalysis>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getCourseAnalysis(studentId, courseId, subjectId, groupLevel, breakUpByDate, durationInDays, returnAllRowsWithourPerfData, callback);
        } else {
            String jsonResponse = null;
            if (groupLevel.equalsIgnoreCase("chapter")) {
                jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/course_analysis_by_chapter.json");
                L.info("Response for 'api/course_analysis_by_chapter.json' is " + jsonResponse);
            } else if (groupLevel.equalsIgnoreCase("dates")) {
                jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/course_analysis_by_date.json");
                L.info("Response for 'api/course_analysis_by_dates.json' is " + jsonResponse);
            }
            JsonParser jsonParser = new JsonParser();
            JsonArray analyticsarray = jsonParser.parse(jsonResponse).getAsJsonArray();
            List<CourseAnalysis> courseAnalysisList = new ArrayList<>();
            for (JsonElement aUser : analyticsarray) {
                CourseAnalysis courseAnalysis = new Gson().fromJson(aUser, CourseAnalysis.class);
                courseAnalysisList.add(courseAnalysis);
            }
            callback.success(courseAnalysisList, MockUtils.getRetrofitResponse());
        }

    }

    public void getCourseAnalysisAsPercentile(String studentId, String courseId, String subjectId,
                                              String groupLevel, String breakUpByDate,
                                              String durationInDays, String returnAllRowsWithourPerfData,
                                              ApiCallback<List<CourseAnalysisPercentile>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getCourseAnalysisPercentile(studentId, courseId, subjectId, groupLevel, breakUpByDate, durationInDays, returnAllRowsWithourPerfData, callback);
        } else {
            String jsonResponse = null;
            jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/course_analysis_percentile.json");
            L.info("Response for 'api/course_analysis_percentile.json' is " + jsonResponse);
            JsonParser jsonParser = new JsonParser();
            JsonArray analyticsarray = jsonParser.parse(jsonResponse).getAsJsonArray();
            List<CourseAnalysisPercentile> courseAnalysisList = new ArrayList<>();
            for (JsonElement aUser : analyticsarray) {
                CourseAnalysisPercentile courseAnalysisPercentile = new Gson().fromJson(aUser, CourseAnalysisPercentile.class);
                courseAnalysisList.add(courseAnalysisPercentile);
            }
            callback.success(courseAnalysisList, MockUtils.getRetrofitResponse());
        }

    }

    public void getTestCoverage(String studentId, String courseId, ApiCallback<List<TestCoverage>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getTestCoverage(studentId, courseId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/test_coverage.json");
            L.info("Response for 'api/test_coverage.json' is " + jsonResponse);
            JsonParser jsonParser = new JsonParser();
            JsonArray analyticsarray = jsonParser.parse(jsonResponse).getAsJsonArray();
            List<TestCoverage> testCoverageList = new ArrayList<>();
            for (JsonElement aUser : analyticsarray) {
                TestCoverage testCoverage = new Gson().fromJson(aUser, TestCoverage.class);
                testCoverageList.add(testCoverage);
            }
            callback.success(testCoverageList, MockUtils.getRetrofitResponse());
        }

    }

    public void getTestCoverage(String studentId, String courseId, String subjectId, String chapterId, ApiCallback<List<TestCoverage>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getTestCoverage(studentId, courseId, subjectId, chapterId, callback);
        }
    }

    public void getUserProfile(String studentId, ApiCallback<UserProfileResponse> callback) {
        apiCacheHolder.setUserProfileRequest(studentId);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getUserProfile(studentId, callback);
        } else if(!isNetworkConnected()) {
            DbManager.getInstance(context).getResponse(apiCacheHolder.userProfile, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/user_profile.json");
            L.info("Response for 'api/user_profile.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, UserProfileResponse.class), MockUtils.getRetrofitResponse());
        }
    }

    public void getVirtualCurrencyBalance(String studentId, ApiCallback<VirtualCurrencyBalanceResponse> callback) {
        apiCacheHolder.setVirtualCurrencyBalanceRequest(studentId);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getVirtualCurrencyBalance(studentId, callback);
        } else if(!isNetworkConnected()) {
            DbManager.getInstance(context).getResponse(apiCacheHolder.virtualCurrencyBalance, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/virtual_currency_balance.json");
            L.info("Response for 'api/virtual_currency_balance.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, VirtualCurrencyBalanceResponse.class), MockUtils.getRetrofitResponse());
        }
    }

    public void getVirtualCurrencyTransactions(String studentId, ApiCallback<VirtualCurrencySummaryResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getVirtualCurrencyTransactions(studentId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/virtual_currency_summary.json");
            L.info("Response for 'api/virtual_currency_summary.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, VirtualCurrencySummaryResponse.class), MockUtils.getRetrofitResponse());
        }
    }

    public void getMessages(String studentId, ApiCallback<List<Message>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getMessages(studentId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/messages.json");
            L.info("Response for 'api/messages.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, List.class), MockUtils.getRetrofitResponse());
        }
    }

    public void getExamHistory(String studentId,String beginRowNumber,String numRows, ApiCallback<List<ExamHistory>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getExamHistory(studentId, beginRowNumber, numRows, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/exam_history.json");
            L.info("Response for 'api/exam_history.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, List.class), MockUtils.getRetrofitResponse());
        }
    }


    public void getStudyCentreData(String studentId, String courseID, ApiCallback<List<StudyCenter>> callback) {
        apiCacheHolder.setStudyCenterRequest(studentId, courseID);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getCourseStudyCenterData(studentId, courseID, callback);
        } else if(!isNetworkConnected()) {
            DbManager.getInstance(context).getResponse(apiCacheHolder.studyCenter, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/studycentre.json");
            System.out.print("Response for 'api/studycentre.json' is " + jsonResponse);
            callback.success(new Gson().fromJson(jsonResponse, List.class), MockUtils.getRetrofitResponse());
        }
    }

    public void getContentIndex(String courseID, String studentId, ApiCallback<List<ContentIndex>> callback) {
        apiCacheHolder.setContentIndexRequest(studentId, courseID);
        if (isApiOnline() && isNetworkConnected()) {
            ApiClientService.get().getContentIndexData(courseID, studentId, callback);
        } else if(!isNetworkConnected()) {
            DbManager.getInstance(context).getResponse(apiCacheHolder.contentIndex, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/content_index.json");
            Type listType = new TypeToken<ArrayList<ContentIndex>>() {
            }.getType();
            List<ContentIndex> contentIndexes = new Gson().fromJson(jsonResponse, listType);
            callback.success(contentIndexes, MockUtils.getRetrofitResponse());
        }
    }

    public void getContent(String idContents, String UpdateTime, ApiCallback<List<Content>> callback) {
        apiCacheHolder.setContentRequest(idContents, UpdateTime);
        if (isApiOnline()) {
            ApiClientService.get().getContentData(idContents, UpdateTime, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/content_data.json");
            System.out.print("Response for 'api/content_data.json' is " + jsonResponse);
            Type listType = new TypeToken<ArrayList<Content>>() {
            }.getType();
            List<Content> contents = new Gson().fromJson(jsonResponse, listType);
            callback.success(contents, MockUtils.getRetrofitResponse());
        }
    }

    public void getExercise(String topicId, String courseId, String idStudent, String UpdateTime, ApiCallback<List<ExamModel>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getExerciseData(topicId, courseId, idStudent, UpdateTime, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/exercise.json");
            System.out.print("Response for 'api/exercise.json' is " + jsonResponse);
            Type listType = new TypeToken<ArrayList<ExamModel>>() {
            }.getType();
            List<ExamModel> examModels = new Gson().fromJson(jsonResponse, listType);
            callback.success(examModels, MockUtils.getRetrofitResponse());
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
        if(isApiOnline()) {
            ApiClientService.get().getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, callback);
        }
    }

    public void submitTestAnswerPaper(TestAnswerPaper testAnswerPaper, ApiCallback<TestAnswerPaperResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().submitTestAnswerPaper(new Gson().toJson(testAnswerPaper), callback);
        }
    }

    public void getStandardExamsByCourse(String courseId, String entityId, ApiCallback<List<ExamModels>> callback) {
        if(isApiOnline()) {
            ApiClientService.get().getStandardExamsByCourse(courseId, entityId, callback);
        }
    }

    public void getNotes(String studentId, String mSubjectId, String mChapterId, String mTopicId, ApiCallback<List<Note>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getNotes(studentId, mSubjectId, mChapterId, mTopicId, callback);
        } else {
            String jsonResponse = FileUtils.loadJSONFromAsset(assets, "api/notes.json");
            System.out.print("Response for 'api/notes.json' is " + jsonResponse);
            Type listType = new TypeToken<ArrayList<Note>>() {
            }.getType();
            List<Note> notes = new Gson().fromJson(jsonResponse, listType);
            callback.success(notes, MockUtils.getRetrofitResponse());
        }
    }

    public void addNote(String insertNote, ApiCallback<DefaultNoteResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().addNote(insertNote, callback);
        }
    }

    public void updateNote(String updateNote, ApiCallback<DefaultNoteResponse> callback) {
        if(isApiOnline()) {
            ApiClientService.get().updateNote(updateNote, callback);
        }
    }

    public void updateUserProfile(String userProfile, ApiCallback<EditProfileModel> callback) {
        if (isApiOnline()) {
            ApiClientService.get().updateUserProfile(userProfile, callback);
        }
    }

    public void updateDefaultCourse(String defaultCourse, ApiCallback<DefaultCourseResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().updateDefaultCourse(defaultCourse, callback);
        }
    }

    public void updateExamDetails(String examDetails, ApiCallback<UpdateExamDetailsResponse> callback) {
        if (isApiOnline()) {
            ApiClientService.get().updateExamDetails(examDetails, callback);
        }
    }

    public void postExerciseAnswer(String insert, ApiCallback<PostExercise> callback) {
        if (isApiOnline()) {
            ApiClientService.get().postExerciseAnswer(insert, callback);
        }
    }

    public void postCustomExamTemplate(String insert, ApiCallback<PostExamTemplate> callback) {
        if (isApiOnline()) {
            ApiClientService.get().postCustomExamTemplate(insert, callback);
        }
    }

    public void postQuestionPaper(String insert, ApiCallback<PostQuestionPaper> callback) {
        if (isApiOnline()) {
            ApiClientService.get().postQuestionPaper(insert, callback);
        }
    }

    public void postFlaggedQuestions(String update, ApiCallback<PostFlaggedQuestions> callback) {
        if (isApiOnline()) {
            ApiClientService.get().postFlaggedQuestions(update, callback);
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

    public void addForumLike(ForumLikeRequest forumLikeRequest, ApiCallback<CommonResponseModel> callback){
        if(isApiOnline()){
            ApiClientService.get().addForumLike(new Gson().toJson(forumLikeRequest), callback);
        }
    }

    public void deleteForum(ForumLikeRequest forumdeleteRequest, ApiCallback<CommonResponseModel> callback){
        if(isApiOnline()){
            ApiClientService.get().deleteForum(new Gson().toJson(forumdeleteRequest), callback);
        }
    }

    public void postUserEvents(String insert, ApiCallback<UserEventsResponse> callback){
        if(isApiOnline()){
            ApiClientService.get().postUserEvents(insert, callback);
        }
    }

    public void getMockTests(String courseId, String studentId, ApiCallback<List<MockTest>> callback) {
        if (isApiOnline()) {
            ApiClientService.get().getMockTests(courseId, studentId, callback);
        }
    }

    public void getTestPaperIndex(String questionPaperId,String answerPpaerId,String allPosts,ApiCallback<TestPaperIndex> callback){
        if(isApiOnline()){
            ApiClientService.get().getTestPaperIndex(questionPaperId, answerPpaerId, allPosts, callback);
        }
    }

    public void getWelcomeDetails(String idStudent,ApiCallback<WelcomeDetails> callback){
        if(isApiOnline()){
            ApiClientService.get().getWelcomeDetails(idStudent,callback);
        }
    }
}
