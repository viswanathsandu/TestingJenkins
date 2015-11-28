package com.education.corsalite.models.db.reqres;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.Course;

import java.util.List;

/**
 * Created by vissu on 11/27/15.
 */
public class CoursesReqRes extends BaseModel {
    public String studentId;
    public List<Course> response;
}
