package com.education.corsalite.utils;

import com.education.corsalite.BuildConfig;

/**
 * Created by ayush on 15/09/15.
 */
public class Constants {

    public final static String PARENT_FOLDER = BuildConfig.PARENT_FOLDER;
    public final static String CONTENT_FOLDER = "Content";
    public final static String VIDEO_FOLDER = "Videos";
    public final static String TESTS_FOLDER = "Tests";

    public static final String HTML_PREFIX_URL = "file:///";

    public static final String HTML_FILE = "html";
    public static final String VIDEO_FILE = "mpg";
    public static final String TEST_FILE = "tst";

    public static final String EXERCISE_MODEL = "exercise_model";
    public static final String SELECTED_POSITION =  "selectedPosition";
    public static final String SELECTED_SUBJECT_NAME =  "selectedSubjectName";
    public static final String SELECTED_SUBJECTID =  "selectedSubjectId";
    public static final String DB_ROW_ID=  "dbRowId";
    public static final String SELECTED_CHAPTER =  "selectedChapter";
    public static final String SELECTED_CHAPTERID =  "selectedChapterId";
    public static final String SELECTED_CHAPTER_NAME =  "selectedChapterName";
    public static final String SELECTED_TOPIC_NAME =  "selectedTopic";
    public static final String SELECTED_TOPICID =  "selectedTopicId";
    public static final String LEVEL_CROSSED =  "level_crossed";
    public static final String PART_TEST_GRID_DATA =  "partTestGridData";
    public static final String TEST_TITLE =  "testTitle";
    public static final String SELECTED_COURSE =  "selectedCourse";
    public static final String SELECTED_ENTITY =  "selectedEntity";
    public static final String SELECTED_EXAM_NAME = "selectedExamName";
    public static final String SELECTED_QUESTION_PAPER = "selectedQuestionPaper";
    public static final String EXAM_TEMPLATE_ID = "examTemplateId";
    public static final String ADAPIVE_LEAERNING = "adaptiveLearning";
    public static final String QUESTIONS_COUNT = "questionsCount";
    public static final String TEST_COVERAGE_LIST_GSON = "testCoverageListGson";
    public static final String IS_OFFLINE = "IS_OFFLINE";
    public static final String PARTTEST_GRIDMODELS = "partTestGridElements";
    public static final String PARTTEST_EXAMMODEL = "partTestExamModel";
    public static final int STATUS_SUSPENDED = 22;
    public static final int STATUS_COMPLETED = 33;
    public static final int STATUS_START = 44;

    public static final int EXAM_DOWNLOADED_REQUEST_ID = 500;
    public static final int EXAM_ADVANCED_NOTIFICATION_REQUEST_ID = 501;
    public static final int EXAM_STARTED_REQUEST_ID = 502;
    public static final String WEBVIEW_DATA_FORMAT = "text/html; charset=UTF-8";
//    public static final String WEBVIEW_DATA_FORMAT = "text/html; charset=ISO-8859-1";

    public enum AnswerState {
        UNATTEMPTED("Unattended"),
        ANSWERED("Answered"),
        SKIPPED("Skipped"),
        FLAGGED("Flagged");

        private String value;

        AnswerState(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static AnswerState getEnum(String val) {
            if(val.equalsIgnoreCase(UNATTEMPTED.getValue())) {
                return UNATTEMPTED;
            } else if(val.equalsIgnoreCase(ANSWERED.getValue())) {
                return ANSWERED;
            } else if(val.equalsIgnoreCase(SKIPPED.getValue())) {
                return SKIPPED;
            } else if(val.equalsIgnoreCase(FLAGGED.getValue())) {
                return FLAGGED;
            }
            return UNATTEMPTED;
        }
    }
}

