package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.enums.Tests;
import com.education.corsalite.helpers.ExamEngineHelper;
import com.education.corsalite.listener.OnExamLoadCallback;
import com.education.corsalite.models.db.ExerciseOfflineModel;
import com.education.corsalite.models.db.MockTest;
import com.education.corsalite.models.db.OfflineTestObjectModel;
import com.education.corsalite.models.db.ScheduledTestsArray;
import com.education.corsalite.models.examengine.BaseTest;
import com.education.corsalite.models.responsemodels.Chapter;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.PartTestGridElement;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.models.responsemodels.TestQuestionPaperResponse;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.ExamUtils;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.TimeUtils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by madhuri on 2/27/16.
 */
public class TestDownloadService extends IntentService {

    private SugarDbManager dbManager;
    private ExamUtils examUtils;

    public TestDownloadService() {
        super("TestDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        dbManager = SugarDbManager.get(getApplicationContext());
        examUtils = new ExamUtils(getApplicationContext());
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
            exerciseModelsList = Gson.get().fromJson(exerciseQuestionsListJson, listType);
        }
        String partTestGridElimentsJson = intent.getStringExtra(Constants.PARTTEST_GRIDMODELS);
        List<PartTestGridElement> partTestGridElements = null;
        if(!TextUtils.isEmpty(partTestGridElimentsJson)) {
            Type listType = new TypeToken<ArrayList<PartTestGridElement>>() {}.getType();
            partTestGridElements = Gson.get().fromJson(partTestGridElimentsJson, listType);
        }
        if (mockTestStr != null) {
            MockTest mockTest = Gson.get().fromJson(mockTestStr, MockTest.class);
            getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, mockTest, null);
        } else if (scheduledTestStr != null) {
            ScheduledTestsArray scheduledTest = Gson.get().fromJson(scheduledTestStr, ScheduledTestsArray.class);
            getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, null, scheduledTest);
        } else if(takeTestStr != null){
            Chapter chapter = Gson.get().fromJson(takeTestStr,Chapter.class);
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
            ApiManager.getInstance(this).getExercise(model.topicId, model.courseId, LoginUserCache.getInstance().getStudentId(), null,
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
                            dbManager.saveOfflineExerciseTest(model);
                        }
                    }
                });
        }
    }


    private void getTestQuestionPaper(final String testQuestionPaperId, final String testAnswerPaperId,
                                      final MockTest mockTest, final ScheduledTestsArray scheduledTestsArray) {
        try {
            TestPaperIndex testPaperIndexResponse = ApiManager.getInstance(this).getTestPaperIndex(testQuestionPaperId, testAnswerPaperId, "N");
            TestQuestionPaperResponse questionPaperResponse = ApiManager.getInstance(this).getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId);
            if (questionPaperResponse != null) {
                OfflineTestObjectModel model = new OfflineTestObjectModel();
                String testName = "";
                if (questionPaperResponse != null) {
                    model.testQuestionPaperResponse = questionPaperResponse;
                }
                if (mockTest != null) {
                    model.testType = Tests.MOCK;
                    model.mockTest = mockTest;
                    testName = mockTest.displayName;
                } else if (scheduledTestsArray != null) {
                    model.testType = Tests.SCHEDULED;
                    model.scheduledTest = scheduledTestsArray;
                    testName = scheduledTestsArray.examName;
                }
                model.baseTest = new BaseTest();
                model.baseTest.courseId = AbstractBaseActivity.getSelectedCourseId();
                model.testQuestionPaperId = testQuestionPaperId;
                model.testAnswerPaperId = testAnswerPaperId;
                model.dateTime = TimeUtils.currentTimeInMillis();
                dbManager.saveOfflineTest(model);
                examUtils.saveTestPaperIndex(model.testQuestionPaperId, testPaperIndexResponse);
                examUtils.saveTestQuestionPaper(model.testQuestionPaperId, questionPaperResponse);
                if (!TextUtils.isEmpty(testName)) {
                    Toast.makeText(getApplicationContext(), "Test \"" + testName + "\" has been downloaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Test has been downloaded successfully", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void loadTakeTest(final Chapter chapter, final String subjectName, String questionsCount, String subjectId){
        ExamEngineHelper helper = new ExamEngineHelper(this);
        helper.loadTakeTest(chapter, subjectName, subjectId, questionsCount, new OnExamLoadCallback() {
            @Override
            public void onSuccess(BaseTest test) {
                OfflineTestObjectModel model = new OfflineTestObjectModel();
                model.testType = Tests.CHAPTER;
                model.baseTest = test;
                model.dateTime = TimeUtils.currentTimeInMillis();
                model.testQuestionPaperId = test.testQuestionPaperId;
                dbManager.saveOfflineTest(model);
                new ExamUtils(getApplicationContext()).saveTestQuestionPaper(model.testQuestionPaperId, test.testQuestionPaperResponse);
                Toast.makeText(getApplicationContext(), "\"" + chapter.chapterName + "\" test is downloaded successfully", Toast.LENGTH_SHORT).show();
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
                OfflineTestObjectModel model = new OfflineTestObjectModel();
                model.testType = Tests.PART;
                model.baseTest = test;
                if(test!= null) {
                    model.baseTest.subjectId = subjectId;
                    model.baseTest.subjectName = subjectName;
                }
                model.dateTime = TimeUtils.currentTimeInMillis();
                model.testQuestionPaperId = test.testQuestionPaperId;
                dbManager.saveOfflineTest(model);
                new ExamUtils(getApplicationContext()).saveTestQuestionPaper(model.testQuestionPaperId, test.testQuestionPaperResponse);
                L.info("Test Saved : "+model.getClass());
                Toast.makeText(getApplicationContext(), "\""+subjectName + "\" test is downloaded successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFailure(String message) {
            }
        });
    }
}
