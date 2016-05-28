package com.corsalite.tabletapp.event;

import android.app.Activity;

import com.corsalite.tabletapp.api.ApiCallback;
import com.corsalite.tabletapp.api.ApiManager;
import com.corsalite.tabletapp.cache.LoginUserCache;
import com.corsalite.tabletapp.models.requestmodels.UserEventsModel;
import com.corsalite.tabletapp.models.responsemodels.UserEventsResponse;
import com.corsalite.tabletapp.utils.SystemUtils;
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
        }else {

        }
    }

    public void postContentReading(Activity activity,ContentReadingEvent event){
        UserEventsModel model = getUserEventsModel("Content Reading",event.eventStartTime,
                event.eventEndTime, event.id,event.pageView);
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
        model.userId = LoginUserCache.getInstance().loginResponse.studentId;
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
