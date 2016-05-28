package com.corsalite.tabletapp.models.db;

import com.corsalite.tabletapp.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Girish on 03/10/15.
 */
public class MockTest extends BaseModel implements Comparable<MockTest>, Serializable{

    @SerializedName("DisplayTestName")
    public String displayName;
    @SerializedName("ExamName")
    public String examName;
    @SerializedName("idExamTemplate")
    public String examTemplateId;
    @SerializedName("idSubject")
    public String subjectId;
    @SerializedName("subjectName")
    public String subjectName;

    public MockTest() {}

    @Override
    public int compareTo(MockTest another) {
        try {
            return Integer.valueOf(this.examTemplateId) - Integer.valueOf(another.examTemplateId);
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
