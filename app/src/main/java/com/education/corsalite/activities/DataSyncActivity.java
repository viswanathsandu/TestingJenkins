package com.education.corsalite.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.db.SugarDbManager;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.db.SyncModel;
import com.education.corsalite.models.responsemodels.BaseResponseModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.TestAnswerPaperResponse;
import com.education.corsalite.models.responsemodels.UserEventsResponse;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.client.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DataSyncActivity extends AbstractBaseActivity {

    @Bind(R.id.progress_count_txt) TextView progressTxt;
    private SyncModel currentEvent;
    private long totalEvents = 0;
    private long completedEvents = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_sync);
        ButterKnife.bind(this);
        Glide.with(this)
                .load(R.drawable.data_sync_anim)
                .asGif()
                .into((ImageView) findViewById(R.id.sync_anim_img));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!SystemUtils.isNetworkConnected(this)) {
            showToast("Network disconnected. Switching to offline mode.");
            finish();
        } else {
            dbManager = SugarDbManager.get(getApplicationContext());
            totalEvents = dbManager.getcount(SyncModel.class);
            if(totalEvents > 0) {
                syncEvents();
            } else {
                finish();
            }
        }
    }

    @OnClick(R.id.sync_later_btn)
    public void onSyncLater() {
        AppPref.get(getApplicationContext()).save("data_sync_later", String.valueOf(new Date().getTime()));
        showToast("Sync has been stopped");
        finish();
    }

    private void updateProgress(long total, long completed) {
        long percentage = completed * 100 / total;
        progressTxt.setText(percentage + "%");
        if(total == completed) {
            showToast("All offline events have been synced successfully");
            finish();
        }
    }

    private void syncEvents() {
        if(isShown()) {
            completedEvents = totalEvents - dbManager.getcount(SyncModel.class);
            updateProgress(totalEvents, completedEvents);
            currentEvent = dbManager.getFirstSyncModel();
            if (currentEvent != null) {
                executeApi(currentEvent);
            }
        }
    }

    private void success() {
        dbManager.delete(currentEvent);
        syncEvents();
    }

    private void failure() {
        dbManager.delete(currentEvent);
        currentEvent.setId(null);
        dbManager.addSyncModel(currentEvent);
        syncEvents();
    }


    private void executeApi(SyncModel model) {
        try {
            if(model == null) {
                return;
            }
            if (model.getTestAnswerPaper() != null) {
                L.info("DataSyncService : Executing SubmitAnswerPaper");
                ApiManager.getInstance(getApplicationContext()).submitTestAnswerPaper(model.getTestAnswerPaper(),
                        new ApiCallback<TestAnswerPaperResponse>(this) {
                            @Override
                            public void failure(CorsaliteError error) {
                                super.failure(error);
                                DataSyncActivity.this.failure();
                            }

                            @Override
                            public void success(TestAnswerPaperResponse baseResponseModel, Response response) {
                                super.success(baseResponseModel, response);
                                DataSyncActivity.this.success();
                            }
                        }
                );
            } else if(model.getUserEventsModel() != null) {
                L.info("DataSyncService : Executing UserEvents");
                ApiManager.getInstance(getApplicationContext()).postUserEvents(Gson.get().toJson(model.getUserEventsModel()),
                        new ApiCallback<UserEventsResponse>(this) {
                            @Override
                            public void failure(CorsaliteError error) {
                                super.failure(error);
                                DataSyncActivity.this.failure();
                            }

                            @Override
                            public void success(UserEventsResponse userEventsResponse, Response response) {
                                super.success(userEventsResponse, response);
                                DataSyncActivity.this.success();
                            }
                        });
            } else if(model.getContentReadingEvent() != null) {
                L.info("DataSyncService : Executing ContentUsage");
                ApiManager.getInstance(getApplicationContext()).postContentUsage(Gson.get().toJson(model.getContentReadingEvent()),
                        new ApiCallback<BaseResponseModel>(this) {
                            @Override
                            public void success(BaseResponseModel baseResponseModel, Response response) {
                                super.success(baseResponseModel, response);
                                DataSyncActivity.this.success();
                            }

                            @Override
                            public void failure(CorsaliteError error) {
                                super.failure(error);
                                DataSyncActivity.this.failure();
                            }
                        });
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    @Override
    public void onBackPressed() {
        // Do not close activity
    }
}
