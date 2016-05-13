package com.education.corsalite.models.db.reqres;

import com.education.corsalite.models.db.reqres.requests.AbstractBaseRequest;
import com.education.corsalite.models.responsemodels.BaseModel;

/**
 * Created by vissu on 11/28/15.
 */
public class ReqRes<T> extends BaseModel{

    public AbstractBaseRequest request;

    public T response;

    public ReqRes() {}

    public void setResponse(T response) {
        this.response = response;
    }

    public boolean isRequestSame(ReqRes reqres) {
        return reqres != null
                && request != null
                && reqres.request != null
                && reqres.request.equals(request);
    }
}
