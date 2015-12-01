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
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.adapters.GridRecyclerAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.db.DbManager;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.Chapters;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
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
    private StudyCenter studyCenter;
    private CourseData mCourseData;
    private LinearLayout linearLayout;
    private ArrayList<String> subjects;
    private List<Chapters> allChapters = new ArrayList<>();
    private View redView;
    private View blueView;
    private View yellowView;
    private View greenView;
    private LinearLayout allColorLayout;
    private String key;
    private TextView selectedSubjectTxt;
    private View selectedColorFilter;
    private boolean closeApp = false;
    private ArrayList<Object> offlineContentList;
    private boolean isOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_study_center, null);
        linearLayout = (LinearLayout) myView.findViewById(R.id.subjects_name_id);
        frameLayout.addView(myView);
        setUpViews(myView);
        setToolbarForStudyCenter();
        initUI();
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
                mAdapter.updateData(studyCenter.redListChapters, key);
                mAdapter.notifyDataSetChanged();
                updateSelected(redView);
            }
        });
        blueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(studyCenter.blueListChapters, key);
                mAdapter.notifyDataSetChanged();
                updateSelected(blueView);
            }
        });
        yellowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(studyCenter.amberListChapters, key);
                mAdapter.notifyDataSetChanged();
                updateSelected(yellowView);
            }
        });
        greenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(studyCenter.greenListChapters, key);
                mAdapter.notifyDataSetChanged();
                updateSelected(greenView);
            }
        });

        allColorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.updateData(studyCenter.Chapters, key);
                mAdapter.notifyDataSetChanged();
                updateSelected(allColorLayout);
            }
        });
    }

    private void updateSelected(View colorView) {
        if (selectedColorFilter != null) {
            selectedColorFilter.setSelected(false);
        }
        selectedColorFilter = colorView;
        selectedColorFilter.setSelected(true);
    }

    private void initDataAdapter(String subject) {
        showList();
        key = subject;
        allChapters = getChaptersForSubject();
        mAdapter = new GridRecyclerAdapter(allChapters, this, key);
        recyclerView.setAdapter(mAdapter);
    }

    private List<Chapters> getChaptersForSubject() {
        for (StudyCenter studyCenter : mCourseData.StudyCenter) {
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
            case R.id.action_exam_history:
                Intent intent = new Intent(this, ExamHistoryActivity.class);
                startActivity(intent);
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

    @Override
    public void onEvent(Course course) {
        super.onEvent(course);
        getStudyCentreData(course.courseId.toString());
    }

    private void getOfflineStudyCenterData() {
        DbManager.getInstance(this).getOfflineContentList(new ApiCallback<List<OfflineContent>>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
            }

            @Override
            public void success(List<OfflineContent> offlineContents, Response response) {
                offlineContentList = new ArrayList<>();
                if (offlineContentList != null && offlineContentList.size() > 0) {
                    studyCenter = ApiCacheHolder.getInstance().studyCenter.response.get(0);
                    setUpStudyCentreData(studyCenter);
                    mCourseData = new CourseData();
                    mCourseData.StudyCenter = ApiCacheHolder.getInstance().studyCenter.response;
                    setupSubjects(mCourseData);
                    for (Chapters chapter : studyCenter.Chapters) {
                        for (OfflineContent offlineContent : offlineContents) {
                            if (!chapter.idCourseSubjectchapter.equals(offlineContent.chapterId)) {
                                chapter.isChapterOffline = false;
                            }
                        }
                    }
                    initDataAdapter(subjects.get(0));
                } else {
                    Toast.makeText(StudyCentreActivity.this, "No offline content available for study center", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    showGrayBg(ApiCacheHolder.getInstance().studyCenter.response);
                }
                updateSelected(allColorLayout);
            }
        });
    }

    private void showGrayBg(List<StudyCenter> studyCenters) {
        if (studyCenters != null) {
            mCourseData = new CourseData();
            mCourseData.StudyCenter = studyCenters;
        }
        if (mCourseData != null && mCourseData.StudyCenter != null && !mCourseData.StudyCenter.isEmpty()) {
            setupSubjects(mCourseData);
            key = mCourseData.StudyCenter.get(0).SubjectName;
            studyCenter = mCourseData.StudyCenter.get(0);
            for (Chapters chapter : studyCenter.Chapters) {
                chapter.isChapterOffline = true;
            }
            setUpStudyCentreData(studyCenter);
            initDataAdapter(subjects.get(0));
            updateSelected(allColorLayout);
        } else {
            hideRecyclerView();
        }
    }

    private void getStudyCentreData(String courseId) {
        isOffline = true;
        if (!isOffline) {
            hideRecyclerView();

            ApiManager.getInstance(this).getStudyCentreData(LoginUserCache.getInstance().loginResponse.studentId,
                    courseId, new ApiCallback<List<StudyCenter>>(this) {
                        @Override
                        public void failure(CorsaliteError error) {
                            super.failure(error);
                            if (error != null && !TextUtils.isEmpty(error.message)) {
                                showToast(error.message);
                            }
                            hideRecyclerView();
                        }

                        @Override
                        public void success(List<StudyCenter> studyCenters, Response response) {
                            super.success(studyCenters, response);
                            if (studyCenters != null) {
                                ApiCacheHolder.getInstance().setStudyCenterResponse(studyCenters);
                                dbManager.saveReqRes(ApiCacheHolder.getInstance().studyCenter);
                                mCourseData = new CourseData();
                                mCourseData.StudyCenter = studyCenters;
                            }
                            if (mCourseData != null && mCourseData.StudyCenter != null && !mCourseData.StudyCenter.isEmpty()) {
                                setupSubjects(mCourseData);
                                key = mCourseData.StudyCenter.get(0).SubjectName;
                                studyCenter = mCourseData.StudyCenter.get(0);
                                setUpStudyCentreData(studyCenter);
                                initDataAdapter(subjects.get(0));
                                updateSelected(allColorLayout);
                            } else {
                                hideRecyclerView();
                            }
                        }
                    });
        } else {
            getOfflineStudyCenterData();
        }
    }

    private void hideRecyclerView() {
        recyclerView.setVisibility(View.GONE);
    }

    private void setupSubjects(CourseData courseData) {
        linearLayout.removeAllViews();
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

    private void setUpStudyCentreData(StudyCenter studyCenter) {
        resetColorsVisibility();
        studyCenter.resetColoredLists();
        if (key.equalsIgnoreCase(studyCenter.SubjectName)) {
            for (Chapters chapter : studyCenter.Chapters) {
                double totalMarks = Data.getDoubleWithTwoDecimals(chapter.totalTestedMarks);
                double earnedMarks = Data.getDoubleWithTwoDecimals(chapter.earnedMarks);
                double scoreRedPercentage = Data.getInt(chapter.scoreRed) * totalMarks / 100;
                double scoreAmberPercentage = Data.getInt(chapter.scoreAmber) * totalMarks / 100;
                if (earnedMarks == 0 && totalMarks == 0) {
                    studyCenter.blueListChapters.add(chapter);
                    blueView.setVisibility(View.VISIBLE);
                } else if (earnedMarks < scoreRedPercentage) {
                    studyCenter.redListChapters.add(chapter);
                    redView.setVisibility(View.VISIBLE);
                } else if (earnedMarks < scoreAmberPercentage) {
                    studyCenter.amberListChapters.add(chapter);
                    yellowView.setVisibility(View.VISIBLE);
                } else {
                    studyCenter.greenListChapters.add(chapter);
                    greenView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void addSubjectsAndCreateViews(StudyCenter studyCenter) {
        String subject = studyCenter.SubjectName;
        subjects.add(subject);
        linearLayout.addView(getSubjectView(subject, studyCenter.idCourseSubject + "", subjects.size() == 1));
    }

    public String getSelectedSubjectId() {
        if (selectedSubjectTxt != null) {
            return selectedSubjectTxt.getTag().toString();
        }
        return null;
    }

    private View getSubjectView(String subjectName, String subjectId, boolean isSelected) {
        View v = getView();
        TextView tv = (TextView) v.findViewById(R.id.subject);
        tv.setText(subjectName);
        tv.setTag(subjectId);
        if (isSelected) {
            tv.setSelected(true);
            selectedSubjectTxt = tv;
        }
        setListener(tv, subjectName);
        return v;
    }

    private void setListener(final TextView textView, final String text) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList();
                if (selectedSubjectTxt != null) {
                    selectedSubjectTxt.setSelected(false);
                }
                selectedSubjectTxt = textView;
                selectedSubjectTxt.setSelected(true);
                if (mCourseData != null && mCourseData.StudyCenter != null) {
                    key = text;
                    for (StudyCenter studyCenter : mCourseData.StudyCenter) {
                        if (key.equalsIgnoreCase(studyCenter.SubjectName)) {
                            StudyCentreActivity.this.studyCenter = studyCenter;
                            setUpStudyCentreData(studyCenter);
                            mAdapter.updateData(getChaptersForSubject(), text);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        });
    }

    private View getView() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.study_center_text_view, null);
    }

    @Override
    public void onBackPressed() {
        if (!closeApp) {
            closeApp = true;
            showToast(getString(R.string.app_close_alert));
        } else {
            finish();
        }
    }
}
