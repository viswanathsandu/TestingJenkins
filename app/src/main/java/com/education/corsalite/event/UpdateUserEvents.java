package com.education.corsalite.event;

import android.app.Activity;

import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.models.requestmodels.UserEventsModel;
import com.education.corsalite.models.responsemodels.UserEventsResponse;
import com.google.gson.Gson;

import retrofit.client.Response;

/**
 * Created by Madhuri on 24-01-2016.
 */
public class UpdateUserEvents {

    public void postUserEvent(Activity activity, UserEventsModel model){
        ApiManager.getInstance(activity).postUserEvents(new Gson().toJson(model), new ApiCallback<UserEventsResponse>(activity) {
            @Override
            public void success(UserEventsResponse userEventsResponse, Response response) {
                super.success(userEventsResponse, response);

            }
        });
    }
}
