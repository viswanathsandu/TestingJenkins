package com.education.corsalite.models.responsemodels;

import com.education.corsalite.models.SubjectModel;
import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Girish on 30/09/15.
 */
public class ContentIndex extends BaseModel implements Serializable {

    public String idEntity;
    public String idCourse;
    @SerializedName("CourseName")
    public String courseName;
    @SerializedName("CourseStatus")
    public String courseStatus;
    @Ignore
    @SerializedName("Subjects")
    public List<SubjectModel> subjectModelList;
}
