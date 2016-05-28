package com.corsalite.tabletapp.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by madhuri on 2/20/16.
 */
public class ExamDetails {
    @SerializedName("idExamTemplate")
    public String examTemplateId;
    @SerializedName("Name")
    public String examName;
    @SerializedName("ExamInstructions")
    public String examInstucation;
}
