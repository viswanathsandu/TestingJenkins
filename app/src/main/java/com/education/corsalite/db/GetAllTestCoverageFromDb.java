package com.education.corsalite.db;

import android.os.AsyncTask;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.responsemodels.TestCoverage;
import com.education.corsalite.utils.MockUtils;

import java.util.List;

/**
 * Created by madhuri on 3/14/16.
 */
public class GetAllTestCoverageFromDb extends AsyncTask<String, Void, List<TestCoverage>> {
    private DbService dbService;
    private ApiCallback<List<TestCoverage>> callback;

    public GetAllTestCoverageFromDb(DbService dbService, ApiCallback callback) {
        this.dbService = dbService;
        this.callback = callback;
    }

    @Override
    protected List<TestCoverage> doInBackground(String... params) {
        List<TestCoverage> responseList = dbService.Get(TestCoverage.class);
        return responseList;
    }

    @Override
    protected void onPostExecute(List<TestCoverage> response) {
        super.onPostExecute(response);
        callback.success(response, MockUtils.getRetrofitResponse());
    }
}
