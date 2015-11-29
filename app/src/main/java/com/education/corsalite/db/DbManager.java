package com.education.corsalite.db;

import android.content.Context;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.db.ContentIndexResponse;
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
        if(instance == null) {
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
        }else {
            contentIndexResponses = new ContentIndexResponse(contentIndexJson, courseId, studentId);
        }
        contentIndexResponses.save();
    }

    /**
     * User Profile Db stuff
     */
    public <T>  void saveReqRes(final ReqRes<T> reqres) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized(this) {
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


}
