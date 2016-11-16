package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class AppConfigRequest extends AbstractBaseRequest {

    public AppConfigRequest() {}

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof AppConfigRequest;
    }
}
