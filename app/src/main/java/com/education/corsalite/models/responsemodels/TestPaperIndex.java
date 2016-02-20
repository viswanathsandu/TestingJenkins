package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by madhuri on 2/20/16.
 */
public class TestPaperIndex {
    @SerializedName("ExamDetails")
   public List<ExamDetails> examDetails;
}
