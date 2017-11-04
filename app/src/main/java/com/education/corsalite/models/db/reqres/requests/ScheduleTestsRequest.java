package com.education.corsalite.models.db.reqres.requests;

/**
 * Created by vissu on 11/28/15.
 */
public class ScheduleTestsRequest extends AbstractBaseRequest {

    public String studentId;

    public ScheduleTestsRequest() {
    }

    public ScheduleTestsRequest(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public boolean equals(AbstractBaseRequest request) {
        return request instanceof ScheduleTestsRequest
                && studentId != null && ((ScheduleTestsRequest) request).studentId != null
                && studentId.equals(((ScheduleTestsRequest) request).studentId);
    }
}
