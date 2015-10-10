package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
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
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.CourseData;
import com.education.corsalite.models.responsemodels.StudyCenter;
import com.education.corsalite.utils.Data;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by ayush on 25/09/15.
 */
public class StudyCentreActivity extends AbstractBaseActivity {

    private GridRecyclerAdapter mAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CourseData courseData;
    private LinearLayout linearLayout;
    private ArrayList<String> subjects;
    private ArrayList<Chapters> allChapters = new ArrayList<>();
    private View redView;
    private View blueView;
    private View yellowView;
    private View greenView;
    private LinearLayout allColorLayout;
    private String key;
    private Integer mCourseId = 13; // using offline
    private Integer mSubjectId = 51; // using offline

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
                mAdapter.updateData(courseData.redListChapters, key);
                mAdapter.notifyDataSetChanged();
            }
        });
        blueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(courseData.blueListChapters, key);
                mAdapter.notifyDataSetChanged();
            }
        });
        yellowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(courseData.amberListChapters, key);
                mAdapter.notifyDataSetChanged();
            }
        });
        greenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(courseData.greenListChapters, key);
                mAdapter.notifyDataSetChanged();
            }
        });

        allColorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(allChapters, key);
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    private void initDataAdapter(String subject) {
        showList();
        key = subject;
        mAdapter = new GridRecyclerAdapter(getChaptersForSubject(), this, key);
        recyclerView.setAdapter(mAdapter);
    }

    private List<Chapters> getChaptersForSubject() {
        for (StudyCenter studyCenter : courseData.StudyCenter) {
            if (studyCenter.SubjectName.equals(key)) {
                return studyCenter.Chapters;
            }
        }
        return null;
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
            case R.id.action_take_test:
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
        ApiManager.getInstance(this).getStudyCentreData(LoginUserCache.getInstance().loginResponse.studentId,
                "13", new ApiCallback<List<StudyCenter>>() {
                    @Override
                    public void failure(CorsaliteError error) {
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                        hideRecyclerView();
                    }

                    @Override
                    public void success(List<StudyCenter> studyCenters, Response response) {
                        if(studyCenters != null) {
                            courseData = new CourseData();
                            courseData.StudyCenter = studyCenters;
                        }
                        if (courseData != null && courseData.StudyCenter != null && courseData.StudyCenter.size() > 0) {
                            StudyCentreActivity.this.courseData = courseData;
                            setupSubjects(courseData);
                            key = courseData.StudyCenter.get(0).SubjectName;
                            setUpStudyCentreData(courseData);
                            initDataAdapter(subjects.get(0));
                        } else {
                            hideRecyclerView();
                        }
                    }
                });
    }

    private void hideRecyclerView() {
        recyclerView.setVisibility(View.GONE);
    }

    private void setupSubjects(CourseData courseData) {
        subjects = new ArrayList<String>();
        for (StudyCenter studyCenter : courseData.StudyCenter) {
            addSubjectsAndCreateViews(studyCenter);
        }
    }

    private void resetColorsVisibility() {
        blueView.setVisibility(View.GONE);
        yellowView.setVisibility(View.GONE);
        greenView.setVisibility(View.GONE);
        redView.setVisibility(View.GONE);
    }

    private void setUpStudyCentreData(CourseData courseData) {
        resetColorsVisibility();
        courseData.resetColoredLists();
        allChapters.clear();
        for (StudyCenter studyCenter : courseData.StudyCenter) {
            if(key.equalsIgnoreCase(studyCenter.SubjectName)) {
                for (Chapters chapter : studyCenter.Chapters) {
                    if (Data.getInt(chapter.earnedMarks) == 0 && Data.getInt(chapter.totalTestedMarks) == 0) {
                        courseData.blueListChapters.add(chapter);
                        blueView.setVisibility(View.VISIBLE);
                    } else if(Data.getInt(chapter.earnedMarks) < Data.getInt(chapter.scoreRed)){
                        courseData.redListChapters.add(chapter);
                        redView.setVisibility(View.VISIBLE);
                    } else if (Data.getInt(chapter.earnedMarks) < Data.getInt(chapter.scoreAmber)) {
                        courseData.amberListChapters.add(chapter);
                        yellowView.setVisibility(View.VISIBLE);
                    } else {
                        courseData.greenListChapters.add(chapter);
                        greenView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            }
        }
        allChapters.addAll(courseData.blueListChapters);
        allChapters.addAll(courseData.greenListChapters);
        allChapters.addAll(courseData.amberListChapters);
        allChapters.addAll(courseData.redListChapters);
    }

    private void addSubjectsAndCreateViews(StudyCenter studyCenter) {
        String subject = studyCenter.SubjectName;
        subjects.add(subject);
        TextView tv = getTextView(subject);
        linearLayout.addView(tv);
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
                if (courseData != null && courseData.StudyCenter != null) {
                    key = text;
                    setUpStudyCentreData(courseData);
                    mAdapter.updateData(getChaptersForSubject(), text);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void navigateToContentReading() {
        Intent intent = new Intent(StudyCentreActivity.this, WebActivity.class);
        intent.putExtra("courseId", "11");
        intent.putExtra("subjectId", "37");
        intent.putExtra("chapterId", "1068");
        intent.putExtra("topicId", "887");
        intent.putExtra("contentId", "1536");
        startActivity(intent);
    }

    private View getView() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.study_center_text_view, null);
    }
}
