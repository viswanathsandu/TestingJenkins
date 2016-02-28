package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.models.MockTest;
import com.education.corsalite.models.OfflineMockTestModel;
import com.education.corsalite.models.ScheduledTestList;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.google.gson.Gson;

import java.util.List;

import retrofit.client.Response;

/**
 * Created by madhuri on 2/27/16.
 */
public class TestDownloadService extends IntentService{

    public TestDownloadService() {
        super("TestDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String testQuestionPaperId = intent.getStringExtra("testQuestionPaperId");
        String testAnswerPaperId = intent.getStringExtra("testAnswerPaperId");
        String mockTestStr = intent.getStringExtra("selectedMockTest");
        String scheduledTestStr = intent.getStringExtra("selectedScheduledTest");
        if(mockTestStr != null){
        MockTest mockTest = new Gson().fromJson(mockTestStr,MockTest.class);
        getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId,mockTest,null);
        }else if(scheduledTestStr != null){
            ScheduledTestList.ScheduledTestsArray scheduledTest = new Gson().fromJson(mockTestStr,ScheduledTestList.ScheduledTestsArray.class);
            getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId,null,scheduledTest);
        }

    }

    private void getTestQuestionPaper(String testQuestionPaperId,String testAnswerPaperId,
                                      final MockTest mockTest, final ScheduledTestList.ScheduledTestsArray scheduledTestsArray) {
        ApiManager.getInstance(this).getTestQuestionPaper(testQuestionPaperId, testAnswerPaperId,
                new ApiCallback<List<ExamModel>>(this) {
                    @Override
                    public void success(List<ExamModel> examModels, Response response) {
                        super.success(examModels, response);
                        OfflineMockTestModel model = new OfflineMockTestModel();
                        model.examModels = examModels;
                        if(mockTest != null) {
                            model.mockTest = mockTest;
                        }else {
                            model.scheduledTest = scheduledTestsArray;
                        }
                        DbManager.getInstance(TestDownloadService.this).saveOfflineMockTest(model);
                    }
                });
    }
}
