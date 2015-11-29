package com.education.corsalite.models;

import com.education.corsalite.models.responsemodels.BaseModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Girish on 02/10/15.
 */
public class SubjectModel extends BaseModel implements Serializable{

    @SerializedName("idCourseSubject")
    public String idSubject;
    @SerializedName("SubjectName")
    public String subjectName;
    @SerializedName("SubjectStatus")
    public String subjectStatus;
    @SerializedName("ScoreRed")
    public String scoreRed;
    @SerializedName("ScoreAmber")
    public String scoreAmber;
    @SerializedName("ScoreLevelPassing")
    public String scoreLevelPassing;

    public List<ChapterModel> chapters;

}
