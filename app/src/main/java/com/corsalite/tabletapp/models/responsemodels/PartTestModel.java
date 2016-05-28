package com.corsalite.tabletapp.models.responsemodels;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by madhuri on 3/24/16.
 */
public class PartTestModel extends BaseModel {
    @SerializedName("idExam")
    public LinkedTreeMap<String, String> examIds;
    @Ignore
    @SerializedName("PartTestGrid")
    public List<PartTestGridElement> partTestGrid;
}

