package com.education.corsalite.models.examengine;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.ExamModel;

import java.util.List;

/**
 * Created by vissu on 3/18/16.
 */
public class BaseTest extends BaseModel {

    public String courseId;
    public String subjectId;
    public String testQuestionPaperId;
    public String testAnswerPaperId;
    public List<ExamModel> questions;
}
