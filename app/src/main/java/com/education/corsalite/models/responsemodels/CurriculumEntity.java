package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vissu on 6/28/16.
 */
public class CurriculumEntity extends BaseModel {

    @SerializedName("idEntity")
    public String idEntity;
    @SerializedName("idCurriculumScheduleTask")
    public String idCurriculumScheduleTask;
    @SerializedName("idTaskCategoryTypeXref")
    public String idTaskCategoryTypeXref;
    @SerializedName("Description")
    public String description;
    @SerializedName("TaskAssignedDate")
    public String taskAssignedDate;
    @SerializedName("DueDate")
    public String dueDate;
    @SerializedName("TaskCompleted")
    public String taskCompleted;
    @SerializedName("idCourse")
    public String idCourse;
    @SerializedName("idCourseSubject")
    public String idCourseSubject;
    @SerializedName("idCourseSubjectChapter")
    public String idCourseSubjectChapter;
    @SerializedName("idTaskType")
    public String idTaskType;
    @SerializedName("TaskTypeName")
    public String taskTypeName;
    @SerializedName("idStudent")
    public String idStudent;
    @SerializedName("Points")
    public String points;
    @SerializedName("RecType")
    public String recType;
    @SerializedName("idTestQuestionPaper")
    public String idTestQuestionPaper;
    @SerializedName("idCalendar")
    public String idCalendar;
    @SerializedName("OverDueDays")
    public String overDueDays;
    @SerializedName("idTopic")
    public String idTopic;

    public CurriculumEntity() {}
}
