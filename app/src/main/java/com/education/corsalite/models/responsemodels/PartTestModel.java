package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by madhuri on 3/24/16.
 */
public class PartTestModel extends BaseModel {
    public int idExam;
    @Ignore
    @SerializedName("PartTestGrid")
    public List<PartTestGridElement> partTestGrid;
}

