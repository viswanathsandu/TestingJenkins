package com.education.corsalite.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.GridRecyclerAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.responsemodels.CompletionStatus;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.StudyCenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit.client.Response;

/**
 * Created by ayush on 25/09/15.
 */
public class StudyCentreActivity extends AbstractBaseActivity {

    private GridRecyclerAdapter mAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private StudyCenter mStudyCenter;
    private LinearLayout linearLayout;
    private ArrayList<String> subjects;
    private View redView;
    private View blueView;
    private View yellowView;
    private View greenView;
    private LinearLayout allColorLayout;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_study_center, null);
        linearLayout = (LinearLayout) myView.findViewById(R.id.subjects_name_id);
        frameLayout.addView(myView);
        setUpViews(myView);
        setToolbarTitle(getResources().getString(R.string.study_centre));
        initUI();
        getStudyCentreData();
    }

    private void setUpViews(RelativeLayout myView) {
        redView = myView.findViewById(R.id.redView);
        blueView = myView.findViewById(R.id.blueView);
        yellowView = myView.findViewById(R.id.yellowView);
        greenView = myView.findViewById(R.id.greenView);
        allColorLayout = (LinearLayout) myView.findViewById(R.id.all_colors);
        setUpListener();
    }

    private void setUpListener() {
        redView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(getUpdatedTilesMap(0), key);
                mAdapter.notifyDataSetChanged();
            }
        });
        blueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(getUpdatedTilesMap(1), key);
                mAdapter.notifyDataSetChanged();
            }
        });
        yellowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(getUpdatedTilesMap(2), key);
                mAdapter.notifyDataSetChanged();
            }
        });
        greenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(getUpdatedTilesMap(3), key);
                mAdapter.notifyDataSetChanged();
            }
        });

        allColorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(mStudyCenter.tilesMap, key);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private HashMap<String, List<CompletionStatus>> getUpdatedTilesMap(int color) {
        HashMap<String, List<CompletionStatus>> map = new HashMap<>();
        ArrayList<CompletionStatus> completionStatuses = new ArrayList<>();
        for (CompletionStatus completionStatus : mStudyCenter.tilesMap.get(key)) {
            if (completionStatus.statusColor == color) {
                completionStatuses.add(completionStatus);
            }
        }
        map.put(key, completionStatuses);
        return map;
    }

    private void initDataAdapter(HashMap<String, List<CompletionStatus>> tilesMap, String subject) {
        showList();
        key = subject;
        mAdapter = new GridRecyclerAdapter(tilesMap, subject, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_study_center, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_content_reading :
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        recyclerView.setHasFixedSize(true);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        recyclerView.setAdapter(mAdapter);
        progressBar = (ProgressBar) findViewById(R.id.headerProgress);
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
    }

    private void showList() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void getStudyCentreData() {
        // TODO : passing static data
        ApiManager.getInstance(this).getStudyCentreData( LoginUserCache.getInstance().loginResponse.studentId,
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
                        if (mStudyCenter != null && mStudyCenter.getCompletionStatus() != null &&
                                mStudyCenter.getCompletionStatus().size() > 0) {
                            StudyCentreActivity.this.mStudyCenter = mStudyCenter;
                            setUpStudyCentreData(mStudyCenter);
                            initDataAdapter(mStudyCenter.tilesMap, subjects.get(0));
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
        subjects = new ArrayList<String>();
        for (CompletionStatus completionStatus : mStudyCenter.getCompletionStatus()) {
            if (tilesMap.containsKey(completionStatus.getSubjectName())) {
                ArrayList<CompletionStatus> arrayList = (ArrayList<CompletionStatus>) tilesMap.get(completionStatus.getSubjectName());
                completionStatus.statusColor = getRandomNumber();
                arrayList.add(completionStatus);
            } else {
                ArrayList<CompletionStatus> arrayList = new ArrayList<CompletionStatus>();
                completionStatus.statusColor = getRandomNumber();
                arrayList.add(completionStatus);
                tilesMap.put(completionStatus.getSubjectName(), arrayList);
                subjects.add(completionStatus.getSubjectName());
            }
        }
        mStudyCenter.tilesMap = tilesMap;
        for (String subject : subjects) {
            TextView tv = getTextView(subject);
            linearLayout.addView(tv);
        }
    }

    private int getRandomNumber() {
        Random r = new Random();
        int Low = 1;
        int High = 5;
        int color = r.nextInt(High - Low) + Low;
        return color;
    }

    private TextView getTextView(String text) {
        View v = getView();
        TextView tv = (TextView) v.findViewById(R.id.subject);
        tv.setText(text);
        setListener(tv, text);
        return tv;
    }

    private void setListener(final TextView textView, final String text) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList();
                if (mStudyCenter != null && mStudyCenter.tilesMap != null) {
                    key = text;
                    mAdapter.updateData(mStudyCenter.tilesMap, text);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private View getView() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.study_center_text_view, null);
    }
}
