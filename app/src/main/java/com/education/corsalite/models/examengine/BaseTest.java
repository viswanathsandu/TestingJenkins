package com.education.corsalite.models.examengine;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.Chapter;
import com.education.corsalite.models.responsemodels.TestQuestionPaperResponse;
import com.google.gson.annotations.Expose;
import com.orm.dsl.Ignore;

/**
 * Created by vissu on 3/18/16.
 */
public class BaseTest extends BaseModel {

    public String courseId;
    public String subjectId;
    public String subjectName;
    public String testQuestionPaperId;
    public String testAnswerPaperId;
    public Chapter chapter;
    public String questionsCount;
    @Ignore
    @Expose(serialize = false, deserialize = false)
    public TestQuestionPaperResponse testQuestionPaperResponse;

    public BaseTest() {}
}
