package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.models.responsemodels.TestCoverage;
import com.education.corsalite.models.responsemodels.TestPaperIndex;

import java.util.List;

/**
 * Created by madhuri on 2/28/16.
 */
public class OfflineMockTestModel extends BaseModel {

    public MockTest mockTest;
    public List<ExamModel> examModels;
    public ScheduledTestList.ScheduledTestsArray scheduledTest;
    public Chapters chapter;
    public String subjectId;
    public List<TestCoverage> testCoverages;
    public String testQuestionPaperId;
    public String testAnswerPaperId;
    public TestPaperIndex testPaperIndecies;

}
