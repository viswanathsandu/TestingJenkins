package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by ayush on 11/28/15.
 */
public class ContentRequest extends AbstractBaseRequest {

    public String idContents;
    public String updateTime;

    public ContentRequest() {
    }

    public ContentRequest(String idContents, String updateTime) {
        this.idContents = idContents;
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof ContentRequest
                && isSame(idContents, ((ContentRequest) request).idContents)
                && isSame(updateTime, ((ContentRequest) request).updateTime);
    }
}
