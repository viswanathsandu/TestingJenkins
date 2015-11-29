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
        OfflineContent obj = (OfflineContent)o;
        return courseId.equals(obj.chapterId)
                && subjectId.equals(obj.subjectId)
                && chapterId.equals(obj.chapterId)
                && topicId.equals(obj.topicId)
                && contentId.equals(obj.contentId);
    }
}
