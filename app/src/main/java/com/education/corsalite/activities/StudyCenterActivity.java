package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.GridRecyclerAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.fragments.PartTestDialog;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.Chapter;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.CourseData;
import com.education.corsalite.models.responsemodels.StudyCenter;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.Data;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by ayush on 25/09/15.
 */
public class StudyCenterActivity extends AbstractBaseActivity {

    private GridRecyclerAdapter mAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CourseData mCourseData;
    private ArrayList<String> subjects;
    private ArrayList<View> subjectViews;
    private List<Chapter> allChapters = new ArrayList<>();
    private View redView;
    private View blueView;
    private View yellowView;
    private View greenView;
    private LinearLayout allColorLayout;
    private LinearLayout subjectBarLayout;
    private LinearLayout subjectSpinnerLayout;
    private StudyCenter mStudyCenter;
    private View selectedColorFilter;
    private AlertDialog alertDialog;
    private AppCompatSpinner subjectsSpinner;
    private ImageButton subjectIconImg;
    ArrayAdapter<StudyCenter> subjectSpinnerAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_study_center, null);
        frameLayout.addView(myView);
        subjectSpinnerLayout = (LinearLayout) findViewById(R.id.subject_spinner_layout);
        subjectBarLayout = (LinearLayout) findViewById(R.id.top_layout);
        subjectsSpinner = (AppCompatSpinner) myView.findViewById(R.id.spinner_subjects_list);
        subjectSpinnerAdpater = new ArrayAdapter<>(
                this, R.layout.support_simple_spinner_dropdown_item, new ArrayList<StudyCenter>());
        subjectsSpinner.setAdapter(subjectSpinnerAdpater);
        subjectIconImg = (ImageButton) myView.findViewById(R.id.subject_icon);
        setSubjectOptionsClickListener();
        subjects = new ArrayList<String>();
        subjectViews = new ArrayList<>();
        setUpViews(myView);
        setToolbarForStudyCenter();
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // fetch schedule tests and configure the notifications
        loadScheduledTests();
        if (getSelectedCourse() != null) {
            getStudyCentreData(getSelectedCourse().courseId.toString());
        }
        if (mAdapter != null) {
            recyclerView.invalidate();
        }
    }

    @Override
    protected void onCourseChanged(Course course) {
        super.onCourseChanged(course);
        if (course.isTestSeries()) {
            startActivity(new Intent(this, TestSeriesActivity.class));
            finish();
        } else {
            getStudyCentreData(course.courseId.toString());
        }
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
                if (mStudyCenter != null && mStudyCenter.redListChapters != null) {
                    mAdapter.updateData(mStudyCenter.redListChapters, mStudyCenter.SubjectName);
                    mAdapter.notifyDataSetChanged();
                    updateSelected(redView);
                }
            }
        });
        blueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStudyCenter != null && mStudyCenter.blueListChapters != null) {
                    mAdapter.updateData(mStudyCenter.blueListChapters, mStudyCenter.SubjectName);
                    mAdapter.notifyDataSetChanged();
                    updateSelected(blueView);
                }
            }
        });
        yellowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStudyCenter != null && mStudyCenter.amberListChapters != null) {
                    mAdapter.updateData(mStudyCenter.amberListChapters, mStudyCenter.SubjectName);
                    mAdapter.notifyDataSetChanged();
                    updateSelected(yellowView);
                }
            }
        });
        greenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStudyCenter != null && mStudyCenter.greenListChapters != null) {
                    mAdapter.updateData(mStudyCenter.greenListChapters, mStudyCenter.SubjectName);
                    mAdapter.notifyDataSetChanged();
                    updateSelected(greenView);
                }
            }
        });

        allColorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStudyCenter != null && mStudyCenter.chapters != null) {
                    mAdapter.updateData(mStudyCenter.chapters, mStudyCenter.SubjectName);
                    mAdapter.notifyDataSetChanged();
                    updateSelected(allColorLayout);
                }
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

    private void initDataAdapter() {
        showList();
        mAdapter = new GridRecyclerAdapter(mStudyCenter.chapters, this, mStudyCenter.SubjectName);
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
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.action_exam_history:
                intent = new Intent(this, ExamHistoryActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_mock_test:
                showMockTestsDialog();
                return true;
            case R.id.action_scheduled_test:
                showScheduledTestsDialog();
                return true;
            case R.id.action_challenge_your_friends:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        recyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        recyclerView.setHasFixedSize(true);
        if (getResources().getBoolean(R.bool.isTablet)) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            }
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            }
        }
        recyclerView.setAdapter(mAdapter);
        progressBar = (ProgressBar) findViewById(R.id.headerProgress);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getBoolean(R.bool.isTablet)) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            }
        } else {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            }
        }
    }

    private void showList() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void getStudyCentreData(String courseId) {
        hideRecyclerView();
        subjectBarLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        ApiManager.getInstance(this).getStudyCentreData(LoginUserCache.getInstance().getStudentId(),
                courseId, new ApiCallback<List<StudyCenter>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        subjectBarLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        super.failure(error);
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                        hideRecyclerView();
                    }

                    @Override
                    public void success(List<StudyCenter> studyCenters, Response response) {
                        super.success(studyCenters, response);
                        progressBar.setVisibility(View.GONE);
                        subjectBarLayout.setVisibility(View.VISIBLE);
                        if (SystemUtils.isNetworkConnected(StudyCenterActivity.this)) {
                            if (studyCenters != null) {
                                ApiCacheHolder.getInstance().setStudyCenterResponse(studyCenters);
                                dbManager.saveReqRes(ApiCacheHolder.getInstance().studyCenter);
                                mCourseData = new CourseData();
                                mCourseData.StudyCenter = studyCenters;
                                if (!studyCenters.isEmpty()) {
                                    mStudyCenter = studyCenters.get(0);
                                }
                            }
                            if (mCourseData != null && mCourseData.StudyCenter != null && !mCourseData.StudyCenter.isEmpty()) {
                                setupSubjects(mCourseData);
                                setUpStudyCentreData(mStudyCenter);
                                initDataAdapter();
                                updateSelected(allColorLayout);
                            } else {
                                hideRecyclerView();
                            }
                            getOfflineStudyCenterData(studyCenters, true);
                        } else {
                            mCourseData = new CourseData();
                            mCourseData.StudyCenter = studyCenters;
                            if (!studyCenters.isEmpty()) {
                                mStudyCenter = studyCenters.get(0);
                            }
                            getOfflineStudyCenterData(studyCenters, false);
                        }
                        if (subjectViews != null && !subjectViews.isEmpty()) {
                            subjectViews.get(0).performClick();
                        }
                    }
                });
    }

    private void getOfflineStudyCenterData(final List<StudyCenter> studyCenters, final boolean saveForOffline) {
        List<OfflineContent> offlineContents = dbManager.getOfflineContents(AbstractBaseActivity.getSelectedCourseId());
        if (mStudyCenter != null && mStudyCenter.chapters != null) {
            for (Chapter chapter : mStudyCenter.chapters) {
                boolean idMatchFound = false;
                for (OfflineContent offlineContent : offlineContents) {
                    if (chapter.idCourseSubjectChapter.equals(offlineContent.chapterId)) {
                        idMatchFound = true;
                    }
                    offlineContent.earnedMarks = chapter.earnedMarks;
                    offlineContent.totalTestedMarks = chapter.totalTestedMarks;
                    offlineContent.scoreAmber = chapter.scoreAmber;
                    offlineContent.scoreRed = chapter.scoreRed;
                }
                if (!saveForOffline) {
                    if (idMatchFound) {
                        chapter.isChapterOffline = true;
                    } else {
                        chapter.isChapterOffline = false;
                    }
                }
            }
        }
        if (saveForOffline) {
            dbManager.save(offlineContents);
        } else {
            mCourseData = new CourseData();
            mCourseData.StudyCenter = studyCenters;
            setupSubjects(mCourseData);
            initDataAdapter();
        }
    }


    private void hideRecyclerView() {
        recyclerView.setVisibility(View.GONE);
    }

    private void resetColorsVisibility() {
        blueView.setVisibility(View.GONE);
        yellowView.setVisibility(View.GONE);
        greenView.setVisibility(View.GONE);
        redView.setVisibility(View.GONE);
    }

    private void setUpStudyCentreData(StudyCenter studyCenter) {
        try {
            resetColorsVisibility();
            studyCenter.resetColoredLists();
            for (Chapter chapter : studyCenter.chapters) {
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
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    // Use this to show the color of spinner
    private Drawable getSubjectColor(StudyCenter studyCenter) {
        int blueChaptersCount = 0;
        int redChaptersCount = 0;
        int amberChaptersCount = 0;
        int greenChaptersCount = 0;
        try {
            for (Chapter chapter : studyCenter.chapters) {
                double totalMarks = Data.getDoubleWithTwoDecimals(chapter.totalTestedMarks);
                double earnedMarks = Data.getDoubleWithTwoDecimals(chapter.earnedMarks);
                double scoreRedPercentage = Data.getInt(chapter.scoreRed) * totalMarks / 100;
                double scoreAmberPercentage = Data.getInt(chapter.scoreAmber) * totalMarks / 100;
                if (earnedMarks == 0 && totalMarks == 0) {
                    blueChaptersCount++;
                } else if (earnedMarks < scoreRedPercentage) {
                    redChaptersCount++;
                } else if (earnedMarks < scoreAmberPercentage) {
                    amberChaptersCount++;
                } else {
                    greenChaptersCount++;
                }
            }
            if (redChaptersCount > 0) {
                return getResources().getDrawable(R.drawable.redshape);
            } else if (greenChaptersCount == studyCenter.chapters.size()) {
                return getResources().getDrawable(R.drawable.greenshape);
            } else if (blueChaptersCount == studyCenter.chapters.size()) {
                return getResources().getDrawable(R.drawable.blueshape);
            } else {
                return getResources().getDrawable(R.drawable.ambershape);
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return getResources().getDrawable(R.drawable.redshape);
        }
    }

    private void setupSubjects(CourseData courseData) {
        subjectSpinnerAdpater.clear();
        subjectSpinnerAdpater.addAll(courseData.StudyCenter);
        subjectSpinnerAdpater.notifyDataSetChanged();
        subjectsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showList();
                mStudyCenter = (StudyCenter) (parent.getSelectedItem());
                if (!SystemUtils.isNetworkConnected(StudyCenterActivity.this)) {
                    List<OfflineContent> offlineContents = dbManager.getOfflineContents(AbstractBaseActivity.getSelectedCourseId());
                    for (Chapter chapter : mStudyCenter.chapters) {
                        chapter.isChapterOffline = false;
                        for (OfflineContent offlineContent : offlineContents) {
                            if (chapter.idCourseSubjectChapter.equals(offlineContent.chapterId)) {
                                chapter.isChapterOffline = true;
                                break;
                            }
                        }
                    }
                }
                setUpStudyCentreData(mStudyCenter);
                subjectSpinnerLayout.setBackground(getSubjectColor(mStudyCenter));
                mAdapter.updateData(mStudyCenter.chapters, mStudyCenter.SubjectName);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public String getSelectedSubjectId() {
        if (subjectsSpinner != null) {
            return ((StudyCenter) (subjectsSpinner.getSelectedItem())).idCourseSubject + "";
        }
        return null;
    }


    private void setSubjectOptionsClickListener() {
        subjectIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(subjectsSpinner);
            }
        });
    }

    private void showAlertDialog(View v) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = li.inflate(R.layout.layout_list_item_view_popup, null);
            TextView saveOfflineText = (TextView) dialogView.findViewById(R.id.offline_content);
            saveOfflineText.setVisibility(View.GONE);
            builder.setView(dialogView);
            setDataForAlert(dialogView, mStudyCenter);
            alertDialog = builder.create();
            WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.TOP | Gravity.LEFT;
            // position the alertDialog
            wmlp.x = (int) v.getX() + 15;
            wmlp.y = (int) (v.getY() + 15 + v.getContext().getResources().getDimension(R.dimen.abc_action_bar_default_height_material));
            wmlp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            wmlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            alertDialog.show();
            alertDialog.getWindow().setAttributes(wmlp);
            alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void startContentActivity(StudyCenter studyCenter) {
        if (studyCenter != null && studyCenter.chapters != null && !studyCenter.chapters.isEmpty()) {
            Intent intent = new Intent(this, ContentReadingActivity.class);
            intent.putExtra("courseId", AbstractBaseActivity.getSelectedCourseId());
            intent.putExtra("subjectId", studyCenter.idCourseSubject + "");
            intent.putExtra("chapterId", studyCenter.chapters.get(0).idCourseSubjectChapter + "");
            startActivity(intent);
        }
    }

    private void setDataForAlert(View dialogView, final StudyCenter studyCenter) {
        TextView takeTestLabel = (TextView) dialogView.findViewById(R.id.take_test);
        takeTestLabel.setText(getString(R.string.menu_part_test));
        TextView score = (TextView) dialogView.findViewById(R.id.score);
        score.setText(studyCenter.getScore());
        TextView notes = (TextView) dialogView.findViewById(R.id.notes);
        notes.setText(studyCenter.getNotes());
        TextView completedTopics = (TextView) dialogView.findViewById(R.id.completed_topics);
        completedTopics.setText(studyCenter.getCompletion() + "%");
        dialogView.findViewById(R.id.take_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                showPartTestGrid(studyCenter);
            }
        });
        dialogView.findViewById(R.id.notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                startNotesActivity(studyCenter);
            }
        });
        dialogView.findViewById(R.id.flagged_questions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                startFlaggedQuestionView(studyCenter);
            }
        });
        dialogView.findViewById(R.id.start_reading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                startContentActivity(studyCenter);
            }
        });
    }

    private void startFlaggedQuestionView(StudyCenter studyCenter) {
        if (!SystemUtils.isNetworkConnected(this)) {
            showToast("This feature is not available for offline. Please come online.");
            return;
        }
        Intent exerciseIntent = new Intent(this, ExamEngineActivity.class);
        exerciseIntent.putExtra(Constants.TEST_TITLE, "Flagged Questions");
        exerciseIntent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.getSelectedCourseId());
        exerciseIntent.putExtra(Constants.SELECTED_SUBJECTID, studyCenter.idCourseSubject + "");
        exerciseIntent.putExtra(Constants.SELECTED_SUBJECT_NAME, mStudyCenter.SubjectName);
        startActivity(exerciseIntent);
    }

    private void startNotesActivity(StudyCenter studyCenter) {
        Intent intent = new Intent(this, NotesActivity.class);
        intent.putExtra("courseId", AbstractBaseActivity.getSelectedCourseId());
        intent.putExtra(GridRecyclerAdapter.SUBJECT_ID, studyCenter.idCourseSubject + "");
        startActivity(intent);
    }

    private void showPartTestGrid(StudyCenter studyCenter) {
        if (SystemUtils.isNetworkConnected(this)) {
            PartTestDialog dialog = new PartTestDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("idCourseSubject", studyCenter.idCourseSubject);
            bundle.putString(Constants.SELECTED_SUBJECT_NAME, studyCenter.SubjectName);
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "PartTestDialog");
        } else {
            Intent exerciseIntent = new Intent(this, OfflineActivity.class);
            exerciseIntent.putExtra("selection", 1);
            startActivity(exerciseIntent);
        }
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            loadWelcomeScreen();
        } else {
            super.onBackPressed();
        }
    }


}