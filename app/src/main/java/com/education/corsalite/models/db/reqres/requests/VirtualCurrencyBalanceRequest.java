package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class VirtualCurrencyBalanceRequest extends AbstractBaseRequest {

    public String studentId;

    public VirtualCurrencyBalanceRequest() {
        super();
    }

    public VirtualCurrencyBalanceRequest(String studentId) {
        super();
        this.studentId = studentId;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof VirtualCurrencyBalanceRequest
                && isSame(studentId, ((VirtualCurrencyBalanceRequest) request).studentId);
    }
}
