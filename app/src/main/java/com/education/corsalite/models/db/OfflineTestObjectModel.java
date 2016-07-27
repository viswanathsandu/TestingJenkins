package com.education.corsalite.models.db;

import com.education.corsalite.enums.Tests;
import com.education.corsalite.models.examengine.BaseTest;
import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.TestPaperIndex;
import com.education.corsalite.models.responsemodels.TestQuestionPaperResponse;
import com.education.corsalite.utils.Constants;
import com.google.gson.annotations.Expose;
import com.orm.dsl.Ignore;

/**
 * Created by madhuri on 2/28/16.
 */
public class OfflineTestObjectModel extends BaseModel {

    @Ignore
    public Tests testType;
    @Ignore
    @Expose(serialize = false, deserialize = false)
    public TestQuestionPaperResponse testQuestionPaperResponse;
    @Ignore
    public MockTest mockTest;
    @Ignore
    public ScheduledTestsArray scheduledTest;
    public String testQuestionPaperId;
    public String testAnswerPaperId;
    @Ignore
    public TestPaperIndex testPaperIndecies;
    @Ignore
    public BaseTest baseTest;
    public long dateTime;
    public int status = Constants.STATUS_START;

    public OfflineTestObjectModel() {}
}
