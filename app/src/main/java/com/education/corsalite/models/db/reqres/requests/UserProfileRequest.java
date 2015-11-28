package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class UserProfileRequest extends AbstractBaseRequest {

    public String studentId;

    public UserProfileRequest() {
        super();
    }

    public UserProfileRequest(String studentId) {
        super();
        this.studentId = studentId;
    }

    @Override
    public boolean isRequestSame(AbstractBaseRequest request) {
        return request instanceof UserProfileRequest
                && studentId != null && ((UserProfileRequest) request).studentId != null
                && studentId.equals(((UserProfileRequest) request).studentId);
    }
}
