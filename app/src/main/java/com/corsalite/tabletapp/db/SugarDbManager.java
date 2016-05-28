package com.corsalite.tabletapp.db;

import android.content.Context;
import android.text.TextUtils;

import com.corsalite.tabletapp.api.ApiCallback;
import com.corsalite.tabletapp.models.db.ExerciseOfflineModel;
import com.corsalite.tabletapp.models.db.MockTest;
import com.corsalite.tabletapp.models.db.OfflineContent;
import com.corsalite.tabletapp.models.db.OfflineTestObjectModel;
import com.corsalite.tabletapp.models.db.ScheduledTestsArray;
import com.corsalite.tabletapp.models.db.reqres.LoginReqRes;
import com.corsalite.tabletapp.models.db.reqres.ReqRes;
import com.corsalite.tabletapp.models.db.reqres.requests.AbstractBaseRequest;
import com.corsalite.tabletapp.models.examengine.BaseTest;
import com.corsalite.tabletapp.models.responsemodels.BaseModel;
import com.corsalite.tabletapp.models.responsemodels.CorsaliteError;
import com.corsalite.tabletapp.models.responsemodels.ExamModel;
import com.corsalite.tabletapp.utils.L;
import com.corsalite.tabletapp.utils.MockUtils;
import com.google.gson.Gson;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vissu on 5/11/16.
 */
public class SugarDbManager {
    private static SugarDbManager instance;
    private Context context;

    private SugarDbManager() {
    }

    public static SugarDbManager get(Context context) {
        if (instance == null) {
            instance = new SugarDbManager();
        }
        instance.context = context;
        return instance;
    }

    public <T extends BaseModel> void save(T object) {
        if(object != null) {
            object.reflectionJsonString = new Gson().toJson(object);
            object.setUserId();
            object.save();
        }
    }

    public <T extends BaseModel> void save(List<T> objects) {
        if(objects != null) {
            for(T object : objects) {
                object.reflectionJsonString = new Gson().toJson(object);
            }
            SugarRecord.saveInTx(objects);
        }
    }

    public <T extends BaseModel> List<T> fetchRecords(Class<T> type) {
        List<T> results = new ArrayList<>();
        try {
            if (type != null) {
                List<T> allList = SugarRecord.listAll(type);
                for (T t : allList) {
                    if(t.reflectionJsonString != null) {
                        T object = new Gson().fromJson(t.reflectionJsonString, type);
                        object.setId(t.getId());
                        if(TextUtils.isEmpty(object.userId) || object.isCurrentUser()) {
                            results.add(object);
                        }
                    }
                }
            }
        } catch(Exception e) {
            L.error(e.getMessage(), e);
        }
        return results;
    }

