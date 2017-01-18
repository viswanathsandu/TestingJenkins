package com.education.corsalite.db;

import android.content.Context;
import android.text.TextUtils;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.db.ExerciseOfflineModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.db.OfflineTestObjectModel;
import com.education.corsalite.models.db.ScheduledTestsArray;
import com.education.corsalite.models.db.SyncModel;
import com.education.corsalite.models.db.reqres.LoginReqRes;
import com.education.corsalite.models.db.reqres.ReqRes;
import com.education.corsalite.models.db.reqres.requests.AbstractBaseRequest;
import com.education.corsalite.models.responsemodels.BaseModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.DbUtils;
import com.education.corsalite.utils.Gzip;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.MockUtils;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Collection;
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
        try {
            if (object != null) {
                object.setBaseUserId(context);
                object.reflectionJsonString = Gzip.compress(Gson.get().toJson(object));
                object.save();
                object.reflectionJsonString = null; // clear the memory
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

    public <T extends BaseModel> void delete(T object) {
        try {
            if (object != null) {
                object.delete();
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public <T extends BaseModel> void delete(Collection<T> objectList) {
        try {
            if (objectList != null) {
                SugarRecord.deleteInTx(objectList);
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
                    allList = Select.from(type).where(Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId())).list();
                } else {
                    allList = SugarRecord.listAll(type);
                }
                while(allList!=null && allList.size() > 0) {
                    T t = allList.remove(0);
                    if(t.reflectionJsonString != null) {
                        T object = Gson.get().fromJson(Gzip.decompress(t.reflectionJsonString), type);
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

    public <T extends BaseModel> T getProcessedRecord(T data, Class<T> type) {
        try {
            if (data.reflectionJsonString != null) {
                T object = Gson.get().fromJson(Gzip.decompress(data.reflectionJsonString), type);
                data.reflectionJsonString = null;
                object.setId(data.getId());
                return object;
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return data;
    }

    public <T extends BaseModel> List<T> getProcessedRecords(List<T> data, Class<T> type) {
        try {
            List<T> results = new ArrayList<>();
            while(data!=null && data.size() > 0) {
                results.add(getProcessedRecord(data.remove(0), type));
            }
            return results;
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return data;
        }
    }

    public <T extends BaseModel> T fetchRecord(Class<T> type, Long id) {
        try {
            if (type != null) {
                T t = SugarRecord.findById(type, id);
                if (t.reflectionJsonString != null) {
                    T object = Gson.get().fromJson(Gzip.decompress(t.reflectionJsonString), type);
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

    public <T extends BaseModel> long getcount(Class<T> type) {
        try {
            return Select.from(type).where(Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId())).count();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return 0;
    }

    public <T extends BaseModel> T fetchFirstRecord(Class<T> type) {
        try {
            if (type != null) {
                T t = Select.from(type).where(Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId())).first();
                if (t != null && t.reflectionJsonString != null) {
                    T object = Gson.get().fromJson(Gzip.decompress(t.reflectionJsonString), type);
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

    /*
    This should be used after implementing the course id for all exams
     */
    public List<OfflineTestObjectModel> fetchOfflineTestRecords(String courseId) {
        try {
            List<OfflineTestObjectModel> allList = Select.from(OfflineTestObjectModel.class)
                    .where(Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId()),
                            Condition.prop("COURSE_ID").eq(TextUtils.isEmpty(courseId) ? null : courseId)).list();
            if(allList == null) {
                allList = new ArrayList<>();
            }
            if(courseId != null) {
                List<OfflineTestObjectModel> otherList = Select.from(OfflineTestObjectModel.class)
                        .where(Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId()),
                                Condition.prop("COURSE_ID").eq(null)).list();
                if (otherList != null && !otherList.isEmpty()) {
                    allList.addAll(otherList);
                }
            }
            List<OfflineTestObjectModel> results = new ArrayList<>();
            while(allList!=null && allList.size() > 0) {
                OfflineTestObjectModel t = allList.remove(0);
                if(t.reflectionJsonString != null) {
                    OfflineTestObjectModel object = Gson.get().fromJson(Gzip.decompress(t.reflectionJsonString), OfflineTestObjectModel.class);
                    t.reflectionJsonString = null;
                    object.setId(t.getId());
                    results.add(object);
                }
            }
            return results;
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public OfflineTestObjectModel fetchOfflineTestRecord(String testQuestionPaperID) {
        try {
            OfflineTestObjectModel t = Select.from(OfflineTestObjectModel.class)
                            .where(Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId()),
                                    Condition.prop("TEST_QUESTION_PAPER_ID").eq(testQuestionPaperID)).first();
            if (t != null && t.reflectionJsonString != null) {
                OfflineTestObjectModel object = Gson.get().fromJson(Gzip.decompress(t.reflectionJsonString), OfflineTestObjectModel.class);
                t.reflectionJsonString = null;
                object.setId(t.getId());
                return object;
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public void addSyncModel(SyncModel model) {
        if(model != null) {
            save(model);
        }
    }

    public SyncModel getFirstSyncModel() {
        try {
            return fetchFirstRecord(SyncModel.class);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return null;
        }
    }

    public List<OfflineContent> getOfflineContents(String courseId) {
        List<OfflineContent> results = new ArrayList<>();
        try {
            if(!TextUtils.isEmpty(courseId)) {
                Long id = 0l;
                List<OfflineContent> data;
                do {
                     data = Select.from(OfflineContent.class).where(
                            Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId()),
                            Condition.prop("COURSE_ID").eq(courseId),
                            Condition.prop("ID").gt(id)).orderBy("ID").limit("10").list();
                    if(data != null && !data.isEmpty()) {
                        id = data.get(data.size() - 1).getId();
                        results.addAll(getProcessedRecords(data, OfflineContent.class));
                    } else {
                        id = null;
                    }
                } while(id != null);
            } else {
                Long id = 0l;
                List<OfflineContent> data;
                do {
                    data = Select.from(OfflineContent.class).where(
                            Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId()),
                            Condition.prop("ID").gt(id)).orderBy("ID").limit("10").list();
                    if(data != null && !data.isEmpty()) {
                        id = data.get(data.size() - 1).getId();
                        results.addAll(getProcessedRecords(data, OfflineContent.class));
                    } else {
                        id = null;
                    }
                } while(id != null);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return results;
    }

    public OfflineContent getOfflineContentWithContent(String contentId) {
        try {
            OfflineContent data = Select.from(OfflineContent.class).where(
                    Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId()),
                    Condition.prop("CONTENT_ID").eq(contentId)).first();
            return getProcessedRecord(data, OfflineContent.class);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public OfflineContent getOfflineContentWithTopic(String topicId) {
        try {
            OfflineContent data = Select.from(OfflineContent.class).where(
                    Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId()),
                    Condition.prop("TOPIC_ID").eq(topicId)).first();
            return getProcessedRecord(data, OfflineContent.class);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public List<OfflineContent> getOfflineContentWithChapter(String chapterId) {
        try {
            List<OfflineContent> data = Select.from(OfflineContent.class).where(
                    Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId()),
                    Condition.prop("CHAPTER_ID").eq(chapterId)).list();
            return getProcessedRecords(data, OfflineContent.class);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public List<OfflineContent> getOfflineContentWithSubject(String subjectId) {
        try {
            List<OfflineContent> data = Select.from(OfflineContent.class).where(
                    Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId()),
                    Condition.prop("SUBJECT_ID").eq(subjectId)).list();
            return getProcessedRecords(data, OfflineContent.class);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public void deleteOfflineContent(OfflineContent offlineContent) {
        try {
            delete(offlineContent);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
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
                        reqresItem.request = reqres.request;
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
                    s = MockUtils.getCorsaliteError("Failure", "Network not available");
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

    public void deleteAuthentication(String username) {
        if(!TextUtils.isEmpty(username)) {
            List<LoginReqRes> reqResList = fetchRecords(LoginReqRes.class);
            if(reqResList != null && !reqResList.isEmpty()) {
                for(LoginReqRes reqRes : reqResList) {
                    if(reqRes.request != null && reqRes.request.loginId.equalsIgnoreCase(username)) {
                        delete(reqRes);
                        break;
                    }
                }
            }
        }
    }

    public void saveOfflineTest(final OfflineTestObjectModel model) {
        try {
            if(model != null && !TextUtils.isEmpty(model.testQuestionPaperId)
                    && TextUtils.isDigitsOnly(model.testQuestionPaperId)) {
                save(model);
                DbUtils.get(context).backupDatabase();
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void updateOfflineTestModel(String testQuestionPaperId, int status, long examTakenTime) {
        try {
            OfflineTestObjectModel result = fetchOfflineTestRecord(testQuestionPaperId);
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
            OfflineTestObjectModel result = fetchOfflineTestRecord(testQuestionPaperId);
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
            ExerciseOfflineModel model = Select.from(ExerciseOfflineModel.class).where(
                                Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId()),
                                Condition.prop("TOPIC_ID").eq(exercise.topicId),
                                Condition.prop("COURSE_ID").eq(exercise.courseId)).first();
            if(model != null) {
                return model.getId();
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return null;
    }

    public void saveOfflineExerciseTest(ExerciseOfflineModel exercise) {
        try {
            Long id = getOfflineExerciseId(exercise);
            if(id != null && id > 0) {
                exercise.setId(id);
            }
            save(exercise);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public List<ExerciseOfflineModel> getOfflineExerciseModels(String courseId) {
        List<ExerciseOfflineModel> results = new ArrayList<>();
        try {
            List<ExerciseOfflineModel> allList = null;
            if(!TextUtils.isEmpty(courseId)) {
                allList = Select.from(ExerciseOfflineModel.class).where(
                                Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId()),
                                Condition.prop("COURSE_ID").eq(courseId)).list();
            } else {
                allList = Select.from(ExerciseOfflineModel.class).where(
                        Condition.prop("BASE_USER_ID").eq(AppPref.get(context).getUserId())).list();
            }
            while (allList != null && allList.size() > 0) {
                ExerciseOfflineModel t = allList.remove(0);
                if (t.reflectionJsonString != null) {
                    ExerciseOfflineModel object = Gson.get().fromJson(Gzip.decompress(t.reflectionJsonString), ExerciseOfflineModel.class);
                    t.reflectionJsonString = null;
                    object.setId(t.getId());
                    results.add(object);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return results;
    }

    public void deleteExpiredScheduleTests(List<ScheduledTestsArray> testsList) {
        List<OfflineTestObjectModel> offlineTests = fetchRecords(OfflineTestObjectModel.class);
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

}
