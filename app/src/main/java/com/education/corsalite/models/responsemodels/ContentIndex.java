package com.education.corsalite.models.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mt0060 on 30/09/15.
 */
public class ContentIndex extends BaseModel implements Serializable {

    public String idEntity;
    public String idCourse;
    @SerializedName("CourseName")
    public String courseName;
    @SerializedName("CourseStatus")
    public String courseStatus;
    public String idCourseSubject;
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
    public String idCourseSubjectChapter;
    @SerializedName("ChapterName")
    public String chapterName;
    @SerializedName("ChapterStatus")
    public String chapterStatus;
    @SerializedName("ChapterSortOrder")
    public String chapterSortOrder;
    public String idTopic;
    @SerializedName("TopicName")
    public String topicName;
    @SerializedName("TopicStatus")
    public String topicStatus;
    @SerializedName("TopicSortOrder")
    public String topicSortOrder;
    public String idContent;
    @SerializedName("Type")
    public String type;
    @SerializedName("ContentName")
    public String contentName;
    @SerializedName("Status")
    public String status;
    @SerializedName("AuthorCopyright")
    public String authorCopyright;
    @SerializedName("UpdateTime")
    public String updateTime;
    @SerializedName("NamePrefix")
    public String namePrefix;
    @SerializedName("GivenName")
    public String givenName;
    @SerializedName("SurName")
    public String surName;
    @SerializedName("Rating")
    public String rating;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentIndex that = (ContentIndex) o;

        if (idEntity != null ? !idEntity.equals(that.idEntity) : that.idEntity != null)
            return false;
        if (idCourse != null ? !idCourse.equals(that.idCourse) : that.idCourse != null)
            return false;
        if (courseName != null ? !courseName.equals(that.courseName) : that.courseName != null)
            return false;
        if (courseStatus != null ? !courseStatus.equals(that.courseStatus) : that.courseStatus != null)
            return false;
        if (idCourseSubject != null ? !idCourseSubject.equals(that.idCourseSubject) : that.idCourseSubject != null)
            return false;
        if (subjectName != null ? !subjectName.equals(that.subjectName) : that.subjectName != null)
            return false;
        if (subjectStatus != null ? !subjectStatus.equals(that.subjectStatus) : that.subjectStatus != null)
            return false;
        if (scoreRed != null ? !scoreRed.equals(that.scoreRed) : that.scoreRed != null)
            return false;
        if (scoreAmber != null ? !scoreAmber.equals(that.scoreAmber) : that.scoreAmber != null)
            return false;
        if (scoreLevelPassing != null ? !scoreLevelPassing.equals(that.scoreLevelPassing) : that.scoreLevelPassing != null)
            return false;
        if (idCourseSubjectChapter != null ? !idCourseSubjectChapter.equals(that.idCourseSubjectChapter) : that.idCourseSubjectChapter != null)
            return false;
        if (chapterName != null ? !chapterName.equals(that.chapterName) : that.chapterName != null)
            return false;
        if (chapterStatus != null ? !chapterStatus.equals(that.chapterStatus) : that.chapterStatus != null)
            return false;
        if (chapterSortOrder != null ? !chapterSortOrder.equals(that.chapterSortOrder) : that.chapterSortOrder != null)
            return false;
        if (idTopic != null ? !idTopic.equals(that.idTopic) : that.idTopic != null) return false;
        if (topicName != null ? !topicName.equals(that.topicName) : that.topicName != null)
            return false;
        if (topicStatus != null ? !topicStatus.equals(that.topicStatus) : that.topicStatus != null)
            return false;
        if (topicSortOrder != null ? !topicSortOrder.equals(that.topicSortOrder) : that.topicSortOrder != null)
            return false;
        if (idContent != null ? !idContent.equals(that.idContent) : that.idContent != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (contentName != null ? !contentName.equals(that.contentName) : that.contentName != null)
            return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (authorCopyright != null ? !authorCopyright.equals(that.authorCopyright) : that.authorCopyright != null)
            return false;
        if (updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null)
            return false;
        if (namePrefix != null ? !namePrefix.equals(that.namePrefix) : that.namePrefix != null)
            return false;
        if (givenName != null ? !givenName.equals(that.givenName) : that.givenName != null)
            return false;
        if (surName != null ? !surName.equals(that.surName) : that.surName != null) return false;
        return !(rating != null ? !rating.equals(that.rating) : that.rating != null);

    }

    @Override
    public int hashCode() {
        int result = idEntity != null ? idEntity.hashCode() : 0;
        result = 31 * result + (idCourse != null ? idCourse.hashCode() : 0);
        result = 31 * result + (courseName != null ? courseName.hashCode() : 0);
        result = 31 * result + (courseStatus != null ? courseStatus.hashCode() : 0);
        result = 31 * result + (idCourseSubject != null ? idCourseSubject.hashCode() : 0);
        result = 31 * result + (subjectName != null ? subjectName.hashCode() : 0);
        result = 31 * result + (subjectStatus != null ? subjectStatus.hashCode() : 0);
        result = 31 * result + (scoreRed != null ? scoreRed.hashCode() : 0);
        result = 31 * result + (scoreAmber != null ? scoreAmber.hashCode() : 0);
        result = 31 * result + (scoreLevelPassing != null ? scoreLevelPassing.hashCode() : 0);
        result = 31 * result + (idCourseSubjectChapter != null ? idCourseSubjectChapter.hashCode() : 0);
        result = 31 * result + (chapterName != null ? chapterName.hashCode() : 0);
        result = 31 * result + (chapterStatus != null ? chapterStatus.hashCode() : 0);
        result = 31 * result + (chapterSortOrder != null ? chapterSortOrder.hashCode() : 0);
        result = 31 * result + (idTopic != null ? idTopic.hashCode() : 0);
        result = 31 * result + (topicName != null ? topicName.hashCode() : 0);
        result = 31 * result + (topicStatus != null ? topicStatus.hashCode() : 0);
        result = 31 * result + (topicSortOrder != null ? topicSortOrder.hashCode() : 0);
        result = 31 * result + (idContent != null ? idContent.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (contentName != null ? contentName.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (authorCopyright != null ? authorCopyright.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (namePrefix != null ? namePrefix.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (surName != null ? surName.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContentIndex{" +
                "idEntity='" + idEntity + '\'' +
                ", idCourse='" + idCourse + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseStatus='" + courseStatus + '\'' +
                ", idCourseSubject='" + idCourseSubject + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", subjectStatus='" + subjectStatus + '\'' +
                ", scoreRed='" + scoreRed + '\'' +
                ", scoreAmber='" + scoreAmber + '\'' +
                ", scoreLevelPassing='" + scoreLevelPassing + '\'' +
                ", idCourseSubjectChapter='" + idCourseSubjectChapter + '\'' +
                ", chapterName='" + chapterName + '\'' +
                ", chapterStatus='" + chapterStatus + '\'' +
                ", chapterSortOrder='" + chapterSortOrder + '\'' +
                ", idTopic='" + idTopic + '\'' +
                ", topicName='" + topicName + '\'' +
                ", topicStatus='" + topicStatus + '\'' +
                ", topicSortOrder='" + topicSortOrder + '\'' +
                ", idContent='" + idContent + '\'' +
                ", type='" + type + '\'' +
                ", contentName='" + contentName + '\'' +
                ", status='" + status + '\'' +
                ", authorCopyright='" + authorCopyright + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", namePrefix='" + namePrefix + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surName='" + surName + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}
