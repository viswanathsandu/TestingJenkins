package com.education.corsalite.models.examengine;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.models.responsemodels.ExamModel;

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
    public Chapters chapter;
    public List<ExamModel> questions;
}
