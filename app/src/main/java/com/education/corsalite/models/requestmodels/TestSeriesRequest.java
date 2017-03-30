package com.education.corsalite.models.requestmodels;

import com.education.corsalite.models.db.reqres.requests.AbstractBaseRequest;
import com.education.corsalite.models.db.reqres.requests.AppEntityConfigRequest;

/**
 * Created by vissu on 3/27/17.
 */

public class TestSeriesRequest extends AbstractBaseRequest {

    public String idStudent;
    public String idCourse;
    public String idCourseInstance;

    public TestSeriesRequest(String idStudent, String idCourse, String idCourseInstance) {
        this.idStudent = idStudent;
        this.idCourse = idCourse;
        this.idCourseInstance = idCourseInstance;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof AppEntityConfigRequest
                && idStudent != null && ((TestSeriesRequest) request).idStudent != null
                && idStudent.equals(((TestSeriesRequest) request).idStudent)
                && idCourse != null && ((TestSeriesRequest) request).idCourse != null
                && idCourse.equals(((TestSeriesRequest) request).idCourse)
                && idCourseInstance != null && ((TestSeriesRequest) request).idCourseInstance != null
                && idCourseInstance.equals(((TestSeriesRequest) request).idCourseInstance);
    }
}
