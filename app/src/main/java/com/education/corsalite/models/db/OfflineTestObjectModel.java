package com.education.corsalite.models.db;

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
public class OfflineTestObjectModel extends BaseModel {

    @Ignore
    public Tests testType;
    @Ignore
    public List<ExamModel> examModels;
    public MockTest mockTest;
    public ScheduledTestsArray scheduledTest;
    public String testQuestionPaperId;
    public String testAnswerPaperId;
    public TestPaperIndex testPaperIndecies;
    public BaseTest baseTest;
    public long dateTime;
    public int status = Constants.STATUS_START;

    public OfflineTestObjectModel() {}
}
