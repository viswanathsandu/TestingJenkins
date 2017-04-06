package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vissu on 4/6/17.
 */

public class TestSeriesResponse extends BaseResponseModel {

    @SerializedName("subjectData")
    public TestSubject subject;
    @SerializedName("mockTestData")
    public List<TestSeriesMockData> mockTests;

    public List<TestSubject> getSubjectList() {
        List<TestSubject> subjects = new ArrayList<>();
        subjects.add(subject);
        return subjects;
    }
}
