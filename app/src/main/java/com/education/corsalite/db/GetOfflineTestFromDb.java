package com.education.corsalite.db;

import android.os.AsyncTask;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.OfflineMockTestModel;
import com.education.corsalite.utils.MockUtils;

import java.util.List;

/**
 * Created by madhuri on 2/28/16.
 */
public class GetOfflineTestFromDb extends AsyncTask<String, Void, List<OfflineMockTestModel>> {

    private DbService dbService;
    private ApiCallback<List<OfflineMockTestModel>> callback;

    public GetOfflineTestFromDb(DbService dbService, ApiCallback callback) {
        this.dbService = dbService;
        this.callback = callback;
    }

    @Override
    protected List<OfflineMockTestModel> doInBackground(String... params) {
        List<OfflineMockTestModel> responseList = dbService.Get(OfflineMockTestModel.class);
        return responseList;
    }

    @Override
    protected void onPostExecute(List<OfflineMockTestModel> response) {
        super.onPostExecute(response);
        callback.success(response, MockUtils.getRetrofitResponse());
    }
}