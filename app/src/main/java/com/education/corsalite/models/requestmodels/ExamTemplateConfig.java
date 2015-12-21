package com.education.corsalite.models.requestmodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Girish on 19/12/15.
 */
public class ExamTemplateConfig {

    @SerializedName("SubjectID")
    public String subjectId;
    @SerializedName("QuestionTypeID")
    public int questionTypeID;
    @SerializedName("MarksPerQuestion")
    public int marksPerQuestion;
    @SerializedName("NegativeMarksPerQuestion")
    public int negativeMarksPerQuestion;
    @SerializedName("PartialMarkYN")
    public String partialMarkYN;
    @SerializedName("QuestionCount")
    public String questionCount;
    @SerializedName("ComplexityFrom")
    public String complexityFrom;
    @SerializedName("ComplexityTo")
    public String complexityTo;
    @SerializedName("Duration")
    public String duration;
    @SerializedName("ExamTemplateChapter")
    public List<ExamTemplateChapter> examTemplateChapter;
}
