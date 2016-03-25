package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by madhuri on 3/24/16.
 */
public class PartTestModel extends BaseModel {
    public int idExam;
    @SerializedName("partTestGrid")
    public List<PartTestGridElement> partTestGrid;
}

