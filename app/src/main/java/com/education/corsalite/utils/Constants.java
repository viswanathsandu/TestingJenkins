package com.education.corsalite.utils;

/**
 * Created by ayush on 15/09/15.
 */
public class Constants {
    public final static String FORGOT_PASSWORD_URL = "http://staging.corsalite.com/v1/login/forgotPassword";
    public final static String ADD_GUARDIAN_URL = "http://staging.corsalite.com/v1/profile/guardianInfo";
    public final static String ADD_COURSES_URL = "http://staging.corsalite.com/v1/profile/courseInfo";
    public final static String REDEEM_URL = "http://staging.corsalite.com/v1/redeem";

    public final static String PARENT_FOLDER = "Corsalite";
    public final static String HTML_FOLDER = "Html";
    public final static String VIDEO_FOLDER = "Video";


    public static final String VIDEO_PREFIX_URL = "http://app.corsalite.com/v1/";
    public static final String HTML_PREFIX_URL = "file:///";

    public static final String HTML_FILE = "html";
    public static final String VIDEO_FILE = "mpg";

    public static final String EXERCISE_MODEL = "exercise_model";
    public static final String SELECTED_POSITION =  "selectedPosition";
    public static final String SELECTED_SUBJECT =  "selectedSubject";
    public static final String SELECTED_SUBJECTID =  "selectedSubjectId";
    public static final String SELECTED_CHAPTER =  "selectedChapter";
    public static final String SELECTED_CHAPTERID =  "selectedChapterId";
    public static final String SELECTED_CHAPTER_NAME =  "selectedChapterName";
    public static final String SELECTED_TOPIC =  "selectedTopic";
    public static final String SELECTED_TOPICID =  "selectedTopicId";
    public static final String TEST_TITLE =  "testTitle";
    public static final String SELECTED_COURSE =  "selectedCourse";
    public static final String SELECTED_ENTITY =  "selectedEntity";


    public static enum AnswerState {
        UNATTEMPTED, ANSWERED, SKIPPED, FLAGGED
    }
}

