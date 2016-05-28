package com.corsalite.tabletapp.models.db;

import com.corsalite.tabletapp.models.responsemodels.BaseModel;

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
