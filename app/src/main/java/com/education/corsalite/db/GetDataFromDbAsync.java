package com.education.corsalite.db;

import android.os.AsyncTask;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.utils.MockUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vissu on 11/29/15.
 */
public class GetDataFromDbAsync extends AsyncTask<String, Void, List<OfflineContent>> {

    private DbService dbService;
    private ApiCallback<List<OfflineContent>> callback;

    public GetDataFromDbAsync(DbService dbService, ApiCallback callback) {
        this.dbService = dbService;
        this.callback = callback;
    }

    @Override
    protected List<OfflineContent> doInBackground(String... params) {
        List<OfflineContent> responseList = dbService.Get(OfflineContent.class);
        try {
            List<OfflineContent> currentUserResults = new ArrayList<>();
            for (OfflineContent content : responseList) {
                if (content.isCurrentUser()) {
                    currentUserResults.add(content);
                }
            }
            return currentUserResults;
        } catch (Exception e) {
            return responseList;
        }
    }

    @Override
    protected void onPostExecute(List<OfflineContent> response) {
        super.onPostExecute(response);
        callback.success(response, MockUtils.getRetrofitResponse());
    }
}
