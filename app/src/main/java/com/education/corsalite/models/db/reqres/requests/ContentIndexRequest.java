package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class ContentIndexRequest extends AbstractBaseRequest {

    public String studentId;
    public String courseId;

    public ContentIndexRequest() {
    }

    public ContentIndexRequest(String studentId, String courseId) {
        super();
        this.studentId = studentId;
        this.courseId = courseId;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof ContentIndexRequest
                && isSame(studentId, ((ContentIndexRequest) request).studentId)
                && isSame(courseId, ((ContentIndexRequest) request).courseId);
    }
}
