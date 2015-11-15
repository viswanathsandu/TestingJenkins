package com.education.corsalite.models.db;

import com.education.corsalite.models.responsemodels.BaseResponseModel;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by Girish on 30/09/15.
 */
public class ContentIndexResponse extends SugarRecord<ContentIndexResponse> {
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
