package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 06-09-2017.
 */

public class PercentileChartSubjectData extends BaseModel {
    @SerializedName("studentAccuracy")
    public Integer accuracy;
    @SerializedName("SubjectName")
    public String subjectName;
}
