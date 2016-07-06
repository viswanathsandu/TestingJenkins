package com.education.corsalite.event;

import android.app.Activity;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.db.SyncModel;
import com.education.corsalite.models.requestmodels.UserEventsModel;
import com.education.corsalite.models.responsemodels.BaseResponseModel;
import com.education.corsalite.models.responsemodels.UserEventsResponse;
import com.education.corsalite.services.DataSyncService;
import com.education.corsalite.utils.SystemUtils;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit.client.Response;

/**
 * Created by Madhuri on 24-01-2016.
 */
public class UpdateUserEvents {

    public void postUserEvent(Activity activity, UserEventsModel model){
        if(SystemUtils.isNetworkConnected(activity)) {
            ApiManager.getInstance(activity).postUserEvents(new Gson().toJson(model), new ApiCallback<UserEventsResponse>(activity) {
                @Override
                public void success(UserEventsResponse userEventsResponse, Response response) {
                    super.success(userEventsResponse, response);
                }
            });
        } else {
            SyncModel syncModel = new SyncModel();
            syncModel.requestObject = model;
            DataSyncService.addSyncModel(syncModel);
        }
    }

    public void postContentReading(Activity activity, ContentReadingEvent event){
        if(SystemUtils.isNetworkConnected(activity)) {
            ApiManager.getInstance(activity).postContentUsage(new Gson().toJson(event), new ApiCallback<BaseResponseModel>(activity) {
                @Override
                public void success(BaseResponseModel baseResponse, Response response) {
                    super.success(baseResponse, response);
                }
            });
        } else {
            SyncModel syncModel = new SyncModel();
            syncModel.requestObject = event;
            DataSyncService.addSyncModel(syncModel);
        }
        UserEventsModel model = getUserEventsModel("Content Reading",event.eventStartTime,
                event.eventEndTime, event.idContent, "");
        postUserEvent(activity, model);
    }

    public void postExerciseAns(Activity activity, ExerciseAnsEvent event){
        UserEventsModel model = getUserEventsModel("Exercise Answering",event.eventStartTime,
                event.eventEndTime, event.id,event.pageView);
        postUserEvent(activity,model);
    }

    public void postTakingTest(Activity activity,TakingTestEvent event){
        UserEventsModel model = getUserEventsModel("Taking Test",event.eventStartTime,
                event.eventEndTime, event.id,event.pageView);
        postUserEvent(activity,model);
    }

    public void postForumPosting(Activity activity,ForumPostingEvent event){
        UserEventsModel model = getUserEventsModel("Forum Posting",event.eventStartTime,
                event.eventEndTime, event.id,event.pageView);
        postUserEvent(activity,model);
    }

    private UserEventsModel getUserEventsModel(String eventName,String eventStartTime,
                                               String eventEndTime, String eventSourceId, String pageView){
        UserEventsModel model = new UserEventsModel();
        model.userId = LoginUserCache.getInstance().getStudentId();
        model.eventEndTime = eventEndTime;
        model.eventStartTime =eventStartTime;
        model.eventName = eventName;
        model.eventSourceId = eventSourceId;
        model.pageView = pageView;

        return model;
    }

    private String getDate(long millis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-mm-dd hh:mm:ss", Locale.getDefault());
        Date date = new Date(millis);
        return dateFormat.format(date);
    }

}
