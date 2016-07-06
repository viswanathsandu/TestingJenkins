package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;

import com.education.corsalite.api.ApiManager;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.models.db.SyncModel;
import com.education.corsalite.models.requestmodels.UserEventsModel;
import com.education.corsalite.models.responsemodels.TestAnswerPaper;
import com.education.corsalite.models.responsemodels.TestAnswerPaperResponse;
import com.education.corsalite.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madhuri on 2/27/16.
 */
public class DataSyncService extends IntentService {

    private SugarDbManager dbManager;
    public static List<SyncModel> syncData = new ArrayList<>();

    public static void addSyncModel(SyncModel model) {
        if(model != null) {
            syncData.add(model);
        }
    }

    public DataSyncService() {
        super("DataSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        dbManager = SugarDbManager.get(getApplicationContext());
        L.info("DataSyncService : Starting");
        for(SyncModel model : syncData) {
            if (model != null) {
                executeApi(model);
            }
        }
    }

    private void executeApi(SyncModel model) {
        try {
            if(model.requestObject != null) {
                return;
            }
            if (model.requestObject instanceof TestAnswerPaper) {
                L.info("DataSuncService : Executing SubmitAnswerPaper");
                TestAnswerPaperResponse response = null;
                response = ApiManager.getInstance(getApplicationContext()).submitTestAnswerPaper((TestAnswerPaper) model.requestObject);
                if (response == null) {
                    syncData.remove(model);
                }
            } else if(model.requestObject instanceof UserEventsModel) {
                L.info("DataSuncService : Executing SubmitAnswerPaper");
                TestAnswerPaperResponse response = null;
                response = ApiManager.getInstance(getApplicationContext()).submitTestAnswerPaper((TestAnswerPaper) model.requestObject);
                if (response == null) {
                    syncData.remove(model);
                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

}
