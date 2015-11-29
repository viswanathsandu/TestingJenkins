package com.education.corsalite.models.db;

/**
 * Created by Girish on 29/11/15.
 */
public class OfflineContent {

    String courseId;
    String courseName;
    String subjectId;
    String subjectName;
    String chapterId;
    String chapterName;
    String topicId;
    String topicName;
    String contentId;
    String contentName;
    String fileName;

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
}
