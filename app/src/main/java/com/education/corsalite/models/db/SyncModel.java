package com.education.corsalite.models.db;

import com.education.corsalite.models.responsemodels.BaseModel;

/**
 * Created by vissu on 7/3/16.
 */

public class SyncModel<T extends BaseModel> extends BaseModel{
    public T requestObject;
}