    public List<OfflineContent> getOfflineContents(String courseId) {
        List<OfflineContent> results = new ArrayList<>();
        try {
            List<OfflineContent> offlineContents = fetchRecords(OfflineContent.class);
            if (TextUtils.isEmpty(courseId)) {
                return offlineContents;
            }
            for (OfflineContent offlineContent : offlineContents) {
                if (offlineContent.courseId.equals(courseId)) {
                    results.add(offlineContent);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return results;
    }

    private Long getOfflineContentId(List<OfflineContent> offlineContents, OfflineContent content) {
        try {
            for (OfflineContent offlineContent : offlineContents) {
                if (offlineContent.equals(content)) {
                    return offlineContent.getId();
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public void saveOfflineContents(List<OfflineContent> offlineContents) {
        List<OfflineContent> offlineContentsInDb = fetchRecords(OfflineContent.class);
        for (OfflineContent content : offlineContents) {
            Long id = getOfflineContentId(offlineContentsInDb, content);
            if(id != null) {
                content.setId(id);
            }
        }
        save(offlineContents);
    }

    public void deleteOfflineContent(OfflineContent offlineContent) {
        try {
            offlineContent.delete();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void deleteOfflineContents(List<OfflineContent> offlineContents) {
        for (OfflineContent content : offlineContents) {
            deleteOfflineContent(content);
        }
    }

    public <P extends AbstractBaseRequest, T> void saveReqRes(final ReqRes<P, T> reqres) {
        if (reqres == null) {
            return;
        }
        try {
            List<? extends ReqRes> reqResList = fetchRecords(reqres.getClass());
            if (reqResList != null && !reqResList.isEmpty()) {
                for (ReqRes<P, T> reqresItem : reqResList) {
                    if (reqresItem.isRequestSame(reqres)) {
                        reqresItem.response = reqres.response;
                        save(reqresItem);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        save(reqres);
    }

    public <P extends AbstractBaseRequest, T> void getResponse(ReqRes<P,T> reqres, ApiCallback<T> callback) {
        Object s = null;
        try {
            if (reqres != null) {
                List<? extends ReqRes> reqResList = fetchRecords(reqres.getClass());
                if (reqResList != null && !reqResList.isEmpty()) {
                    for (ReqRes reqresItem : reqResList) {
                        if (reqresItem instanceof LoginReqRes) {
                            if (reqresItem != null && reqresItem.isRequestSame(reqres)) {
                                s = reqresItem.response;
                                callback.success((T) s, MockUtils.getRetrofitResponse());
                                return;
                            }
                        } else if (reqresItem != null && reqresItem.isRequestSame(reqres)) {
                            s = reqresItem.response;
                            callback.success((T) s, MockUtils.getRetrofitResponse());
                            return;
                        }
                    }
                } else {
                    s = MockUtils.getCorsaliteError("Failure", "Notwork not available");
                }
            }
            s = MockUtils.getCorsaliteError("Failure", "No data found");
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        if (s == null || s instanceof CorsaliteError) {
            callback.failure((CorsaliteError) s);
        } else {
            callback.success((T) s, MockUtils.getRetrofitResponse());
        }
    }

    public void updateOfflineTestModel(final long date, final int status, final long examTakenTime) {
        try {
            List<OfflineTestObjectModel> offlinecontentList = fetchRecords(OfflineTestObjectModel.class);
            for (OfflineTestObjectModel savedOfflineContent : offlinecontentList) {
                if (date == savedOfflineContent.dateTime) {
                    savedOfflineContent.dateTime = examTakenTime;
                    savedOfflineContent.status = status;
                    save(savedOfflineContent);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void saveOfflineTest(final OfflineTestObjectModel model) {
        try {
            save(model);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void saveOfflineExerciseTests(List<ExerciseOfflineModel> offlineExercises) {
        List<ExerciseOfflineModel> offlineExercisesInDb = fetchRecords(ExerciseOfflineModel.class);
        for (ExerciseOfflineModel exercise : offlineExercisesInDb) {
            Long id = getOfflineexerciseId(offlineExercises, exercise);
            if(id != null) {
                exercise.setId(id);
            }
        }
        save(offlineExercises);
    }

    private Long getOfflineexerciseId(List<ExerciseOfflineModel> offlineExercises, ExerciseOfflineModel exercise) {
        try {
            for (ExerciseOfflineModel offlineExercise: offlineExercises) {
                if (offlineExercise.equals(exercise)) {
                    return offlineExercise.getId();
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }


    public void saveOfflineExerciseTest(ExerciseOfflineModel exercise) {
        try {
            if(exercise != null && exercise.getId() != null) {
                save(exercise);
            } else {
                List<ExerciseOfflineModel> offlineExercisesInDb = fetchRecords(ExerciseOfflineModel.class);
                for (ExerciseOfflineModel offlineExercise : offlineExercisesInDb) {
                    if (offlineExercise.equals(exercise)) {
                        exercise.setId(offlineExercise.getId());
                    }
                }
                save(exercise);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void deleteOfflineExercise(ExerciseOfflineModel exercise) {
        try {
            exercise.delete();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public List<ExerciseOfflineModel> getOfflineExerciseModels(String courseId) {
        List<ExerciseOfflineModel> offlineExercises = new ArrayList<>();
        try {
            List<ExerciseOfflineModel> offlineTestModels = fetchRecords(ExerciseOfflineModel.class);
            if(TextUtils.isEmpty(courseId)) {
                return offlineTestModels;
            }
            for (ExerciseOfflineModel model : offlineTestModels) {
                if (!TextUtils.isEmpty(model.courseId) && model.courseId.equals(courseId)) {
                    offlineExercises.add(model);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return offlineExercises;
    }

    public void getAllExamModels(String courseId, final String subjectId, final ApiCallback<BaseTest> callback) {
        try {
            List<OfflineTestObjectModel> responseList = fetchRecords(OfflineTestObjectModel.class);
            for (OfflineTestObjectModel test : responseList) {
                if (test != null && courseId.equals(test.baseTest.courseId)) {
                    if (test != null && test.baseTest.subjectId.equalsIgnoreCase(subjectId)) {
                        callback.success(test.baseTest, MockUtils.getRetrofitResponse());
                    }
                }
            }
        } catch (Exception e) {
            CorsaliteError error = new CorsaliteError();
            error.message = "No data found";
            callback.failure(error);
            L.error(e.getMessage(), e);
        }
    }

    public void getAllExamModels(String courseId, final MockTest mockTest, final ApiCallback<OfflineTestObjectModel> callback) {
        try {
            List<OfflineTestObjectModel> responseList = fetchRecords(OfflineTestObjectModel.class);
            for (OfflineTestObjectModel test : responseList) {
                if (test != null && courseId.equals(test.baseTest.courseId)) {
                    if (test != null && test.mockTest != null && !TextUtils.isEmpty(test.mockTest.examTemplateId)
                            && test.mockTest.examTemplateId.equalsIgnoreCase(mockTest.examTemplateId)) {
                        callback.success(test, MockUtils.getRetrofitResponse());
                    }
                }
            }
        } catch (Exception e) {
            CorsaliteError error = new CorsaliteError();
            error.message = "No data found";
            callback.failure(error);
            L.error(e.getMessage(), e);
        }
    }

    public void getAllExamModels(String courseId, final ScheduledTestsArray scheduledTest, final ApiCallback<List<ExamModel>> callback) {
        try {
            List<OfflineTestObjectModel> responseList = fetchRecords(OfflineTestObjectModel.class);
            for (OfflineTestObjectModel test : responseList) {
                if (test != null && courseId.equals(test.baseTest.courseId)) {
                    if (test != null && test.scheduledTest != null && !TextUtils.isEmpty(test.scheduledTest.testQuestionPaperId)
                            && test.scheduledTest.testQuestionPaperId.equalsIgnoreCase(scheduledTest.testQuestionPaperId)) {
                        callback.success(test.examModels, MockUtils.getRetrofitResponse());
                    }
                }
            }
        } catch (Exception e) {
            CorsaliteError error = new CorsaliteError();
            error.message = "No data found";
            callback.failure(error);
            L.error(e.getMessage(), e);
        }
    }

    public void deleteOfflineMockTest(OfflineTestObjectModel model) {
        model.delete();
    }

    public void getAllOfflineMockTests(String courseId, ApiCallback<List<OfflineTestObjectModel>> callback) {
        List<OfflineTestObjectModel> currentUserResults = new ArrayList<>();
        try {
            List<OfflineTestObjectModel> responseList = fetchRecords(OfflineTestObjectModel.class);
            for (OfflineTestObjectModel test : responseList) {
                if (test != null && courseId.equals(test.baseTest.courseId)) {
                    currentUserResults.add(test);
                }
            }
            callback.success(currentUserResults, MockUtils.getRetrofitResponse());
        } catch (Exception e) {
            CorsaliteError error = new CorsaliteError();
            error.message = "No data found";
            callback.failure(error);
            L.error(e.getMessage(), e);
        }
    }
}
