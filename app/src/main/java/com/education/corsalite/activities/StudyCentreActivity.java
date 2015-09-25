package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
    private TextView subject1;
    private TextView subject2;
    private TextView subject3;
    private StudyCenter mStudyCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.grid_recycler_view, null);
        frameLayout.addView(myView);
        initUI();
        toolbar.setTitle(getResources().getString(R.string.study_centre));
        getStudyCentreData();
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(mAdapter);
        subject1 = (TextView) findViewById(R.id.subject1);
        subject2 = (TextView) findViewById(R.id.subject2);
        subject3 = (TextView) findViewById(R.id.subject3);
        setListeners();
    }

    private void setListeners() {
        subject1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter = new GridRecyclerAdapter(mStudyCenter.tilesMap,subject1.getText().toString());
                recyclerView.setAdapter(mAdapter);
            }
        });

        subject2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter = new GridRecyclerAdapter(mStudyCenter.tilesMap,subject2.getText().toString());
                recyclerView.setAdapter(mAdapter);
            }
        });

        subject3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter = new GridRecyclerAdapter(mStudyCenter.tilesMap,subject3.getText().toString());
                recyclerView.setAdapter(mAdapter);
            }
        });
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
                            StudyCentreActivity.this.mStudyCenter = mStudyCenter;
                            setUpStudyCentreData(mStudyCenter);
                        } else {
//                            hideRecyclerView();
                        }
                    }
                });
    }

    private void setUpStudyCentreData(StudyCenter mStudyCenter){
        HashMap<String, List<CompletionStatus>> tilesMap = new HashMap<>();
        for(CompletionStatus completionStatus : mStudyCenter.getCompletionStatus()){
            if(tilesMap.containsKey(completionStatus.getSubjectName())){
                ArrayList<CompletionStatus> arrayList = (ArrayList<CompletionStatus>) tilesMap.get(completionStatus.getSubjectName());
                arrayList.add(completionStatus);
            }else{
                ArrayList<CompletionStatus> arrayList=new ArrayList<CompletionStatus>();
                arrayList.add(completionStatus);
                tilesMap.put(completionStatus.getSubjectName(),arrayList);
            }
        }
        mStudyCenter.tilesMap = tilesMap;
    }
}
