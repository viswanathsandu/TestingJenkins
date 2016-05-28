package com.corsalite.tabletapp.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class UserProfileRequest extends AbstractBaseRequest {

    public String studentId;

    public UserProfileRequest() {}

    public UserProfileRequest(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof UserProfileRequest
                && studentId != null && ((UserProfileRequest) request).studentId != null
                && studentId.equals(((UserProfileRequest) request).studentId);
    }
}
