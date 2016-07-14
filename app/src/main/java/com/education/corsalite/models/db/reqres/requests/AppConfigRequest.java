package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class AppConfigRequest extends AbstractBaseRequest {

    public String idUser;

    public AppConfigRequest() {}

    public AppConfigRequest(String idUser) {
        this.idUser = idUser;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof AppConfigRequest
                && idUser != null && ((AppConfigRequest) request).idUser != null
                && idUser.equals(((AppConfigRequest) request).idUser);
    }
}
