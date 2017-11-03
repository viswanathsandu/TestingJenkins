package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class CourseRequest extends AbstractBaseRequest {

    public String studentId;

    public CourseRequest() {
    }

    public CourseRequest(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof CourseRequest
                && isSame(studentId, ((CourseRequest) request).studentId);
    }
}
