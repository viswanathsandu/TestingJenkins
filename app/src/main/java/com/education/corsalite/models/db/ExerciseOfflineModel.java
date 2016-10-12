package com.education.corsalite.models.db;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by vissu on 3/22/16.
 */
public class ExerciseOfflineModel extends BaseModel {
    public String topicId;
    public String courseId;
    @Ignore
    public String topicName;

    public int progress = 0;

    @Ignore
    public List<ExamModel> questions;

    public ExerciseOfflineModel(){}

    public ExerciseOfflineModel(String courseId, String topicId, String topicName){
        this.courseId = courseId;
        this.topicId = topicId;
        this.topicName = topicName;
    }
}
