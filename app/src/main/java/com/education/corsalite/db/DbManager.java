package com.education.corsalite.db;

import android.content.Context;
import android.text.TextUtils;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.ExerciseOfflineModel;
import com.education.corsalite.models.MockTest;
import com.education.corsalite.models.OfflineTestModel;
import com.education.corsalite.models.ScheduledTestList;
import com.education.corsalite.models.db.ContentIndexResponse;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.db.reqres.ReqRes;
import com.education.corsalite.models.examengine.BaseTest;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.utils.L;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

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
        if (reqres == null) {
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
                                reqresItem.setUserId();
                                reqresItem.response = reqres.response;
                                dbService.Save(reqresItem);
                                return;
                            }
                        }
                    }
                    reqres.setUserId();
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
                                savedOfflineContent.setUserId();
                                savedOfflineContent.timeStamp = offlineContent.timeStamp;
                                dbService.Save(savedOfflineContent);
                                return;
                            }
                        }
                        offlineContent.setUserId();
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

    public void updateOfflineTestModel(final long date, final int status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    List<OfflineTestModel> offlinecontentList = dbService.Get(OfflineTestModel.class);
                    for (OfflineTestModel savedOfflineContent : offlinecontentList) {
                        if (date == savedOfflineContent.dateTime) {
                            savedOfflineContent.status = status;
                            dbService.Update(OfflineTestModel.class, savedOfflineContent);
                        }
                    }
                }
            }
        }).start();
    }

    public <T> void getOfflineContentList(ApiCallback<List<OfflineContent>> callback) {
        new GetDataFromDbAsync(dbService, callback).execute();
    }

    public void saveOfflineTest(final OfflineTestModel model) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    model.setUserId();
                    dbService.Save(model);
                }
            }
        }).start();
    }

    public void saveOfflineExerciseTest(final ExerciseOfflineModel exercise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    List<ExerciseOfflineModel> models = dbService.Get(ExerciseOfflineModel.class);
                    for (ExerciseOfflineModel model : models) {
                        if (model.equals(exercise)) {
                            model.setUserId();
                            model.questions = exercise.questions;
                            dbService.Save(model);
                            return;
                        }
                    }
                    exercise.setUserId();
                    dbService.Save(exercise);
                }
            }
        }).start();
    }

    public void getOfflineExerciseModels(final String courseId, final ApiCallback<List<ExerciseOfflineModel>> callback) {
        new GetOfflineExerciseFromDb(dbService, new ApiCallback<List<ExerciseOfflineModel>>(context) {
            public void success(List<ExerciseOfflineModel> offlineTestModels, Response response) {
                super.success(offlineTestModels, response);
                List<ExerciseOfflineModel> offlineExercises = new ArrayList<ExerciseOfflineModel>();
                for (ExerciseOfflineModel model : offlineTestModels) {
                    try {
                        if (model.courseId.equals(courseId) && model.isCurrentUser()) {
                            offlineExercises.add(model);
                        }
                    } catch (Exception e) {
                        CorsaliteError error = new CorsaliteError();
                        error.message = "No data found";
                        callback.failure(error);
                        L.error(e.getMessage(), e);
                    }
                }
                callback.success(offlineExercises, response);
            }
        }).execute();
    }

    public void getAllExamModels(final String subjectId, final ApiCallback<BaseTest> callback) {
        new GetOfflineTestFromDb(dbService, new ApiCallback<List<OfflineTestModel>>(context) {
            public void success(List<OfflineTestModel> offlineTestModels, Response response) {
                super.success(offlineTestModels, response);
                for (OfflineTestModel model : offlineTestModels) {
                    try {
                        if (model != null && model.baseTest.subjectId.equalsIgnoreCase(subjectId) && model.isCurrentUser()) {
                            callback.success(model.baseTest, response);
                        }
                    } catch (Exception e) {
                        CorsaliteError error = new CorsaliteError();
                        error.message = "No data found";
                        callback.failure(error);
                        L.error(e.getMessage(), e);
                    }
                }
            }
        }).execute();
    }

    public void deleteOfflineMockTest(OfflineTestModel model) {
        dbService.Delete(OfflineTestModel.class, model);
    }

    public void getAllOfflineMockTests(ApiCallback<List<OfflineTestModel>> callback) {
        new GetOfflineTestFromDb(dbService, callback).execute();
    }

    public void getAllExamModels(final MockTest mockTest, final ApiCallback<OfflineTestModel> callback) {
        new GetOfflineTestFromDb(dbService, new ApiCallback<List<OfflineTestModel>>(context) {
            public void success(List<OfflineTestModel> offlineTestModels, Response response) {
                super.success(offlineTestModels, response);
                for (OfflineTestModel model : offlineTestModels) {
                    if (model != null && model.mockTest != null && !TextUtils.isEmpty(model.mockTest.examTemplateId)
                            && model.mockTest.examTemplateId.equalsIgnoreCase(mockTest.examTemplateId)
                            && model.isCurrentUser()) {
                        callback.success(model, response);
                    }
                }
            }
        }).execute();
    }

    public void getAllExamModels(final ScheduledTestList.ScheduledTestsArray scheduledTest, final ApiCallback<List<ExamModel>> callback) {
        new GetOfflineTestFromDb(dbService, new ApiCallback<List<OfflineTestModel>>(context) {
            public void success(List<OfflineTestModel> offlineTestModels, Response response) {
                super.success(offlineTestModels, response);
                for (OfflineTestModel model : offlineTestModels) {
                    if (model != null && model.scheduledTest != null && !TextUtils.isEmpty(model.scheduledTest.testQuestionPaperId)
                            && model.scheduledTest.testQuestionPaperId.equalsIgnoreCase(scheduledTest.testQuestionPaperId)
                            && model.isCurrentUser()) {
                        callback.success(model.examModels, response);
                    }
                }
            }
        }).execute();
    }
}
