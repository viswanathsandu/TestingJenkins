package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by madhuri on 2/20/16.
 */
public class TestPaperIndex extends BaseModel{
    @Ignore
    @SerializedName("ExamDetails")
    public List<ExamDetails> examDetails;
    @Ignore
    @SerializedName("QuestionPaperIndex")
    public List<QuestionPaperIndex> questionPaperIndecies;

    public TestPaperIndex() {}
}
