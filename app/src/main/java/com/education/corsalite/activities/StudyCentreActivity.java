package com.education.corsalite.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.GridRecyclerAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.models.responsemodels.CompletionStatus;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.StudyCenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by ayush on 25/09/15.
 */
public class StudyCentreActivity extends AbstractBaseActivity {
    private GridRecyclerAdapter mAdapter;
    private RecyclerView recyclerView;
    //    private TextView subject1;
//    private TextView subject2;
//    private TextView subject3;
    private ProgressBar progressBar;
    private StudyCenter mStudyCenter;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_study_center, null);
        linearLayout = (LinearLayout) findViewById(R.id.subjects_name_id);
        frameLayout.addView(myView);
        toolbar.setTitle(getResources().getString(R.string.study_centre));
        initUI();
        setListeners();
        getStudyCentreData();
    }

    private void initDataAdapter(HashMap<String, List<CompletionStatus>> tilesMap) {
        showList();
        mAdapter = new GridRecyclerAdapter(tilesMap, "Physics", this);
        recyclerView.setAdapter(mAdapter);
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(mAdapter);
//        subject1 = (TextView) findViewById(R.id.subject1);
//        subject2 = (TextView) findViewById(R.id.subject2);
//        subject3 = (TextView) findViewById(R.id.subject3);
        progressBar = (ProgressBar) findViewById(R.id.headerProgress);
        progressBar.setVisibility(View.VISIBLE);
        setListeners();
    }

    private void setListeners() {
//        subject1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showList();
//                if (mStudyCenter != null && mStudyCenter.tilesMap != null) {
//                    mAdapter.updateData(mStudyCenter.tilesMap, subject1.getText().toString());
//                    mAdapter.notifyDataSetChanged();
//                }
//            }
//        });
//
//        subject2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showList();
//                if (mStudyCenter != null && mStudyCenter.tilesMap != null) {
//                    mAdapter.updateData(mStudyCenter.tilesMap, subject2.getText().toString());
//                    mAdapter.notifyDataSetChanged();
//                }
//            }
//        });
//
//        subject3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                showList();
//                if (mStudyCenter != null && mStudyCenter.tilesMap != null) {
//                    mAdapter.updateData(mStudyCenter.tilesMap, subject3.getText().toString());
//                    mAdapter.notifyDataSetChanged();
//                }
//            }
//        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
    }

    private void showList() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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
                        hideRecyclerView();
                    }

                    @Override
                    public void success(StudyCenter mStudyCenter, Response response) {
                        if (mStudyCenter != null && mStudyCenter.getCompletionStatus() != null && mStudyCenter.getCompletionStatus().size() > 0) {
                            StudyCentreActivity.this.mStudyCenter = mStudyCenter;
                            setUpStudyCentreData(mStudyCenter);
                            initDataAdapter(mStudyCenter.tilesMap);
                        } else {
                            hideRecyclerView();
                        }
                    }
                });
    }

    private void hideRecyclerView() {
        recyclerView.setVisibility(View.GONE);
    }

    private void setUpStudyCentreData(StudyCenter mStudyCenter) {
        HashMap<String, List<CompletionStatus>> tilesMap = new HashMap<>();
        ArrayList<String> subjects = new ArrayList<String>();
        for (CompletionStatus completionStatus : mStudyCenter.getCompletionStatus()) {
            if (tilesMap.containsKey(completionStatus.getSubjectName())) {
                ArrayList<CompletionStatus> arrayList = (ArrayList<CompletionStatus>) tilesMap.get(completionStatus.getSubjectName());
                arrayList.add(completionStatus);
            } else {
                ArrayList<CompletionStatus> arrayList = new ArrayList<CompletionStatus>();
                arrayList.add(completionStatus);
                tilesMap.put(completionStatus.getSubjectName(), arrayList);
                subjects.add(completionStatus.getSubjectName());
            }
        }
        mStudyCenter.tilesMap = tilesMap;
        for (String subject : subjects) {
            linearLayout.addView(getTextView(subject));
        }
    }

    private TextView getTextView(String text) {
        TextView t = (TextView) findViewById(R.id.subject);
        t.setText(text);
        return t;
    }
}
