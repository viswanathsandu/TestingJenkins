package com.education.corsalite.services;

import android.app.IntentService;
import android.content.Intent;

import com.education.corsalite.api.ApiManager;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.db.SyncModel;
import com.education.corsalite.models.responsemodels.BaseResponseModel;
import com.education.corsalite.models.responsemodels.TestAnswerPaperResponse;
import com.education.corsalite.models.responsemodels.UserEventsResponse;
import com.education.corsalite.utils.L;

/**
 * Created by madhuri on 2/27/16.
 */
public class DataSyncService extends IntentService {

    private SugarDbManager dbManager;

    public DataSyncService() {
        super("DataSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        L.info("DataSyncService : Starting");
        dbManager = SugarDbManager.get(getApplicationContext());
        SyncModel object = null;
        while((object = dbManager.getFirstSyncModel()) != null) {
            if (object != null) {
                dbManager.delete(object);
                if(!executeApi(object)) {
                    object.setId(null);
                    dbManager.addSyncModel(object);
                }
            }
        }
    }

    private boolean executeApi(SyncModel model) {
        try {
            if(model == null) {
                return false;
            }
            if (model.getTestAnswerPaper() != null) {
                L.info("DataSuncService : Executing SubmitAnswerPaper");
                TestAnswerPaperResponse response = null;
                response = ApiManager.getInstance(getApplicationContext()).submitTestAnswerPaper(model.getTestAnswerPaper());
                return response != null;
            } else if(model.getUserEventsModel() != null) {
                L.info("DataSuncService : Executing UserEvents");
                UserEventsResponse response = ApiManager.getInstance(getApplicationContext()).postUserEvents(Gson.get().toJson(model.getUserEventsModel()));
                return response != null;
            } else if(model.getContentReadingEvent() != null) {
                L.info("DataSuncService : Executing ContentUsage");
                BaseResponseModel response = ApiManager.getInstance(getApplicationContext()).postContentUsage(Gson.get().toJson(model.getContentReadingEvent()));
                return response != null;
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return false;
    }

}
