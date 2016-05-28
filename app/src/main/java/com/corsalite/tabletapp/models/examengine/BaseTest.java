package com.corsalite.tabletapp.models.examengine;

import com.corsalite.tabletapp.models.responsemodels.BaseModel;
import com.corsalite.tabletapp.models.responsemodels.Chapter;
import com.corsalite.tabletapp.models.responsemodels.ExamModel;
import com.orm.dsl.Ignore;

import java.util.List;

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
    public List<ExamModel> questions;

    public BaseTest() {}
}
