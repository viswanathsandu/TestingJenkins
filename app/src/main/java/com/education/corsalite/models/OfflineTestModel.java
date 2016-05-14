package com.education.corsalite.models;

import com.education.corsalite.enums.Tests;
import com.education.corsalite.models.examengine.BaseTest;
import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.utils.Constants;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by madhuri on 2/28/16.
 */
public class OfflineTestModel extends BaseModel {

    public Tests testType;
    public MockTest mockTest;
    @Ignore
    public List<ExamModel> examModels;
    public ScheduledTestList.ScheduledTestsArray scheduledTest;
    public String testQuestionPaperId;
    public String testAnswerPaperId;
    public TestPaperIndex testPaperIndecies;
    public BaseTest baseTest;
    public long dateTime;
    public int status = Constants.STATUS_START;

    public OfflineTestModel() {}
}
