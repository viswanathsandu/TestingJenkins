package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aastha on 01/10/15.
 */
public class TestCoverage {
    @SerializedName("Subject")
    public String subject;
    @SerializedName("idCourseSubjectChapter")
    public String idCourseSubjectChapter;
    @SerializedName("Chapter")
    public String chapter;
    @SerializedName("Level")
    public String level;
    @SerializedName("SortOrder1")
    public String sortOrder;
    @SerializedName("TestCoveragePercentage")
    public String testCoverage;
    @SerializedName("AttendedQuesCount")
    public String attendedQCount;
    @SerializedName("QuesCount")
    public String questionCount;
    @SerializedName("AttendedCorrectQuesCount")
    public String attendedCorrectQCount;
}
