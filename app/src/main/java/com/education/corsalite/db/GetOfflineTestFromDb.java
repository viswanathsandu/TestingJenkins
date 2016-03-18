package com.education.corsalite.db;

import android.os.AsyncTask;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.OfflineTestModel;
import com.education.corsalite.utils.MockUtils;

import java.util.List;

/**
 * Created by madhuri on 2/28/16.
 */
public class GetOfflineTestFromDb extends AsyncTask<String, Void, List<OfflineTestModel>> {

    private DbService dbService;
    private ApiCallback<List<OfflineTestModel>> callback;

    public GetOfflineTestFromDb(DbService dbService, ApiCallback callback) {
        this.dbService = dbService;
        this.callback = callback;
    }

    @Override
    protected List<OfflineTestModel> doInBackground(String... params) {
        List<OfflineTestModel> responseList = dbService.Get(OfflineTestModel.class);
        return responseList;
    }

    @Override
    protected void onPostExecute(List<OfflineTestModel> response) {
        super.onPostExecute(response);
        callback.success(response, MockUtils.getRetrofitResponse());
    }
}