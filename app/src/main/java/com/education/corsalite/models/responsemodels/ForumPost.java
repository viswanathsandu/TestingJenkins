package com.education.corsalite.models.responsemodels;

/**
 * Created by sridharnalam on 1/13/16.
 */
public class ForumPost {

    public String idCourseSubject;
    public String CourseName;
    public String postLikes;
    public String TopicName;
    public String createrLastView;
    public String ReferidUserPost;
    public String idTopic;
    public String Datetime;
    public String idUserPost;
    public String postReplies;
    public String htmlText;
    public String PostTags;
    public String LockedDate;
    public String idCourseSubjectChapter;
    public String EditedDate;
    public String PhotoUrl;
    public String SearchPost;
    public String LockedBy;
    public String postViews;
    public String PostSubject;
    public String UserType;
    public String bookmark;
    public String ChapterName;
    public String idCourse;
    public String DisplayName;
    public String SubjectName;
    public String isAuthorOnly;
    public String idUser;
    public String EditedBy;
    public String Locked;
    public String IsLiked;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForumPost forumPost = (ForumPost) o;

        if (idCourseSubject != null ? !idCourseSubject.equals(forumPost.idCourseSubject) : forumPost.idCourseSubject != null)
            return false;
        if (CourseName != null ? !CourseName.equals(forumPost.CourseName) : forumPost.CourseName != null)
            return false;
        if (postLikes != null ? !postLikes.equals(forumPost.postLikes) : forumPost.postLikes != null)
            return false;
        if (TopicName != null ? !TopicName.equals(forumPost.TopicName) : forumPost.TopicName != null)
            return false;
        if (createrLastView != null ? !createrLastView.equals(forumPost.createrLastView) : forumPost.createrLastView != null)
            return false;
        if (ReferidUserPost != null ? !ReferidUserPost.equals(forumPost.ReferidUserPost) : forumPost.ReferidUserPost != null)
            return false;
        if (idTopic != null ? !idTopic.equals(forumPost.idTopic) : forumPost.idTopic != null)
            return false;
        if (Datetime != null ? !Datetime.equals(forumPost.Datetime) : forumPost.Datetime != null)
            return false;
        if (idUserPost != null ? !idUserPost.equals(forumPost.idUserPost) : forumPost.idUserPost != null)
            return false;
        if (postReplies != null ? !postReplies.equals(forumPost.postReplies) : forumPost.postReplies != null)
            return false;
        if (htmlText != null ? !htmlText.equals(forumPost.htmlText) : forumPost.htmlText != null)
            return false;
        if (PostTags != null ? !PostTags.equals(forumPost.PostTags) : forumPost.PostTags != null)
            return false;
        if (LockedDate != null ? !LockedDate.equals(forumPost.LockedDate) : forumPost.LockedDate != null)
            return false;
        if (idCourseSubjectChapter != null ? !idCourseSubjectChapter.equals(forumPost.idCourseSubjectChapter) : forumPost.idCourseSubjectChapter != null)
            return false;
        if (EditedDate != null ? !EditedDate.equals(forumPost.EditedDate) : forumPost.EditedDate != null)
            return false;
        if (PhotoUrl != null ? !PhotoUrl.equals(forumPost.PhotoUrl) : forumPost.PhotoUrl != null)
            return false;
        if (SearchPost != null ? !SearchPost.equals(forumPost.SearchPost) : forumPost.SearchPost != null)
            return false;
        if (LockedBy != null ? !LockedBy.equals(forumPost.LockedBy) : forumPost.LockedBy != null)
            return false;
        if (postViews != null ? !postViews.equals(forumPost.postViews) : forumPost.postViews != null)
            return false;
        if (PostSubject != null ? !PostSubject.equals(forumPost.PostSubject) : forumPost.PostSubject != null)
            return false;
        if (UserType != null ? !UserType.equals(forumPost.UserType) : forumPost.UserType != null)
            return false;
        if (bookmark != null ? !bookmark.equals(forumPost.bookmark) : forumPost.bookmark != null)
            return false;
        if (ChapterName != null ? !ChapterName.equals(forumPost.ChapterName) : forumPost.ChapterName != null)
            return false;
        if (idCourse != null ? !idCourse.equals(forumPost.idCourse) : forumPost.idCourse != null)
            return false;
        if (DisplayName != null ? !DisplayName.equals(forumPost.DisplayName) : forumPost.DisplayName != null)
            return false;
        if (SubjectName != null ? !SubjectName.equals(forumPost.SubjectName) : forumPost.SubjectName != null)
            return false;
        if (isAuthorOnly != null ? !isAuthorOnly.equals(forumPost.isAuthorOnly) : forumPost.isAuthorOnly != null)
            return false;
        if (idUser != null ? !idUser.equals(forumPost.idUser) : forumPost.idUser != null)
            return false;
        if (EditedBy != null ? !EditedBy.equals(forumPost.EditedBy) : forumPost.EditedBy != null)
            return false;
        return !(Locked != null ? !Locked.equals(forumPost.Locked) : forumPost.Locked != null);

    }

    @Override
    public int hashCode() {
        int result = idCourseSubject != null ? idCourseSubject.hashCode() : 0;
        result = 31 * result + (CourseName != null ? CourseName.hashCode() : 0);
        result = 31 * result + (postLikes != null ? postLikes.hashCode() : 0);
        result = 31 * result + (TopicName != null ? TopicName.hashCode() : 0);
        result = 31 * result + (createrLastView != null ? createrLastView.hashCode() : 0);
        result = 31 * result + (ReferidUserPost != null ? ReferidUserPost.hashCode() : 0);
        result = 31 * result + (idTopic != null ? idTopic.hashCode() : 0);
        result = 31 * result + (Datetime != null ? Datetime.hashCode() : 0);
        result = 31 * result + (idUserPost != null ? idUserPost.hashCode() : 0);
        result = 31 * result + (postReplies != null ? postReplies.hashCode() : 0);
        result = 31 * result + (htmlText != null ? htmlText.hashCode() : 0);
        result = 31 * result + (PostTags != null ? PostTags.hashCode() : 0);
        result = 31 * result + (LockedDate != null ? LockedDate.hashCode() : 0);
        result = 31 * result + (idCourseSubjectChapter != null ? idCourseSubjectChapter.hashCode() : 0);
        result = 31 * result + (EditedDate != null ? EditedDate.hashCode() : 0);
        result = 31 * result + (PhotoUrl != null ? PhotoUrl.hashCode() : 0);
        result = 31 * result + (SearchPost != null ? SearchPost.hashCode() : 0);
        result = 31 * result + (LockedBy != null ? LockedBy.hashCode() : 0);
        result = 31 * result + (postViews != null ? postViews.hashCode() : 0);
        result = 31 * result + (PostSubject != null ? PostSubject.hashCode() : 0);
        result = 31 * result + (UserType != null ? UserType.hashCode() : 0);
        result = 31 * result + (bookmark != null ? bookmark.hashCode() : 0);
        result = 31 * result + (ChapterName != null ? ChapterName.hashCode() : 0);
        result = 31 * result + (idCourse != null ? idCourse.hashCode() : 0);
        result = 31 * result + (DisplayName != null ? DisplayName.hashCode() : 0);
        result = 31 * result + (SubjectName != null ? SubjectName.hashCode() : 0);
        result = 31 * result + (isAuthorOnly != null ? isAuthorOnly.hashCode() : 0);
        result = 31 * result + (idUser != null ? idUser.hashCode() : 0);
        result = 31 * result + (EditedBy != null ? EditedBy.hashCode() : 0);
        result = 31 * result + (Locked != null ? Locked.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ForumPost{" +
                "idCourseSubject='" + idCourseSubject + '\'' +
                ", CourseName='" + CourseName + '\'' +
                ", postLikes='" + postLikes + '\'' +
                ", TopicName='" + TopicName + '\'' +
                ", createrLastView='" + createrLastView + '\'' +
                ", ReferidUserPost='" + ReferidUserPost + '\'' +
                ", idTopic='" + idTopic + '\'' +
                ", Datetime='" + Datetime + '\'' +
                ", idUserPost='" + idUserPost + '\'' +
                ", postReplies='" + postReplies + '\'' +
                ", htmlText='" + htmlText + '\'' +
                ", PostTags='" + PostTags + '\'' +
                ", LockedDate='" + LockedDate + '\'' +
                ", idCourseSubjectChapter='" + idCourseSubjectChapter + '\'' +
                ", EditedDate='" + EditedDate + '\'' +
                ", PhotoUrl='" + PhotoUrl + '\'' +
                ", SearchPost='" + SearchPost + '\'' +
                ", LockedBy='" + LockedBy + '\'' +
                ", postViews='" + postViews + '\'' +
                ", PostSubject='" + PostSubject + '\'' +
                ", UserType='" + UserType + '\'' +
                ", bookmark='" + bookmark + '\'' +
                ", ChapterName='" + ChapterName + '\'' +
                ", idCourse='" + idCourse + '\'' +
                ", DisplayName='" + DisplayName + '\'' +
                ", SubjectName='" + SubjectName + '\'' +
                ", isAuthorOnly='" + isAuthorOnly + '\'' +
                ", idUser='" + idUser + '\'' +
                ", EditedBy='" + EditedBy + '\'' +
                ", Locked='" + Locked + '\'' +
                ", isLiked='" + IsLiked + '\'' +
                '}';
    }
}

