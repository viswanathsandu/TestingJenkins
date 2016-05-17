//package com.education.corsalite.db;
//
//import android.os.AsyncTask;
//
//import com.education.corsalite.api.ApiCallback;
//import com.education.corsalite.models.ExerciseOfflineModel;
//import com.education.corsalite.utils.MockUtils;
//
//import java.util.List;
//
///**
// * Created by madhuri on 2/28/16.
// */
//public class GetOfflineExerciseFromDb extends AsyncTask<String, Void, List<ExerciseOfflineModel>> {
//
//    private DbService dbService;
//    private ApiCallback<List<ExerciseOfflineModel>> callback;
//
//    public GetOfflineExerciseFromDb(DbService dbService, ApiCallback callback) {
//        this.dbService = dbService;
//        this.callback = callback;
//    }
//
//    @Override
//    protected List<ExerciseOfflineModel> doInBackground(String... params) {
//        List<ExerciseOfflineModel> responseList = dbService.Get(ExerciseOfflineModel.class);
//        return responseList;
//    }
//
//    @Override
//    protected void onPostExecute(List<ExerciseOfflineModel> response) {
//        super.onPostExecute(response);
//        callback.success(response, MockUtils.getRetrofitResponse());
//    }
//}