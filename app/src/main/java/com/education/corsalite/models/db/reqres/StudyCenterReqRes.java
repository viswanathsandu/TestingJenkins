package com.education.corsalite.models.db.reqres;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.StudyCenter;

import java.util.List;

/**
 * Created by vissu on 11/27/15.
 */
public class StudyCenterReqRes extends BaseModel {
    public String studentId;
    public String courseId;
    public List<StudyCenter> response;
}
