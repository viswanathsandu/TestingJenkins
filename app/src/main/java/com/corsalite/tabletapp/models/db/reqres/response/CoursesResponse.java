package com.corsalite.tabletapp.models.db.reqres.response;

import com.corsalite.tabletapp.models.responsemodels.BaseResponseModel;
import com.corsalite.tabletapp.models.responsemodels.Course;

import java.util.List;

/**
 * Created by vissu on 5/13/16.
 */
public class CoursesResponse extends BaseResponseModel {

    public List<Course> courses;
    public CoursesResponse() {
    }
}
