//package com.education.corsalite.db;
//
//import android.os.AsyncTask;
//
//import com.education.corsalite.api.ApiCallback;
//import com.education.corsalite.models.db.OfflineTestObjectModel;
//import com.education.corsalite.utils.MockUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by madhuri on 2/28/16.
// */
//public class GetOfflineTestFromDb extends AsyncTask<String, Void, List<OfflineTestObjectModel>> {
//
//    private DbService dbService;
//    private String courseId;
//    private ApiCallback<List<OfflineTestObjectModel>> callback;
//
//    public GetOfflineTestFromDb(String courseId, DbService dbService, ApiCallback callback) {
//        this.dbService = dbService;
//        this.callback = callback;
//        this.courseId = courseId;
//    }
//
//    @Override
//    protected List<OfflineTestObjectModel> doInBackground(String... params) {
//        List<OfflineTestObjectModel> responseList = dbService.Get(OfflineTestObjectModel.class);
//        try {
//            List<OfflineTestObjectModel> currentUserResults = new ArrayList<>();
//            for (OfflineTestObjectModel test : responseList) {
//                if (test != null && test.isCurrentUser() && courseId.equals(test.baseTest.courseId)) {
//                    currentUserResults.add(test);
//                }
//            }
//            return currentUserResults;
//        } catch (Exception e) {
//            return responseList;
//        }
//    }
//
//    @Override
//    protected void onPostExecute(List<OfflineTestObjectModel> response) {
//        super.onPostExecute(response);
//        callback.success(response, MockUtils.getRetrofitResponse());
//    }
//}