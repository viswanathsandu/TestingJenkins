package com.education.corsalite.models.db;

import com.education.corsalite.event.ContentReadingEvent;
import com.education.corsalite.models.requestmodels.UserEventsModel;
import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.TestAnswerPaper;

/**
 * Created by vissu on 7/3/16.
 */

public class SyncModel extends BaseModel {

    private ContentReadingEvent contentReadingEvent;
    private UserEventsModel userEventsModel;
    private TestAnswerPaper testAnswerPaper;

    public void setContentReadingEvent(ContentReadingEvent event) {
        this.contentReadingEvent = event;
        userEventsModel = null;
        testAnswerPaper = null;
    }

    public void setTestAnswerPaperEvent(TestAnswerPaper event) {
        contentReadingEvent = null;
        userEventsModel = null;
        this.testAnswerPaper = event;
    }

    public void setUserEventsModelEvent(UserEventsModel event) {
        contentReadingEvent = null;
        this.userEventsModel = event;
        testAnswerPaper = null;
    }

    public ContentReadingEvent getContentReadingEvent() {
        return contentReadingEvent;
    }

    public UserEventsModel getUserEventsModel() {
        return userEventsModel;
    }

    public TestAnswerPaper getTestAnswerPaper() {
        return testAnswerPaper;
    }
}
