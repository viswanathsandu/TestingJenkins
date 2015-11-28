package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class LoginRequest extends AbstractBaseRequest {

    public String loginId;
    public String passwordHash;

    public LoginRequest() {
        super();
    }

    public LoginRequest(String loginId, String passwordHash) {
        super();
        this.loginId = loginId;
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof LoginRequest
                && isSame(loginId, ((LoginRequest) request).loginId)
                && isSame(passwordHash, ((LoginRequest) request).passwordHash);
    }
}
