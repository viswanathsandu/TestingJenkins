package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by ayush on 11/28/15.
 */
public class ContentRequest extends AbstractBaseRequest {

    public String idContents;
    public String updateTime;

    public ContentRequest() {
        super();
    }

    public ContentRequest(String studentId, String courseId) {
        super();
        this.idContents = studentId;
        this.updateTime = courseId;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof ContentRequest
                && isSame(idContents, ((ContentRequest) request).idContents)
                && isSame(updateTime, ((ContentRequest) request).updateTime);
    }
}
