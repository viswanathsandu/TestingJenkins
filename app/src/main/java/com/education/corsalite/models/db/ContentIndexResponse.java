package com.education.corsalite.models.db;

import com.orm.SugarRecord;

/**
 * Created by Girish on 30/09/15.
 */
public class ContentIndexResponse extends SugarRecord {
    public String contentIndexesJson;
    public String courseId;
    public String studentId;

    public ContentIndexResponse() {
        super();
    }

    public ContentIndexResponse(String contentIndexesJson, String courseId, String studentId) {
        this.contentIndexesJson = contentIndexesJson;
        this.courseId = courseId;
        this.studentId = studentId;
    }
}
