package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.utils.L;

import java.util.List;

/**
 * Created by vissu on 3/22/16.
 */
public class ExerciseOfflineModel extends BaseModel {
    public String topicId;
    public String courseId;
    public List<ExamModel> questions;

    public ExerciseOfflineModel(){}

    public ExerciseOfflineModel(String courseId, String topicId){
        this.courseId = courseId;
        this.topicId = topicId;
    }

    @Override
    public boolean equals(Object o) {
        try {
            return (this.topicId.equals(topicId) && this.courseId.equals(courseId));
        } catch (NullPointerException e) {
            L.error(e.getMessage(), e);
            return false;
        }
    }
}
