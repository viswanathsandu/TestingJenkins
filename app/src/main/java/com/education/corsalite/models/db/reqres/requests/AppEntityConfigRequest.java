package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class AppEntityConfigRequest extends AbstractBaseRequest {

    public String userId;
    public String entityId;

    public AppEntityConfigRequest() {}

    public AppEntityConfigRequest(String userId, String entityId) {
        this.userId = userId;
        this.entityId = entityId;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof AppEntityConfigRequest
                && userId != null && ((AppEntityConfigRequest) request).userId != null
                && userId.equals(((AppEntityConfigRequest) request).userId)
                && entityId != null && ((AppEntityConfigRequest) request).entityId != null
                && entityId.equals(((AppEntityConfigRequest) request).entityId);
    }
}
