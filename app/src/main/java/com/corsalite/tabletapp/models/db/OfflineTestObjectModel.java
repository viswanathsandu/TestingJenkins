package com.corsalite.tabletapp.models.db;

import com.corsalite.tabletapp.enums.Tests;
import com.corsalite.tabletapp.models.examengine.BaseTest;
import com.corsalite.tabletapp.models.responsemodels.BaseModel;
import com.corsalite.tabletapp.models.responsemodels.ExamModel;
import com.corsalite.tabletapp.models.responsemodels.TestPaperIndex;
import com.corsalite.tabletapp.utils.Constants;
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
