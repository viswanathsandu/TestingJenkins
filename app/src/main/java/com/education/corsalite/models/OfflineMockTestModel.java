package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.ExamModel;

import java.util.List;

/**
 * Created by madhuri on 2/28/16.
 */
public class OfflineMockTestModel extends BaseModel {

    public MockTest mockTest;
    public List<ExamModel> examModels;
    public ScheduledTestList.ScheduledTestsArray scheduledTest;

    public OfflineMockTestModel() {}
}
