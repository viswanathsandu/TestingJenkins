package com.corsalite.tabletapp.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Girish on 19/12/15.
 */
public class PostExamTemplate extends BaseResponseModel {

    @SerializedName("idExamTemplate")
    public String idExamTemplate;

    public PostExamTemplate() {
    }
}
