package com.education.corsalite.models.db;

import com.education.corsalite.models.responsemodels.BaseModel;

/**
 * Created by Girish on 30/09/15.
 */
public class ContentIndexResponse extends BaseModel {
    public String contentIndexesJson;
    public String courseId;
    public String studentId;

    public ContentIndexResponse() {
    }

    public ContentIndexResponse(String contentIndexesJson, String courseId, String studentId) {
        this.contentIndexesJson = contentIndexesJson;
        this.courseId = courseId;
        this.studentId = studentId;
    }
}
