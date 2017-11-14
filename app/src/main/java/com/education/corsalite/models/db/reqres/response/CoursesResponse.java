package com.education.corsalite.models.db.reqres.response;

import com.education.corsalite.models.responsemodels.BaseResponseModel;
import com.education.corsalite.models.responsemodels.Course;

import java.util.List;

/**
 * Created by vissu on 5/13/16.
 */
public class CoursesResponse extends BaseResponseModel {

    public List<Course> courses;

    public CoursesResponse() {
    }
}
