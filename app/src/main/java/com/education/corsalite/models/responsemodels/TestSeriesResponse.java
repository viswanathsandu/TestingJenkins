package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vissu on 4/6/17.
 */

public class TestSeriesResponse extends BaseResponseModel {

    @SerializedName("subjectData")
    public List<TestSubject> subjects;
    @SerializedName("mockTestData")
    public List<TestSeriesMockData> mockTests;

}
