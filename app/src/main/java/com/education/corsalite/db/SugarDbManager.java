package com.education.corsalite.db;

import android.content.Context;
import android.text.TextUtils;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.ExerciseOfflineModel;
import com.education.corsalite.models.MockTest;
import com.education.corsalite.models.OfflineTestModel;
import com.education.corsalite.models.ScheduledTestList;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.db.reqres.LoginReqRes;
import com.education.corsalite.models.db.reqres.ReqRes;
import com.education.corsalite.models.db.reqres.requests.AbstractBaseRequest;
import com.education.corsalite.models.examengine.BaseTest;
import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ExamModel;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.MockUtils;
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
                    T object = new Gson().fromJson(t.reflectionJsonString, type);
                    object.setId(t.getId());
                    results.add(object);

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

//    public void saveOfflineContent(OfflineContent content) {
//        try {
//            List<OfflineContent> offlineContents = fetchRecords(OfflineContent.class);
//            for (OfflineContent offlineContent : offlineContents) {
//                if (offlineContent.equals(content)) {
//                    offlineContent.delete();
//                }
//            }
//            save(content);
//        } catch (Exception e) {
//            L.error(e.getMessage(), e);
//        }
//    }

    private Long getOfflineContentId(OfflineContent content) {
        try {
            List<OfflineContent> offlineContents = fetchRecords(OfflineContent.class);
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
        for (OfflineContent content : offlineContents) {
            Long id = getOfflineContentId(content);
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
                        reqresItem.setUserId();
                        reqresItem.response = reqres.response;
                        save(reqresItem);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        reqres.setUserId();
        save(reqres);
    }

    public <P extends AbstractBaseRequest, T> void getResponse(ReqRes<P,T> reqres, ApiCallback<T> callback) {
        Object s = null;
        try {
            if (reqres != null) {
                List<? extends ReqRes> reqResList = fetchRecords(reqres.getClass());
                if (reqResList != null && !reqResList.isEmpty()) {
                    for (ReqRes reqresItem : reqResList) {
                        if (reqresItem.isCurrentUser() || reqresItem instanceof LoginReqRes) {
                            if (reqresItem != null && reqresItem.isRequestSame(reqres)) {
                                s = reqresItem.response;
                                callback.success((T) s, MockUtils.getRetrofitResponse());
                                return;
                            }
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
            List<OfflineTestModel> offlinecontentList = fetchRecords(OfflineTestModel.class);
            for (OfflineTestModel savedOfflineContent : offlinecontentList) {
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

    public void saveOfflineTest(final OfflineTestModel model) {
        try {
            model.setUserId();
            save(model);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void saveOfflineExerciseTest(final ExerciseOfflineModel exercise) {
        try {
            List<ExerciseOfflineModel> models = fetchRecords(ExerciseOfflineModel.class);
            for (ExerciseOfflineModel model : models) {
                if (model.equals(exercise)) {
                    model.setUserId();
                    model.questions = exercise.questions;
                    save(exercise);
                    return;
                }
            }
            exercise.setUserId();
            save(exercise);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public List<ExerciseOfflineModel> getOfflineExerciseModels(String courseId) {
        List<ExerciseOfflineModel> offlineExercises = new ArrayList<>();
        try {
            List<ExerciseOfflineModel> offlineTestModels = fetchRecords(ExerciseOfflineModel.class);
            for (ExerciseOfflineModel model : offlineTestModels) {
                if (model.courseId.equals(courseId) && model.isCurrentUser()) {
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
            List<OfflineTestModel> responseList = fetchRecords(OfflineTestModel.class);
            for (OfflineTestModel test : responseList) {
                if (test != null && test.isCurrentUser() && courseId.equals(test.baseTest.courseId)) {
                    if (test != null && test.baseTest.subjectId.equalsIgnoreCase(subjectId) && test.isCurrentUser()) {
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

    public void getAllExamModels(String courseId, final MockTest mockTest, final ApiCallback<OfflineTestModel> callback) {
        try {
            List<OfflineTestModel> responseList = fetchRecords(OfflineTestModel.class);
            for (OfflineTestModel test : responseList) {
                if (test != null && test.isCurrentUser() && courseId.equals(test.baseTest.courseId)) {
                    if (test != null && test.mockTest != null && !TextUtils.isEmpty(test.mockTest.examTemplateId)
                            && test.mockTest.examTemplateId.equalsIgnoreCase(mockTest.examTemplateId)
                            && test.isCurrentUser()) {
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

    public void getAllExamModels(String courseId, final ScheduledTestList.ScheduledTestsArray scheduledTest, final ApiCallback<List<ExamModel>> callback) {
        try {
            List<OfflineTestModel> responseList = fetchRecords(OfflineTestModel.class);
            for (OfflineTestModel test : responseList) {
                if (test != null && test.isCurrentUser() && courseId.equals(test.baseTest.courseId)) {
                    if (test != null && test.scheduledTest != null && !TextUtils.isEmpty(test.scheduledTest.testQuestionPaperId)
                            && test.scheduledTest.testQuestionPaperId.equalsIgnoreCase(scheduledTest.testQuestionPaperId)
                            && test.isCurrentUser()) {
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

    public void deleteOfflineMockTest(OfflineTestModel model) {
        model.delete();
    }

    public void getAllOfflineMockTests(String courseId, ApiCallback<List<OfflineTestModel>> callback) {
        List<OfflineTestModel> currentUserResults = new ArrayList<>();
        try {
            List<OfflineTestModel> responseList = fetchRecords(OfflineTestModel.class);
            for (OfflineTestModel test : responseList) {
                if (test != null && test.isCurrentUser() && courseId.equals(test.baseTest.courseId)) {
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
