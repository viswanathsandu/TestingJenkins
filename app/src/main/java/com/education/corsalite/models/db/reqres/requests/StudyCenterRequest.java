package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class StudyCenterRequest extends AbstractBaseRequest {

    public String studentId;
    public String courseId;

    public StudyCenterRequest() {
    }

    public StudyCenterRequest(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof StudyCenterRequest
                && isSame(studentId, ((StudyCenterRequest) request).studentId)
                && isSame(courseId, ((StudyCenterRequest) request).courseId);
    }
}
