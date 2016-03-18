package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;

import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.models.MockTest;
import com.education.corsalite.models.OfflineMockTestModel;
import com.education.corsalite.models.ScheduledTestList;
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.TestCoverage;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.utils.L;
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
        String subjectId = intent.getStringExtra("subjectId");
        String chapterId = intent.getStringExtra("chapterId");
        if (mockTestStr != null) {
            MockTest mockTest = new Gson().fromJson(mockTestStr, MockTest.class);
            getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, mockTest, testPaperIndicies, null);
        } else if (scheduledTestStr != null) {
            ScheduledTestList.ScheduledTestsArray scheduledTest = new Gson().fromJson(scheduledTestStr, ScheduledTestList.ScheduledTestsArray.class);
            getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId, null, null, scheduledTest);
        }else if(takeTestStr != null){
            Chapters chapters = new Gson().fromJson(takeTestStr,Chapters.class);
            fetchTestCoverageFromServer(chapters,subjectId,chapterId);
        }
    }

    private void getTestQuestionPaper(final String testQuestionPaperId, final String testAnswerPaperId,
                                      final MockTest mockTest, final TestPaperIndex testPAperIndecies, final ScheduledTestList.ScheduledTestsArray scheduledTestsArray) {
        ApiManager.getInstance(this).getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId,
                new ApiCallback<List<ExamModel>>(this) {
                    @Override
                    public void success(List<ExamModel> examModels, Response response) {
                        super.success(examModels, response);
                        OfflineMockTestModel model = new OfflineMockTestModel();
                        model.examModels = examModels;
                        if (mockTest != null) {
                            model.mockTest = mockTest;
                        } else {
                            model.scheduledTest = scheduledTestsArray;
                        }
                        model.testPaperIndecies = testPAperIndecies;
                        model.testQuestionPaperId = testQuestionPaperId;
                        model.testAnswerPaperId = testAnswerPaperId;
                        DbManager.getInstance(getApplicationContext()).saveOfflineMockTest(model);
                    }
                });
    }

    private void fetchTestCoverageFromServer(final Chapters chapter, final String subjectId,String chapterID) {
        ApiManager.getInstance(this).getTestCoverage(LoginUserCache.getInstance().loginResponse.studentId, AbstractBaseActivity.selectedCourse.courseId.toString(), subjectId, chapterID,
            new ApiCallback<List<TestCoverage>>(this) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    L.error(error.message);
                }

                @Override
                public void success(List<TestCoverage> testCoverages, Response response) {
                    super.success(testCoverages, response);
                    OfflineMockTestModel model = new OfflineMockTestModel();
                    model.testCoverages = testCoverages;
                    model.chapter = chapter;
                    model.subjectId = subjectId;
                    DbManager.getInstance(getApplicationContext()).saveOfflineMockTest(model);
                }
            });
    }
}
