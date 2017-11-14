package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class WelcomeRequest extends AbstractBaseRequest {

    public String studentId;

    public WelcomeRequest() {
    }

    public WelcomeRequest(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof WelcomeRequest
                && studentId != null && ((WelcomeRequest) request).studentId != null
                && studentId.equals(((WelcomeRequest) request).studentId);
    }
}
