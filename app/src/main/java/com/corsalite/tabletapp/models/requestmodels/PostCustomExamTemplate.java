package com.corsalite.tabletapp.models.requestmodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by Girish on 19/12/15.
 */
public class PostCustomExamTemplate {

    @SerializedName("ExamID")
    public String examId;
    @SerializedName("Name")
    public String examName;
    @SerializedName("DoTestBySlidingComplexity")
    public String examDoTestBySlidingComplexity;
    @Ignore
    @SerializedName("ExamTemplateConfig")
    public List<ExamTemplateConfig> examTemplateConfig;
}
