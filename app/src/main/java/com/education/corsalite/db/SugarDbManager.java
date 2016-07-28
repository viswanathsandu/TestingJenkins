package com.education.corsalite.db;

import android.content.Context;
import android.text.TextUtils;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.db.ExerciseOfflineModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.db.OfflineTestObjectModel;
import com.education.corsalite.models.db.ScheduledTestsArray;
import com.education.corsalite.models.db.reqres.LoginReqRes;
import com.education.corsalite.models.db.reqres.ReqRes;
import com.education.corsalite.models.db.reqres.requests.AbstractBaseRequest;
import com.education.corsalite.models.examengine.BaseTest;
import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.Gzip;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.MockUtils;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Collection;
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
                object.setUserId(context);
                object.reflectionJsonString = Gzip.compressBytes(Gson.get().toJson(object));
                object.save();
                object.reflectionJsonString = null; // clear the memory
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
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public <T extends BaseModel> List<T> fetchRecords(Class<T> type) {
        List<T> results = new ArrayList<>();
        try {
            if (type != null) {
                List<T> allList;
                if(!type.equals(LoginReqRes.class)) {
                    allList = SugarRecord.find(type, "USER_ID=?", AppPref.get(context).getUserId());
                } else {
                    allList = SugarRecord.listAll(type);
                }
                while(allList!=null && allList.size() > 0) {
                    T t = allList.remove(0);
                    if(t.reflectionJsonString != null) {
                        T object = Gson.get().fromJson(Gzip.decompressBytes(t.reflectionJsonString), type);
                        t.reflectionJsonString = null;
                        object.setId(t.getId());
                        results.add(object);
                    }
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return results;
    }

    public <T extends BaseModel> T fetchRecord(Class<T> type, Long id) {
        try {
            if (type != null) {
                T t = SugarRecord.findById(type, id);
                if (t.reflectionJsonString != null) {
                    T object = Gson.get().fromJson(Gzip.decompressBytes(t.reflectionJsonString), type);
                    t.reflectionJsonString = null;
                    object.setId(t.getId());
                    return object;
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
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

    public <T extends BaseModel> void delete(Collection<T> objectList) {
        try {
            if (objectList != null) {
                SugarRecord.deleteInTx(objectList);
                for(T obj : objectList) {
                    deleteFromCachedData(obj);
                }
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

    public void saveOfflineTest(final OfflineTestObjectModel model) {
        try {
            if(model != null && !TextUtils.isEmpty(model.testQuestionPaperId)
                    && TextUtils.isDigitsOnly(model.testQuestionPaperId)) {
                model.setId(Long.valueOf(model.testQuestionPaperId));
                save(model);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void updateOfflineTestModel(final String testQuestionPaperId, final int status, final long examTakenTime) {
        try {
            OfflineTestObjectModel result = fetchRecord(OfflineTestObjectModel.class, Long.valueOf(testQuestionPaperId));
            if (result != null) {
                result.dateTime = examTakenTime;
                result.status = status;
                save(result);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void deleteOfflineTestModel(final String testQuestionPaperId) {
        try {
            OfflineTestObjectModel result = fetchRecord(OfflineTestObjectModel.class, Long.valueOf(testQuestionPaperId));
            if(result != null) {
                delete(result);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void saveOfflineExerciseTests(List<ExerciseOfflineModel> offlineExercises) {
        for(ExerciseOfflineModel exercise : offlineExercises) {
            Long id = getOfflineExerciseId(exercise);
            if (id != null) {
                exercise.setId(id);
            }
        }
        save(offlineExercises);
    }

    private Long getOfflineExerciseId(ExerciseOfflineModel exercise) {
        try {
            for (ExerciseOfflineModel offlineExercise : getCahcedExerciseTests()) {
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
            List<ExerciseOfflineModel> offlineExercisesInDb = getCahcedExerciseTests();
            for (ExerciseOfflineModel offlineExercise : offlineExercisesInDb) {
                if (offlineExercise.equals(exercise)) {
                    exercise.setId(offlineExercise.getId());
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        save(exercise);
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
                    if (test.baseTest.subjectId.equalsIgnoreCase(subjectId)) {
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

    public void getAllExamModels(Long dbRowId, final ApiCallback<OfflineTestObjectModel> callback) {
        try {
            List<OfflineTestObjectModel> responseList = getCachedOfflineTestObjectModles();
            for (OfflineTestObjectModel test : responseList) {
                if (test != null && dbRowId.equals(test.getId())) {
                    if (test != null && test.scheduledTest != null && !TextUtils.isEmpty(test.scheduledTest.testQuestionPaperId)) {
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

    public void deleteExpiredScheduleTests(List<ScheduledTestsArray> testsList) {
        List<OfflineTestObjectModel> offlineTests = getCachedOfflineTestObjectModles();
        List<OfflineTestObjectModel> testsToBeDeleted = new ArrayList<>();
        for(OfflineTestObjectModel offlineTest : offlineTests) {
            if(offlineTest.scheduledTest != null) {
                for (ScheduledTestsArray test : testsList) {
                    if (offlineTest.testQuestionPaperId.equalsIgnoreCase(test.testQuestionPaperId)) {
                        break;
                    } else if(offlineTests.indexOf(offlineTest) == offlineTests.size() - 1) {
                        testsToBeDeleted.add(offlineTest);
                    }
                }
            }
        }
        if(!testsToBeDeleted.isEmpty()) {
            delete(testsToBeDeleted);
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
        return cachedOfflineTestObjects != null ? cachedOfflineTestObjects : new ArrayList<OfflineTestObjectModel>();
    }
}
