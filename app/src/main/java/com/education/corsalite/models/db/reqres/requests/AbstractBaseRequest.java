package com.education.corsalite.models.db.reqres.requests;

import com.education.corsalite.models.responsemodels.BaseModel;

/**
 * Created by vissu on 11/28/15.
 */
public abstract class AbstractBaseRequest extends BaseModel {

    public abstract boolean equals(AbstractBaseRequest request);

    public boolean isSame(Object obj1, Object obj2) {
        return obj1 != null && obj2 != null
                && obj1.equals(obj2);
    }
}
