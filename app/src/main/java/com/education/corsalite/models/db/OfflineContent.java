package com.education.corsalite.models.db;

import com.education.corsalite.enums.OfflineContentStatus;
import com.education.corsalite.models.responsemodels.BaseModel;
import com.orm.dsl.Ignore;

/**
 * Created by Girish on 29/11/15.
 */
public class OfflineContent extends BaseModel {

    public String courseId;
    @Ignore
    public String courseName;
    public String subjectId;
    @Ignore
    public String subjectName;
    public String chapterId;
    @Ignore
    public String chapterName;
    public String topicId;
    @Ignore
    public String topicName;
    public String contentId;
    @Ignore
    public String contentName;
    @Ignore
    public String fileName;
    @Ignore
    public String videoStartTime = "0";
    public String timeStamp;
    public String earnedMarks;
    public String totalTestedMarks;
    public String scoreAmber;
    public String scoreRed;
    public int progress = 0;
    public int downloadId;
    public OfflineContentStatus status;

    public OfflineContent() {}

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
        if (topicId != null ? !topicId.equals(that.topicId) : that.topicId != null)
            return false;
        if(contentId == null && that.contentId == null)
            return true;
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
