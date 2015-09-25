package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.adapters.GridRecyclerAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.StudyCenter;

import retrofit.client.Response;

/**
 * Created by ayush on 25/09/15.
 */
public class StudyCentreActivity extends AbstractBaseActivity {
    private GridRecyclerAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.grid_recycler_view, null);
        frameLayout.addView(myView);
        initUI();
        getStudyCentreData();
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(mAdapter);
    }

    private void getStudyCentreData() {
        // TODO : passing static data
        ApiManager.getInstance(this).getStudyCentreData("1154", //LoginUserCache.getInstance().loginResponse.studentId,
                "13",
                new ApiCallback<StudyCenter>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
//                        hideRecyclerView();
                    }

                    @Override
                    public void success(StudyCenter mStudyCenter, Response response) {
                        if (mStudyCenter != null && mStudyCenter.getCompletionStatus() != null && mStudyCenter.getCompletionStatus().size() > 0) {
                            mAdapter = new GridRecyclerAdapter(mStudyCenter.getCompletionStatus());
                            recyclerView.setAdapter(mAdapter);
                        } else {
//                            hideRecyclerView();
                        }
                    }
                });
    }
}
