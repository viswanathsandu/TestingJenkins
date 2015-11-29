package com.education.corsalite.db;

import android.os.AsyncTask;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.db.reqres.ReqRes;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.utils.MockUtils;

import java.util.List;

/**
 * Created by vissu on 11/29/15.
 */
public class GetFromDbAsync<T> extends AsyncTask<String, Void, Object> {

    private DbService dbService;
    private ReqRes<T> reqres;
    private ApiCallback<T> callback;

    public GetFromDbAsync(DbService dbService, ReqRes<T> reqres, ApiCallback callback) {
        this.dbService = dbService;
        this.reqres = reqres;
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(String... params) {
        List<? extends ReqRes> reqResList = dbService.Get(reqres.getClass());
        if(reqResList != null && !reqResList.isEmpty()) {
            for (ReqRes reqresItem: reqResList) {
                if(reqresItem.isRequestSame(reqres)) {
                    return reqresItem.response;
                }
            }
        } else {
            return MockUtils.getCorsaliteError("Failure", "Notwork not available...");
        }
        return MockUtils.getCorsaliteError("Failure", "No data found...");
    }

    @Override
    protected void onPostExecute(Object s) {
        super.onPostExecute(s);
        if(s == null || s instanceof CorsaliteError) {
            callback.failure((CorsaliteError) s);
        } else {
            callback.success((T)s, MockUtils.getRetrofitResponse());
        }

    }
}
