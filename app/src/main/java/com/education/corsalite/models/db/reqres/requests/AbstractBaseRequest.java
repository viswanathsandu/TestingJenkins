package com.education.corsalite.models.db.reqres.requests;

import com.education.corsalite.models.responsemodels.BaseModel;

/**
 * Created by vissu on 11/28/15.
 */
public abstract class AbstractBaseRequest extends BaseModel {

    public abstract boolean isRequestSame(AbstractBaseRequest request);
}
