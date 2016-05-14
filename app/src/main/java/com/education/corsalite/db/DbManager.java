package com.education.corsalite.db;

import android.content.Context;

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

    /**
     * User Profile Db stuff
     */
//    public <T> void saveReqRes(final ReqRes<T> reqres) {
//        if (reqres == null) {
//            return;
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                synchronized (this) {
//                    List<? extends ReqRes> reqResList = dbService.Get(reqres.getClass());
//                    if (reqResList != null && !reqResList.isEmpty()) {
//                        for (ReqRes reqresItem : reqResList) {
//                            if (reqresItem.isRequestSame(reqres)) {
//                                reqresItem.setUserId();
//                                reqresItem.response = reqres.response;
//                                dbService.Save(reqresItem);
//                                return;
//                            }
//                        }
//                    }
//                    reqres.setUserId();
//                    dbService.Save(reqres);
//                }
//            }
//        }).start();
//    }
//
//    public <T> void getResponse(ReqRes<T> reqres, ApiCallback<T> callback) {
//        new GetFromDbAsync<T>(dbService, reqres, callback).execute();
//    }

//    @Deprecated
//    public void updateOfflineTestModel(final long date, final int status, final long examTakenTime) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                synchronized (this) {
//                    List<OfflineTestModel> offlinecontentList = dbService.Get(OfflineTestModel.class);
//                    for (OfflineTestModel savedOfflineContent : offlinecontentList) {
//                        if (date == savedOfflineContent.dateTime) {
//                            savedOfflineContent.dateTime = examTakenTime;
//                            savedOfflineContent.status = status;
//                            dbService.Update(OfflineTestModel.class, savedOfflineContent);
//                        }
//                    }
//                }
//            }
//        }).start();
//    }

//    @Deprecated
//    public void saveOfflineTest(final OfflineTestModel model) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                synchronized (this) {
//                    model.setUserId();
//                    dbService.Save(model);
//                }
//            }
//        }).start();
//    }

//    @Deprecated
//    public void saveOfflineExerciseTest(final ExerciseOfflineModel exercise) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                synchronized (this) {
//                    List<ExerciseOfflineModel> models = dbService.Get(ExerciseOfflineModel.class);
//                    for (ExerciseOfflineModel model : models) {
//                        if (model.equals(exercise)) {
//                            model.setUserId();
//                            model.questions = exercise.questions;
//                            dbService.Save(model);
//                            return;
//                        }
//                    }
//                    exercise.setUserId();
//                    dbService.Save(exercise);
//                }
//            }
//        }).start();
//    }

//    @Deprecated
//    public void getOfflineExerciseModels(final String courseId, final ApiCallback<List<ExerciseOfflineModel>> callback) {
//        new GetOfflineExerciseFromDb(dbService, new ApiCallback<List<ExerciseOfflineModel>>(context) {
//            public void success(List<ExerciseOfflineModel> offlineTestModels, Response response) {
//                super.success(offlineTestModels, response);
//                List<ExerciseOfflineModel> offlineExercises = new ArrayList<ExerciseOfflineModel>();
//                for (ExerciseOfflineModel model : offlineTestModels) {
//                    try {
//                        if (model.courseId.equals(courseId) && model.isCurrentUser()) {
//                            offlineExercises.add(model);
//                        }
//                    } catch (Exception e) {
//                        CorsaliteError error = new CorsaliteError();
//                        error.message = "No data found";
//                        callback.failure(error);
//                        L.error(e.getMessage(), e);
//                    }
//                }
//                callback.success(offlineExercises, response);
//            }
//        }).execute();
//    }

//    @Deprecated
//    public void getAllExamModels(String courseId, final String subjectId, final ApiCallback<BaseTest> callback) {
//        new GetOfflineTestFromDb(courseId, dbService, new ApiCallback<List<OfflineTestModel>>(context) {
//            public void success(List<OfflineTestModel> offlineTestModels, Response response) {
//                super.success(offlineTestModels, response);
//                for (OfflineTestModel model : offlineTestModels) {
//                    try {
//                        if (model != null && model.baseTest.subjectId.equalsIgnoreCase(subjectId) && model.isCurrentUser()) {
//                            callback.success(model.baseTest, response);
//                        }
//                    } catch (Exception e) {
//                        CorsaliteError error = new CorsaliteError();
//                        error.message = "No data found";
//                        callback.failure(error);
//                        L.error(e.getMessage(), e);
//                    }
//                }
//            }
//        }).execute();
//    }

//    @Deprecated
//    public void deleteOfflineMockTest(OfflineTestModel model) {
//        dbService.Delete(OfflineTestModel.class, model);
//    }
//
//    @Deprecated
//    public void getAllOfflineMockTests(String courseId, ApiCallback<List<OfflineTestModel>> callback) {
//        new GetOfflineTestFromDb(courseId, dbService, callback).execute();
//    }

//    @Deprecated
//    public void getAllExamModels(String courseId, final MockTest mockTest, final ApiCallback<OfflineTestModel> callback) {
//        new GetOfflineTestFromDb(courseId, dbService, new ApiCallback<List<OfflineTestModel>>(context) {
//            public void success(List<OfflineTestModel> offlineTestModels, Response response) {
//                super.success(offlineTestModels, response);
//                for (OfflineTestModel model : offlineTestModels) {
//                    if (model != null && model.mockTest != null && !TextUtils.isEmpty(model.mockTest.examTemplateId)
//                            && model.mockTest.examTemplateId.equalsIgnoreCase(mockTest.examTemplateId)
//                            && model.isCurrentUser()) {
//                        callback.success(model, response);
//                    }
//                }
//            }
//        }).execute();
//    }

//    @Deprecated
//    public void getAllExamModels(String courseId, final ScheduledTestList.ScheduledTestsArray scheduledTest, final ApiCallback<List<ExamModel>> callback) {
//        new GetOfflineTestFromDb(courseId, dbService, new ApiCallback<List<OfflineTestModel>>(context) {
//            public void success(List<OfflineTestModel> offlineTestModels, Response response) {
//                super.success(offlineTestModels, response);
//                for (OfflineTestModel model : offlineTestModels) {
//                    if (model != null && model.scheduledTest != null && !TextUtils.isEmpty(model.scheduledTest.testQuestionPaperId)
//                            && model.scheduledTest.testQuestionPaperId.equalsIgnoreCase(scheduledTest.testQuestionPaperId)
//                            && model.isCurrentUser()) {
//                        callback.success(model.examModels, response);
//                    }
//                }
//            }
//        }).execute();
//    }
}
