package com.corsalite.tabletapp.models.db;

import com.corsalite.tabletapp.models.responsemodels.BaseModel;
import com.corsalite.tabletapp.models.responsemodels.ExamModel;
import com.corsalite.tabletapp.utils.L;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by vissu on 3/22/16.
 */
public class ExerciseOfflineModel extends BaseModel {
    public String topicId;
    public String courseId;
    public int progress = 0;

    @Ignore
    public List<ExamModel> questions;

    public ExerciseOfflineModel(){}

    public ExerciseOfflineModel(String courseId, String topicId){
        this.courseId = courseId;
        this.topicId = topicId;
    }

    @Override
    public boolean equals(Object o) {
        try {
            ExerciseOfflineModel exercise = (ExerciseOfflineModel)o;
            return (this.topicId.equals(exercise.topicId) && this.courseId.equals(exercise.courseId));
        } catch (NullPointerException e) {
            L.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.topicId != null ? this.topicId.hashCode() : 0)
                  +  (this.courseId != null ? this.courseId.hashCode() : 0);
        return hash;
    }
}
