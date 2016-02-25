package com.education.corsalite.db;

import android.content.Context;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.db.ContentIndexResponse;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.db.reqres.ReqRes;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Created by vissu on 9/16/15.
 */
public class DbManager {

    private static DbManager instance;
    private DbService dbService = null;
    private Context context;

    private DbManager(Context context) {
        this.context = context;
    }

    public static DbManager getInstance(Context context) {
        if (instance == null) {
            instance = new DbManager(context);
            instance.dbService = new DbService();
        }
        return instance;
    }

    public ContentIndexResponse getContentIndexList(String courseId, String studentId) {
        Select specificAuthorQuery = Select.from(ContentIndexResponse.class)
                .where(Condition.prop("course_id").eq(courseId),
                        Condition.prop("student_id").eq(studentId))
                .limit("1");
        ContentIndexResponse contentIndexResponses = (ContentIndexResponse) specificAuthorQuery.first();
        return contentIndexResponses;
    }

    public void saveContentIndexList(String contentIndexJson, String courseId, String studentId) {
        Select specificAuthorQuery = Select.from(ContentIndexResponse.class)
                .where(Condition.prop("course_id").eq(courseId),
                        Condition.prop("student_id").eq(studentId))
                .limit("1");
        ContentIndexResponse contentIndexResponses = (ContentIndexResponse) specificAuthorQuery.first();
        if (contentIndexResponses != null) {
            contentIndexResponses.courseId = courseId;
            contentIndexResponses.studentId = studentId;
            contentIndexResponses.contentIndexesJson = contentIndexJson;
        } else {
            contentIndexResponses = new ContentIndexResponse(contentIndexJson, courseId, studentId);
        }
        contentIndexResponses.save();
    }

    /**
     * User Profile Db stuff
     */
    public <T> void saveReqRes(final ReqRes<T> reqres) {
        if(reqres == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    List<? extends ReqRes> reqResList = dbService.Get(reqres.getClass());
                    if (reqResList != null && !reqResList.isEmpty()) {
                        for (ReqRes reqresItem : reqResList) {
                            if (reqresItem.isRequestSame(reqres)) {
                                reqresItem.response = reqres.response;
                                dbService.Save(reqresItem);
                                return;
                            }
                        }
                    }
                    dbService.Save(reqres);
                }
            }
        }).start();
    }

    public <T> void getResponse(ReqRes<T> reqres, ApiCallback<T> callback) {
        new GetFromDbAsync<T>(dbService, reqres, callback).execute();
    }

    public void saveOfflineContent(final List<OfflineContent> offlineContents) {
        for (final OfflineContent offlineContent : offlineContents) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        List<OfflineContent> offlinecontentList = dbService.Get(OfflineContent.class);
                        for (OfflineContent savedOfflineContent : offlinecontentList) {
                            if (offlineContent.equals(savedOfflineContent)) {
                                savedOfflineContent.timeStamp = offlineContent.timeStamp;
                                dbService.Save(savedOfflineContent);
                                return;
                            }
                        }
                        dbService.Save(offlineContent);
                    }
                }
            }).start();
        }
    }

    public void deleteOfflineContent(final List<OfflineContent> offlineContents) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        for (OfflineContent offlineContent : offlineContents) {
                            dbService.Delete(OfflineContent.class, offlineContent);
                        }
                    }
                }
            }).start();
    }

    public void deleteAllOfflineContent() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        List<OfflineContent> offlinecontentList = dbService.Get(OfflineContent.class);
                        for (OfflineContent savedOfflineContent : offlinecontentList) {
                            dbService.Delete(OfflineContent.class, savedOfflineContent);
                        }
                    }
                }
            }).start();
    }

    public <T> void getOfflineContentList(ApiCallback<List<OfflineContent>> callback) {
        new GetDataFromDbAsync(dbService, callback).execute();
    }

}
