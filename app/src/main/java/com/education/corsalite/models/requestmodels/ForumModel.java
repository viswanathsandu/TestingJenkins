package com.education.corsalite.models.requestmodels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ayush on 10/15/15.
 */
public class ForumModel {
    @SerializedName("idTopic")
    public String topicId;
    @SerializedName("idCourse")
    public String courseId;
    @SerializedName("idUser")
    public String userId;
    @SerializedName("idStudent")
    public String studentId;
    public String idCourseSubject;
    public String idCourseSubjectChapter;
    public String idUserPost;
    @SerializedName("PostSubject")
    public String postSubject;
    @SerializedName("ReferidUserPost")
    public String referIdUserPost;
    public String isAuthorOnly;
    @SerializedName("PostContent")
    public String postContent;
    @SerializedName("Locked")
    public String locked;
}
