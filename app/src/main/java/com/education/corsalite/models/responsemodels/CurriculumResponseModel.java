package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vissu on 6/28/16.
 */
public class CurriculumResponseModel extends BaseResponseModel {

    @SerializedName("CurriculumData")
    public List<CurriculumEntity> curriculumEntities;
    /* @SerializedName("graphData")
    public List<Graphentity> graphItems;
    @SerializedName("learningGapData")
    public List<LearningGapEntity> learningGapData; */

    public CurriculumResponseModel() {
    }


}
