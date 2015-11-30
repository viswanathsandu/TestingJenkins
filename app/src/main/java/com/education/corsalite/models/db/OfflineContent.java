package com.education.corsalite.models.db;

import com.education.corsalite.models.responsemodels.BaseModel;

/**
 * Created by Girish on 29/11/15.
 */
public class OfflineContent extends BaseModel {

    public String courseId;
    public String courseName;
    public String subjectId;
    public String subjectName;
    public String chapterId;
    public String chapterName;
    public String topicId;
    public String topicName;
    public String contentId;
    public String contentName;
    public String fileName;
    public String timeStamp;

    public OfflineContent(String courseId, String courseName, String subjectId, String subjectName,
                          String chapterId, String chapterName, String topicId, String topicName,
                          String contentId, String contentName, String fileName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.chapterId = chapterId;
        this.chapterName = chapterName;
        this.topicId = topicId;
        this.topicName = topicName;
        this.contentId = contentId;
        this.contentName = contentName;
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfflineContent that = (OfflineContent) o;

        if (courseId != null ? !courseId.equals(that.courseId) : that.courseId != null)
            return false;
        if (subjectId != null ? !subjectId.equals(that.subjectId) : that.subjectId != null)
            return false;
        if (chapterId != null ? !chapterId.equals(that.chapterId) : that.chapterId != null)
            return false;
        if (topicId != null ? !topicId.equals(that.topicId) : that.topicId != null) return false;
        return !(contentId != null ? !contentId.equals(that.contentId) : that.contentId != null);

    }

    @Override
    public int hashCode() {
        int result = courseId != null ? courseId.hashCode() : 0;
        result = 31 * result + (subjectId != null ? subjectId.hashCode() : 0);
        result = 31 * result + (chapterId != null ? chapterId.hashCode() : 0);
        result = 31 * result + (topicId != null ? topicId.hashCode() : 0);
        result = 31 * result + (contentId != null ? contentId.hashCode() : 0);
        return result;
    }
}
