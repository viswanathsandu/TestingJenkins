package com.education.corsalite.db;

import android.content.Context;
import android.text.TextUtils;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.db.ExerciseOfflineModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.db.OfflineTestObjectModel;
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

    private List<OfflineContent> cachedOfflineContents;
    private List<ExerciseOfflineModel> cachedOfflineExercises;
    private List<OfflineTestObjectModel> cachedOfflineTestObjects;

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
        try {
            if (object != null) {
                object.reflectionJsonString = new Gson().toJson(object);
                object.setUserId();
                object.save();
                object.reflectionJsonString = ""; // clear the memory
                updateChachedData(object);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        } catch (OutOfMemoryError e) {
            L.error(e.getMessage(), e);
        }
    }

    public <T extends BaseModel> void save(List<T> objects) {
        try {
            if (objects != null) {
                for (T object : objects) {
                    save(object);
                    // TODO : this needs to be handled properly such that outofmemory issues won't occur
                    /* object.reflectionJsonString = new Gson().toJson(object);
                    updateChachedData(object); */
                }
                // TODO : this needs to be handled properly such that outofmemory issues won't occur
                // SugarRecord.saveInTx(objects);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public <T extends BaseModel> List<T> fetchRecords(Class<T> type) {
        List<T> results = new ArrayList<>();
        try {
            if (type != null) {
                List<T> allList = SugarRecord.listAll(type);
                for (T t : allList) {
                    if (t.reflectionJsonString != null) {
                        T object = new Gson().fromJson(t.reflectionJsonString, type);
                        object.setId(t.getId());
                        if (TextUtils.isEmpty(object.userId) || object.isCurrentUser()) {
                            results.add(object);
                        }
                    }
                    t.reflectionJsonString = ""; // clear memory
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return results;
    }

    public <T extends BaseModel> void delete(T object) {
        try {
            if (object != null) {
                object.delete();
                deleteFromCachedData(object);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private <T extends BaseModel> void updateChachedData(T object) {
        if (object instanceof OfflineContent) {
            if (object.getId() != null && object.getId() > 0) {
                for (int i = 0; i < cachedOfflineContents.size(); i++) {
                    if (cachedOfflineContents.get(i).getId().equals(object.getId())) {
                        cachedOfflineContents.set(i, (OfflineContent) object);
                        return;
                    }
                }
            }
            cachedOfflineContents.add((OfflineContent) object);
        } else if (object instanceof OfflineTestObjectModel) {
            if (object.getId() != null && object.getId() > 0) {
                for (int i = 0; i < getCachedOfflineTestObjectModles().size(); i++) {
                    if (cachedOfflineTestObjects.get(i).getId().equals(object.getId())) {
                        cachedOfflineTestObjects.set(i, (OfflineTestObjectModel) object);
                        return;
                    }
                }
            }
            cachedOfflineTestObjects.add((OfflineTestObjectModel) object);
        } else if (object instanceof ExerciseOfflineModel) {
            if (object.getId() != null && object.getId() > 0) {
                for (int i = 0; i < cachedOfflineExercises.size(); i++) {
                    if (cachedOfflineExercises.get(i).getId().equals(object.getId())) {
                        cachedOfflineExercises.set(i, (ExerciseOfflineModel) object);
                        return;
                    }
                }
            }
            cachedOfflineExercises.add((ExerciseOfflineModel) object);
        }
    }

    private <T extends BaseModel> void deleteFromCachedData(T object) {
        if (object instanceof OfflineContent) {
            if (object.getId() != null && object.getId() > 0) {
                for (int i = 0; i < cachedOfflineContents.size(); i++) {
                    if (cachedOfflineContents.get(i).getId().equals(object.getId())) {
                        cachedOfflineContents.remove(i);
                        return;
                    }
                }
            }
        } else if (object instanceof OfflineTestObjectModel) {
            if (object.getId() != null && object.getId() > 0) {
                for (int i = 0; i < cachedOfflineTestObjects.size(); i++) {
                    if (cachedOfflineTestObjects.get(i).getId().equals(object.getId())) {
                        cachedOfflineTestObjects.remove(i);
                        return;
                    }
                }
            }
        } else if (object instanceof ExerciseOfflineModel) {
            if (object.getId() != null && object.getId() > 0) {
                for (int i = 0; i < cachedOfflineExercises.size(); i++) {
                    if (cachedOfflineExercises.get(i).getId().equals(object.getId())) {
                        cachedOfflineExercises.remove(i);
                        return;
                    }
                }
            }
        }
    }

    public List<OfflineContent> getOfflineContents(String courseId) {
        List<OfflineContent> results = new ArrayList<>();
        try {
            List<OfflineContent> offlinecontents = getCahcedOfflineContents();
            if (TextUtils.isEmpty(courseId)) {
                return offlinecontents;
            }
            for (OfflineContent offlineContent : offlinecontents) {
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
        List<OfflineContent> offlineContentsInDb = getCahcedOfflineContents();
        for (OfflineContent content : offlineContents) {
            Long id = getOfflineContentId(offlineContentsInDb, content);
            if (id != null) {
                content.setId(id);
            }
        }
        save(offlineContents);
    }

    public void deleteOfflineContent(OfflineContent offlineContent) {
        try {
            delete(offlineContent);
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

    public <P extends AbstractBaseRequest, T> void getResponse(ReqRes<P, T> reqres, ApiCallback<T> callback) {
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
            for (OfflineTestObjectModel savedOfflineContent : getCachedOfflineTestObjectModles()) {
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

    public void deleteOfflineTestModel(final long date) {
        try {
            for (OfflineTestObjectModel savedOfflineContent : getCachedOfflineTestObjectModles()) {
                if (date == savedOfflineContent.dateTime) {
                    delete(savedOfflineContent);
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
        for (ExerciseOfflineModel exercise : getCahcedExerciseTests()) {
            Long id = getOfflineexerciseId(offlineExercises, exercise);
            if (id != null) {
                exercise.setId(id);
            }
        }
        save(offlineExercises);
    }

    private Long getOfflineexerciseId(List<ExerciseOfflineModel> offlineExercises, ExerciseOfflineModel exercise) {
        try {
            for (ExerciseOfflineModel offlineExercise : offlineExercises) {
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
            if (exercise != null && exercise.getId() != null) {
                save(exercise);
            } else {
                List<ExerciseOfflineModel> offlineExercisesInDb = getCahcedExerciseTests();
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
            delete(exercise);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public List<ExerciseOfflineModel> getOfflineExerciseModels(String courseId) {
        List<ExerciseOfflineModel> offlineExercises = new ArrayList<>();
        try {
            List<ExerciseOfflineModel> offlineTestModels = getCahcedExerciseTests();
            if (TextUtils.isEmpty(courseId)) {
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
            List<OfflineTestObjectModel> responseList = getCachedOfflineTestObjectModles();
            for (OfflineTestObjectModel test : responseList) {
                if (test != null && courseId.equals(test.baseTest.courseId)) {
                    if (test != null && test.baseTest.subjectId.equalsIgnoreCase(subjectId)) {
                        callback.success(test.baseTest, MockUtils.getRetrofitResponse());
                        return;
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

    public void getExamModel(Long dbRowId, final ApiCallback<BaseTest> callback) {
        try {
            List<OfflineTestObjectModel> responseList = getCachedOfflineTestObjectModles();
            for (OfflineTestObjectModel test : responseList) {
                if (test != null && dbRowId.equals(test.getId())) {
                    callback.success(test.baseTest, MockUtils.getRetrofitResponse());
                    return;
                }
            }
        } catch (Exception e) {
            CorsaliteError error = new CorsaliteError();
            error.message = "No data found";
            callback.failure(error);
            L.error(e.getMessage(), e);
        }
    }

    public void getMockExamModels(Long dbRowId, final ApiCallback<OfflineTestObjectModel> callback) {
        try {
            List<OfflineTestObjectModel> responseList = getCachedOfflineTestObjectModles();
            for (OfflineTestObjectModel test : responseList) {
                if (test != null && dbRowId.equals(test.getId())) {
                    if (test != null && test.mockTest != null && !TextUtils.isEmpty(test.mockTest.examTemplateId)) {
                        callback.success(test, MockUtils.getRetrofitResponse());
                        return;
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

    public void getAllExamModels(Long dbRowId, final ApiCallback<List<ExamModel>> callback) {
        try {
            List<OfflineTestObjectModel> responseList = getCachedOfflineTestObjectModles();
            for (OfflineTestObjectModel test : responseList) {
                if (test != null && dbRowId.equals(test.getId())) {
                    if (test != null && test.scheduledTest != null && !TextUtils.isEmpty(test.scheduledTest.testQuestionPaperId)) {
                        callback.success(test.examModels, MockUtils.getRetrofitResponse());
                        return;
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
        delete(model);
    }

    public void getAllOfflineMockTests(String courseId, ApiCallback<List<OfflineTestObjectModel>> callback) {
        List<OfflineTestObjectModel> currentUserResults = new ArrayList<>();
        try {
            List<OfflineTestObjectModel> responseList = getCachedOfflineTestObjectModles();
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

    // Cached data
    private List<OfflineContent> getCahcedOfflineContents() {
        if (cachedOfflineContents == null || cachedOfflineContents.isEmpty()) {
            cachedOfflineContents = fetchRecords(OfflineContent.class);
        }
        return cachedOfflineContents;
    }

    private List<ExerciseOfflineModel> getCahcedExerciseTests() {
        if (cachedOfflineExercises == null) {
            cachedOfflineExercises = fetchRecords(ExerciseOfflineModel.class);
        }
        return cachedOfflineExercises;
    }

    private List<OfflineTestObjectModel> getCachedOfflineTestObjectModles() {
        if (cachedOfflineTestObjects == null) {
            cachedOfflineTestObjects = fetchRecords(OfflineTestObjectModel.class);
        }
        if (cachedOfflineTestObjects == null) {
            cachedOfflineTestObjects = new ArrayList<>();
        }
        return cachedOfflineTestObjects;
    }
}
