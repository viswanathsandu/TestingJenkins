package com.education.corsalite.models.responsemodels;

import com.education.corsalite.models.db.ExamResultDetails;
import com.education.corsalite.models.db.ExamResultSummarySubject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vissu on 11-08-2017.
 */

public class ExamResultSummaryResponse {

    @SerializedName("examResultDetails")
    public ExamResultDetails details;
    @SerializedName("examResultSumm")
    public List<ExamResultSummarySubject> examResultSummary;
}
