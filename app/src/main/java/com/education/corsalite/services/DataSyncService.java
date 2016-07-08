package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;

import com.education.corsalite.api.ApiManager;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.event.ContentReadingEvent;
import com.education.corsalite.models.db.SyncModel;
import com.education.corsalite.models.requestmodels.UserEventsModel;
import com.education.corsalite.models.responsemodels.BaseResponseModel;
import com.education.corsalite.models.responsemodels.TestAnswerPaper;
import com.education.corsalite.models.responsemodels.TestAnswerPaperResponse;
import com.education.corsalite.models.responsemodels.UserEventsResponse;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

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
        List<SyncModel> dataToBeRemoved = new ArrayList<>();
        for(SyncModel model : syncData) {
            if (model != null) {
               if(executeApi(model)) {
                   dataToBeRemoved.add(model);
               }
            }
        }
        syncData.removeAll(dataToBeRemoved);
    }

    private boolean executeApi(SyncModel model) {
        try {
            if(model.requestObject == null) {
                return false;
            }
            if (model.requestObject instanceof TestAnswerPaper) {
                L.info("DataSuncService : Executing SubmitAnswerPaper");
                TestAnswerPaperResponse response = null;
                response = ApiManager.getInstance(getApplicationContext()).submitTestAnswerPaper((TestAnswerPaper) model.requestObject);
                return response != null;
            } else if(model.requestObject instanceof UserEventsModel) {
                L.info("DataSuncService : Executing UserEvents");
                UserEventsResponse response = ApiManager.getInstance(getApplicationContext()).postUserEvents(new Gson().toJson(model.requestObject));
                return response != null;
            } else if(model.requestObject instanceof ContentReadingEvent) {
                L.info("DataSuncService : Executing ContentUsage");
                BaseResponseModel response = ApiManager.getInstance(getApplicationContext()).postContentUsage(new Gson().toJson(model.requestObject));
                return response != null;
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return false;
    }

}
