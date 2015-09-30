package com.education.corsalite.models.responsemodels;

import java.io.Serializable;

/**
 * Created by mt0060 on 30/09/15.
 */
public class ContentIndex extends BaseModel implements Serializable {

    private String idEntity;
    private String idCourse;
    private String CourseName;
    private String CourseStatus;
    private String idCourseSubject;
    private String SubjectName;
    private String SubjectStatus;
    private String ScoreRed;
    private String ScoreAmber;
    private String ScoreLevelPassing;
    private String idCourseSubjectChapter;
    private String ChapterName;
    private String ChapterStatus;
    private String ChapterSortOrder;
    private String idTopic;
    private String TopicName;
    private String TopicStatus;
    private String TopicSortOrder;
    private String idContent;
    private String Type;
    private String ContentName;
    private String Status;
    private String AuthorCopyright;
    private String UpdateTime;
    private String NamePrefix;
    private String GivenName;
    private String SurName;
    private String Rating;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentIndex that = (ContentIndex) o;

        if (idEntity != null ? !idEntity.equals(that.idEntity) : that.idEntity != null)
            return false;
        if (idCourse != null ? !idCourse.equals(that.idCourse) : that.idCourse != null)
            return false;
        if (CourseName != null ? !CourseName.equals(that.CourseName) : that.CourseName != null)
            return false;
        if (CourseStatus != null ? !CourseStatus.equals(that.CourseStatus) : that.CourseStatus != null)
            return false;
        if (idCourseSubject != null ? !idCourseSubject.equals(that.idCourseSubject) : that.idCourseSubject != null)
            return false;
        if (SubjectName != null ? !SubjectName.equals(that.SubjectName) : that.SubjectName != null)
            return false;
        if (SubjectStatus != null ? !SubjectStatus.equals(that.SubjectStatus) : that.SubjectStatus != null)
            return false;
        if (ScoreRed != null ? !ScoreRed.equals(that.ScoreRed) : that.ScoreRed != null)
            return false;
        if (ScoreAmber != null ? !ScoreAmber.equals(that.ScoreAmber) : that.ScoreAmber != null)
            return false;
        if (ScoreLevelPassing != null ? !ScoreLevelPassing.equals(that.ScoreLevelPassing) : that.ScoreLevelPassing != null)
            return false;
        if (idCourseSubjectChapter != null ? !idCourseSubjectChapter.equals(that.idCourseSubjectChapter) : that.idCourseSubjectChapter != null)
            return false;
        if (ChapterName != null ? !ChapterName.equals(that.ChapterName) : that.ChapterName != null)
            return false;
        if (ChapterStatus != null ? !ChapterStatus.equals(that.ChapterStatus) : that.ChapterStatus != null)
            return false;
        if (ChapterSortOrder != null ? !ChapterSortOrder.equals(that.ChapterSortOrder) : that.ChapterSortOrder != null)
            return false;
        if (idTopic != null ? !idTopic.equals(that.idTopic) : that.idTopic != null) return false;
        if (TopicName != null ? !TopicName.equals(that.TopicName) : that.TopicName != null)
            return false;
        if (TopicStatus != null ? !TopicStatus.equals(that.TopicStatus) : that.TopicStatus != null)
            return false;
        if (TopicSortOrder != null ? !TopicSortOrder.equals(that.TopicSortOrder) : that.TopicSortOrder != null)
            return false;
        if (idContent != null ? !idContent.equals(that.idContent) : that.idContent != null)
            return false;
        if (Type != null ? !Type.equals(that.Type) : that.Type != null) return false;
        if (ContentName != null ? !ContentName.equals(that.ContentName) : that.ContentName != null)
            return false;
        if (Status != null ? !Status.equals(that.Status) : that.Status != null) return false;
        if (AuthorCopyright != null ? !AuthorCopyright.equals(that.AuthorCopyright) : that.AuthorCopyright != null)
            return false;
        if (UpdateTime != null ? !UpdateTime.equals(that.UpdateTime) : that.UpdateTime != null)
            return false;
        if (NamePrefix != null ? !NamePrefix.equals(that.NamePrefix) : that.NamePrefix != null)
            return false;
        if (GivenName != null ? !GivenName.equals(that.GivenName) : that.GivenName != null)
            return false;
        if (SurName != null ? !SurName.equals(that.SurName) : that.SurName != null) return false;
        return !(Rating != null ? !Rating.equals(that.Rating) : that.Rating != null);

    }

    @Override
    public int hashCode() {
        int result = idEntity != null ? idEntity.hashCode() : 0;
        result = 31 * result + (idCourse != null ? idCourse.hashCode() : 0);
        result = 31 * result + (CourseName != null ? CourseName.hashCode() : 0);
        result = 31 * result + (CourseStatus != null ? CourseStatus.hashCode() : 0);
        result = 31 * result + (idCourseSubject != null ? idCourseSubject.hashCode() : 0);
        result = 31 * result + (SubjectName != null ? SubjectName.hashCode() : 0);
        result = 31 * result + (SubjectStatus != null ? SubjectStatus.hashCode() : 0);
        result = 31 * result + (ScoreRed != null ? ScoreRed.hashCode() : 0);
        result = 31 * result + (ScoreAmber != null ? ScoreAmber.hashCode() : 0);
        result = 31 * result + (ScoreLevelPassing != null ? ScoreLevelPassing.hashCode() : 0);
        result = 31 * result + (idCourseSubjectChapter != null ? idCourseSubjectChapter.hashCode() : 0);
        result = 31 * result + (ChapterName != null ? ChapterName.hashCode() : 0);
        result = 31 * result + (ChapterStatus != null ? ChapterStatus.hashCode() : 0);
        result = 31 * result + (ChapterSortOrder != null ? ChapterSortOrder.hashCode() : 0);
        result = 31 * result + (idTopic != null ? idTopic.hashCode() : 0);
        result = 31 * result + (TopicName != null ? TopicName.hashCode() : 0);
        result = 31 * result + (TopicStatus != null ? TopicStatus.hashCode() : 0);
        result = 31 * result + (TopicSortOrder != null ? TopicSortOrder.hashCode() : 0);
        result = 31 * result + (idContent != null ? idContent.hashCode() : 0);
        result = 31 * result + (Type != null ? Type.hashCode() : 0);
        result = 31 * result + (ContentName != null ? ContentName.hashCode() : 0);
        result = 31 * result + (Status != null ? Status.hashCode() : 0);
        result = 31 * result + (AuthorCopyright != null ? AuthorCopyright.hashCode() : 0);
        result = 31 * result + (UpdateTime != null ? UpdateTime.hashCode() : 0);
        result = 31 * result + (NamePrefix != null ? NamePrefix.hashCode() : 0);
        result = 31 * result + (GivenName != null ? GivenName.hashCode() : 0);
        result = 31 * result + (SurName != null ? SurName.hashCode() : 0);
        result = 31 * result + (Rating != null ? Rating.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContentIndex{" +
                "idEntity='" + idEntity + '\'' +
                ", idCourse='" + idCourse + '\'' +
                ", CourseName='" + CourseName + '\'' +
                ", CourseStatus='" + CourseStatus + '\'' +
                ", idCourseSubject='" + idCourseSubject + '\'' +
                ", SubjectName='" + SubjectName + '\'' +
                ", SubjectStatus='" + SubjectStatus + '\'' +
                ", ScoreRed='" + ScoreRed + '\'' +
                ", ScoreAmber='" + ScoreAmber + '\'' +
                ", ScoreLevelPassing='" + ScoreLevelPassing + '\'' +
                ", idCourseSubjectChapter='" + idCourseSubjectChapter + '\'' +
                ", ChapterName='" + ChapterName + '\'' +
                ", ChapterStatus='" + ChapterStatus + '\'' +
                ", ChapterSortOrder='" + ChapterSortOrder + '\'' +
                ", idTopic='" + idTopic + '\'' +
                ", TopicName='" + TopicName + '\'' +
                ", TopicStatus='" + TopicStatus + '\'' +
                ", TopicSortOrder='" + TopicSortOrder + '\'' +
                ", idContent='" + idContent + '\'' +
                ", Type='" + Type + '\'' +
                ", ContentName='" + ContentName + '\'' +
                ", Status='" + Status + '\'' +
                ", AuthorCopyright='" + AuthorCopyright + '\'' +
                ", UpdateTime='" + UpdateTime + '\'' +
                ", NamePrefix='" + NamePrefix + '\'' +
                ", GivenName='" + GivenName + '\'' +
                ", SurName='" + SurName + '\'' +
                ", Rating='" + Rating + '\'' +
                '}';
    }
}
