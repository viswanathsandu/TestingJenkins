package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vissu on 6/17/16.
 */

public class TestQuestionPaperResponse extends BaseResponseModel {

    @SerializedName("QuestionsDetails")
    public List<ExamModel> questions;
    @SerializedName("ExamDetails")
    public QuestionPaperExamDetails examDetails;

    public TestQuestionPaperResponse() {}

}
