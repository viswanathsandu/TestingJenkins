package com.education.corsalite.models.requestmodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by Girish on 19/12/15.
 */
public class ExamTemplateConfig {

    @SerializedName("SubjectID")
    public String subjectId;
    @SerializedName("QuestionTypeID")
    public Integer questionTypeID = 1;
    @SerializedName("MarksPerQuestion")
    public Integer marksPerQuestion = 1;
    @SerializedName("NegativeMarksPerQuestion")
    public Integer negativeMarksPerQuestion = 0;
    @SerializedName("PartialMarkYN")
    public String partialMarkYN = "Y";
    @SerializedName("QuestionCount")
    public String questionCount = "10";
    @SerializedName("ComplexityFrom")
    public String complexityFrom;
    @SerializedName("ComplexityTo")
    public String complexityTo;
    @SerializedName("Duration")
    public String duration = "300";
    @Ignore
    @SerializedName("ExamTemplateChapter")
    public List<ExamTemplateChapter> examTemplateChapter;
}
