package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.enums.Tests;
import com.education.corsalite.helpers.ExamEngineHelper;
import com.education.corsalite.listener.OnExamLoadCallback;
import com.education.corsalite.models.ExerciseOfflineModel;
import com.education.corsalite.models.MockTest;
import com.education.corsalite.models.OfflineTestModel;
import com.education.corsalite.models.ScheduledTestList;
import com.education.corsalite.models.examengine.BaseTest;
import com.education.corsalite.models.responsemodels.Chapter;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.PartTestGridElement;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by madhuri on 2/27/16.
 */
public class TestDownloadService extends IntentService {

    public TestDownloadService() {
        super("TestDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TestPaperIndex testPaperIndicies = new Gson().fromJson(intent.getStringExtra("Test_Instructions"), TestPaperIndex.class);
        String testQuestionPaperId = intent.getStringExtra("testQuestionPaperId");
        String testAnswerPaperId = intent.getStringExtra("testAnswerPaperId");
        String mockTestStr = intent.getStringExtra("selectedMockTest");
        String scheduledTestStr = intent.getStringExtra("selectedScheduledTest");
        String takeTestStr = intent.getStringExtra("selectedTakeTest");
        String partTestStr = intent.getStringExtra("selectedPartTest");
        String subjectName = intent.getStringExtra("SubjectName");
        String subjectId = intent.getStringExtra("subjectId");
        String questionsCount = intent.getStringExtra("questions_count");
        String exerciseQuestionsListJson = intent.getStringExtra("exercise_data");
        List<ExerciseOfflineModel> exerciseModelsList = null;
        if(!TextUtils.isEmpty(exerciseQuestionsListJson)) {
            Type listType = new TypeToken<ArrayList<ExerciseOfflineModel>>() {
            }.getType();
            exerciseModelsList = new Gson().fromJson(exerciseQuestionsListJson, listType);
        }
        String partTestGridElimentsJson = intent.getStringExtra(Constants.PARTTEST_GRIDMODELS);
        List<PartTestGridElement> partTestGridElements = null;
        if(!TextUtils.isEmpty(partTestGridElimentsJson)) {
            Type listType = new TypeToken<ArrayList<PartTestGridElement>>() {}.getType();
            partTestGridElements = new Gson().fromJson(partTestGridElimentsJson, listType);
        }
        if (mockTestStr != null) {
            MockTest mockTest = new Gson().fromJson(mockTestStr, MockTest.class);
            getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, mockTest, testPaperIndicies, null);
        } else if (scheduledTestStr != null) {
            ScheduledTestList.ScheduledTestsArray scheduledTest = new Gson().fromJson(scheduledTestStr, ScheduledTestList.ScheduledTestsArray.class);
            getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, null, null, scheduledTest);
        } else if(takeTestStr != null){
            Chapter chapter = new Gson().fromJson(takeTestStr,Chapter.class);
            loadTakeTest(chapter,null, questionsCount, subjectId);
        } else if(partTestStr != null){
            subjectName = partTestStr;
            loadPartTest(subjectName, subjectId, partTestGridElements);
        } else if(exerciseModelsList != null) {
            downloadExercises(exerciseModelsList);
        }
    }

    private void downloadExercises(List<ExerciseOfflineModel> models) {
        for(final ExerciseOfflineModel model : models) {
            ApiManager.getInstance(this).getExercise(model.topicId, model.courseId, LoginUserCache.getInstance().loginResponse.studentId, null,
                new ApiCallback<List<ExamModel>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        L.error("Failed to save exercise for topic model" + model.topicId);
                    }

                    @Override
                    public void success(List<ExamModel> examModels, Response response) {
                        super.success(examModels, response);
                        if(examModels != null && !examModels.isEmpty()) {
                            model.questions = examModels;
                            DbManager.getInstance(getApplicationContext()).saveOfflineExerciseTest(model);
                        }
                    }
                });
        }
    }

    private void getTestQuestionPaper(final String testQuestionPaperId, final String testAnswerPaperId,
                                      final MockTest mockTest, final TestPaperIndex testPAperIndecies, final ScheduledTestList.ScheduledTestsArray scheduledTestsArray) {
        ApiManager.getInstance(this).getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId,
                new ApiCallback<List<ExamModel>>(this) {
                    @Override
                    public void success(List<ExamModel> examModels, Response response) {
                        super.success(examModels, response);
                        OfflineTestModel model = new OfflineTestModel();
                        model.examModels = examModels;
                        if (mockTest != null) {
                            model.testType = Tests.MOCK;
                            model.mockTest = mockTest;
                        } else {
                            model.testType = Tests.SCHEDULED;
                            model.scheduledTest = scheduledTestsArray;
                        }
                        model.testPaperIndecies = testPAperIndecies;
                        model.testQuestionPaperId = testQuestionPaperId;
                        model.testAnswerPaperId = testAnswerPaperId;
                        model.dateTime = System.currentTimeMillis();
                        DbManager.getInstance(getApplicationContext()).saveOfflineTest(model);
                    }
                });
    }

    private void loadTakeTest(Chapter chapter, String subjectName, String questionsCount, String subjectId){
        ExamEngineHelper helper = new ExamEngineHelper(this);
        helper.loadTakeTest(chapter, subjectName, subjectId, questionsCount, new OnExamLoadCallback() {
            @Override
            public void onSuccess(BaseTest test) {
                OfflineTestModel model = new OfflineTestModel();
                model.testType = Tests.CHAPTER;
                model.baseTest = test;
                model.dateTime = System.currentTimeMillis();
                DbManager.getInstance(TestDownloadService.this).saveOfflineTest(model);
            }

            @Override
            public void OnFailure(String message) {
            }
        });
    }

    private void loadPartTest(final String subjectName, final String subjectId, List<PartTestGridElement> elements){
        ExamEngineHelper helper = new ExamEngineHelper(this);
        helper.loadPartTest(subjectName, subjectId, elements, new OnExamLoadCallback() {
            @Override
            public void onSuccess(BaseTest test) {
                OfflineTestModel model = new OfflineTestModel();
                model.testType = Tests.PART;
                model.baseTest = test;
                if(test!= null) {
                    model.baseTest.subjectId = subjectId;
                    model.baseTest.subjectName = subjectName;
                }
                model.dateTime = System.currentTimeMillis();
                DbManager.getInstance(TestDownloadService.this).saveOfflineTest(model);
                L.info("Test Saved : "+model.getClass());
            }

            @Override
            public void OnFailure(String message) {
            }
        });
    }
}
