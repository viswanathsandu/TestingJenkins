package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.helpers.ExamEngineHelper;
import com.education.corsalite.listener.OnExamLoadCallback;
import com.education.corsalite.models.MockTest;
import com.education.corsalite.models.OfflineTestModel;
import com.education.corsalite.models.ScheduledTestList;
import com.education.corsalite.models.examengine.BaseTest;
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.StudyCenter;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.google.gson.Gson;

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
        String subjectId = intent.getStringExtra("subjectId");
        if (mockTestStr != null) {
            MockTest mockTest = new Gson().fromJson(mockTestStr, MockTest.class);
            getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, mockTest, testPaperIndicies, null);
        } else if (scheduledTestStr != null) {
            ScheduledTestList.ScheduledTestsArray scheduledTest = new Gson().fromJson(scheduledTestStr, ScheduledTestList.ScheduledTestsArray.class);
            getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, null, null, scheduledTest);
        }else if(takeTestStr != null){
            Chapters chapters = new Gson().fromJson(takeTestStr,Chapters.class);
            loadTakeTest(chapters,null,subjectId);
        }else if(partTestStr != null){
            StudyCenter studyCenter = new Gson().fromJson(partTestStr,StudyCenter.class);
            OfflineTestModel model = new OfflineTestModel();
            loadPartTest(studyCenter.SubjectName, subjectId, model);
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
                            model.mockTest = mockTest;
                        } else {
                            model.scheduledTest = scheduledTestsArray;
                        }
                        model.testPaperIndecies = testPAperIndecies;
                        model.testQuestionPaperId = testQuestionPaperId;
                        model.testAnswerPaperId = testAnswerPaperId;
                        DbManager.getInstance(getApplicationContext()).saveOfflineTest(model);
                    }
                });
    }

    private void loadTakeTest(Chapters chapter, String subjectName, String subjectId){
        ExamEngineHelper helper = new ExamEngineHelper(this);
        helper.loadTakeTest(chapter, subjectName, subjectId, new OnExamLoadCallback() {
            @Override
            public void onSuccess(BaseTest test) {
                OfflineTestModel model = new OfflineTestModel();
                model.baseTest = test;
                DbManager.getInstance(TestDownloadService.this).saveOfflineTest(model);
            }

            @Override
            public void OnFailure(String message) {
            }
        });
    }

    private void loadPartTest(String subjectName, String subjectId, final OfflineTestModel model){
        ExamEngineHelper helper = new ExamEngineHelper(this);
        helper.loadPartTest(subjectName,subjectId, new OnExamLoadCallback() {
            @Override
            public void onSuccess(BaseTest test) {
                model.baseTest = test;
                DbManager.getInstance(TestDownloadService.this).saveOfflineTest(model);
            }

            @Override
            public void OnFailure(String message) {
            }
        });
    }
}
