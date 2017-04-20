package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by vissu on 4/6/17.
 */

public class TestSeriesResponse extends BaseResponseModel {

    @Ignore
    @SerializedName("subjectData")
    public List<TestSubject> subjects;
    @Ignore
    @SerializedName("mockTestData")
    public List<TestSeriesMockData> mockTests;

}
