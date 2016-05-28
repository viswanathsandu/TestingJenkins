package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 2/20/16.
 */
public class QuestionPaperIndex {

    @SerializedName("SectionNumber")
    public String sectionNumber;
    @SerializedName("SectionName")
    public String sectionName;
    @SerializedName("SubSectionID")
    public String subSectionID;
    @SerializedName("SubSectionDesc")
    public String subSectionDesc;
    @SerializedName("MarksPerQuestion")
    public String marksPerQuestion;
    @SerializedName("NegativeMarksPerQuestion")
    public String negativeMarksPerQuestion;
    @SerializedName("PartialMarkYN")
    public String partialMarkYN;
    @SerializedName("idTestQuestion")
    public String idTestQuestion;
    @SerializedName("QuestionNumber")
    public String questionNumber;
    @SerializedName("Status")
    public String status;
    @SerializedName("DoTestBySlidingComplexity")
    public String doTestBySlidingComplexity;
    @SerializedName("QuestionCount")
    public String questionCount;
    @SerializedName("SpecialLogic")
    public String specialLogic;
    @SerializedName("idCourse")
    public String courseId;
    @SerializedName("idCourseSubject")
    public String subjectId;

}
