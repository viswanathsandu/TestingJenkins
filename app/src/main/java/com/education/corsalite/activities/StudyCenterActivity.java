package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
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
    private StudyCenter studyCenter;
    private CourseData mCourseData;
    private LinearLayout linearLayout;
    private ArrayList<String> subjects;
    private ArrayList<View> subjectViews;
    private List<Chapter> allChapters = new ArrayList<>();
    private View redView;
    private View blueView;
    private View yellowView;
    private View greenView;
    private LinearLayout allColorLayout;
    private String mSubjectName;
    private TextView selectedSubjectTxt;
    private View selectedColorFilter;
    private ArrayList<Object> offlineContentList;
    private AlertDialog alertDialog;

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

    @Override
    protected void onResume() {
        super.onResume();
        // fetch schedule tests and configure the notifications
        loadScheduledTests();
        if (mAdapter != null) {
            recyclerView.invalidate();
        }
    }

    @Override
    public void onEvent(Course course) {
        super.onEvent(course);
        if(course.isTestSeries()) {
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
                if (studyCenter != null && studyCenter.redListChapters != null) {
                    mAdapter.updateData(studyCenter.redListChapters, mSubjectName);
                    mAdapter.notifyDataSetChanged();
                    updateSelected(redView);
                }
            }
        });
        blueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studyCenter != null && studyCenter.blueListChapters != null) {
                    mAdapter.updateData(studyCenter.blueListChapters, mSubjectName);
                    mAdapter.notifyDataSetChanged();
                    updateSelected(blueView);
                }
            }
        });
        yellowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studyCenter != null && studyCenter.amberListChapters != null) {
                    mAdapter.updateData(studyCenter.amberListChapters, mSubjectName);
                    mAdapter.notifyDataSetChanged();
                    updateSelected(yellowView);
                }
            }
        });
        greenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studyCenter != null && studyCenter.greenListChapters != null) {
                    mAdapter.updateData(studyCenter.greenListChapters, mSubjectName);
                    mAdapter.notifyDataSetChanged();
                    updateSelected(greenView);
                }
            }
        });

        allColorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studyCenter != null && studyCenter.chapters != null) {
                    mAdapter.updateData(studyCenter.chapters, mSubjectName);
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

    private void initDataAdapter(String subject) {
        showList();
        mSubjectName = subject;
        allChapters = getChaptersForSubject();
        mAdapter = new GridRecyclerAdapter(allChapters, this, mSubjectName);
        recyclerView.setAdapter(mAdapter);
    }

    private List<Chapter> getChaptersForSubject() {
        for (StudyCenter studyCenter : mCourseData.StudyCenter) {
            if (studyCenter.SubjectName.equals(mSubjectName)) {
                return studyCenter.chapters;
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

    private void getStudyCentreData(String courseId) {
        hideRecyclerView();
        progressBar.setVisibility(View.VISIBLE);
        ApiManager.getInstance(this).getStudyCentreData(LoginUserCache.getInstance().getStudentId(),
                courseId, new ApiCallback<List<StudyCenter>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
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
                        if (SystemUtils.isNetworkConnected(StudyCenterActivity.this)) {
                            if (studyCenters != null) {
                                ApiCacheHolder.getInstance().setStudyCenterResponse(studyCenters);
                                dbManager.saveReqRes(ApiCacheHolder.getInstance().studyCenter);
                                mCourseData = new CourseData();
                                mCourseData.StudyCenter = studyCenters;
                            }
                            if (mCourseData != null && mCourseData.StudyCenter != null && !mCourseData.StudyCenter.isEmpty()) {
                                setupSubjects(mCourseData);
                                mSubjectName = mCourseData.StudyCenter.get(0).SubjectName;
                                studyCenter = mCourseData.StudyCenter.get(0);
                                setUpStudyCentreData(studyCenter);
                                initDataAdapter(subjects.get(0));
                                updateSelected(allColorLayout);
                            } else {
                                hideRecyclerView();
                            }
                            getOfflineStudyCenterData(studyCenters, true);
                        } else {
                            getOfflineStudyCenterData(studyCenters, false);
                        }
                        if(subjectViews != null && !subjectViews.isEmpty()) {
                            subjectViews.get(0).performClick();
                        }
                    }
                });
    }

    private void getOfflineStudyCenterData(final List<StudyCenter> studyCenters, final boolean saveForOffline) {
        List<OfflineContent> offlineContents = dbManager.getOfflineContents(AbstractBaseActivity.getSelectedCourseId());
        if (studyCenter != null && studyCenter.chapters != null) {
            for (Chapter chapter : studyCenter.chapters) {
                boolean idMatchFound = false;
                for (OfflineContent offlineContent : offlineContents) {
                    if (chapter.idCourseSubjectchapter.equals(offlineContent.chapterId)) {
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
            mSubjectName = mCourseData.StudyCenter.get(getIndex(studyCenters)).SubjectName;
            studyCenter = mCourseData.StudyCenter.get(getIndex(studyCenters));
            setupSubjects(mCourseData);
            initDataAdapter(subjects.get(getIndex(studyCenters)));
        }
    }

    private int getIndex(List<StudyCenter> studyCenters) {
        int i = 0;
        if (mSubjectName == null) {
            return 0;
        }
        for (StudyCenter studyCenter : studyCenters) {
            if (mSubjectName.equals(studyCenter.SubjectName)) {
                break;
            }
            i++;
        }
        return i;
    }

    private void hideRecyclerView() {
        recyclerView.setVisibility(View.GONE);
    }

    private void setupSubjects(CourseData courseData) {
        linearLayout.removeAllViews();
        subjects = new ArrayList<String>();
        subjectViews = new ArrayList<>();
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
        try {
            resetColorsVisibility();
            studyCenter.resetColoredLists();
            if (mSubjectName.equalsIgnoreCase(studyCenter.SubjectName)) {
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
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

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

    private void addSubjectsAndCreateViews(StudyCenter studyCenter) {
        String subject = studyCenter.SubjectName;
        subjects.add(subject);
        linearLayout.addView(getSubjectView(studyCenter, studyCenter.idCourseSubject + "", subjects.size() == 1));
    }

    public String getSelectedSubjectId() {
        if (selectedSubjectTxt != null) {
            return selectedSubjectTxt.getTag().toString();
        }
        return null;
    }

    private View getSubjectView(StudyCenter studyCenter, String subjectId, boolean isSelected) {
        View v = getView();
        v.findViewById(R.id.subjectLayout).setBackgroundDrawable(getSubjectColor(studyCenter));
        TextView tv = (TextView) v.findViewById(R.id.subject);
        tv.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.roboto_regular)));
        tv.setText(studyCenter.SubjectName);
        tv.setTag(subjectId);

        ImageView iv = (ImageView) v.findViewById(R.id.arrow_img);
        setListener(v, iv, studyCenter);
        if (isSelected) {
            tv.setSelected(true);
            selectedSubjectTxt = tv;
        }
        setListener(tv, studyCenter.SubjectName);
        if(subjectViews != null) {
            subjectViews.add(tv);
        }
        return v;
    }

    private void setListener(final View v, ImageView imageView, final StudyCenter studyCenter) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(v, studyCenter);
            }
        });
    }

    private void showAlertDialog(View v, StudyCenter studyCenter) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = li.inflate(R.layout.layout_list_item_view_popup, null);

            TextView saveOfflineText = (TextView) dialogView.findViewById(R.id.offline_content);
            saveOfflineText.setVisibility(View.GONE);

            builder.setView(dialogView);
            setDataForAlert(dialogView, studyCenter);
            alertDialog = builder.create();
            WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.TOP | Gravity.LEFT;
            // position the alertDialog
            wmlp.x = (int) v.getX() + 15;
            wmlp.y = (int) v.getY() + 140;
            alertDialog.show();
            alertDialog.getWindow().setAttributes(wmlp);
            alertDialog.getWindow().setLayout(300, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void startContentActivity(StudyCenter studyCenter) {
        if (studyCenter != null && studyCenter.chapters != null && !studyCenter.chapters.isEmpty()) {
            Intent intent = new Intent(this, ContentReadingActivity.class);
            intent.putExtra("courseId", AbstractBaseActivity.getSelectedCourseId());
            intent.putExtra("subjectId", studyCenter.idCourseSubject + "");
            intent.putExtra("chapterId", studyCenter.chapters.get(0).idCourseSubjectchapter + "");
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
        dialogView.findViewById(R.id.notes_layout).setOnClickListener(new View.OnClickListener() {
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
        if(!SystemUtils.isNetworkConnected(this)) {
            showToast("This feature is not available for offline. Please come online.");
            return;
        }
        Intent exerciseIntent = new Intent(this, ExamEngineActivity.class);
        exerciseIntent.putExtra(Constants.TEST_TITLE, "Flagged Questions");
        exerciseIntent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.getSelectedCourseId());
        exerciseIntent.putExtra(Constants.SELECTED_SUBJECTID, studyCenter.idCourseSubject + "");
        exerciseIntent.putExtra(Constants.SELECTED_SUBJECT_NAME, mSubjectName);
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

    private void setListener(final TextView textView, final String text) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList();
                if (SystemUtils.isNetworkConnected(StudyCenterActivity.this)) {
                    if (selectedSubjectTxt != null) {
                        selectedSubjectTxt.setSelected(false);
                    }
                    selectedSubjectTxt = textView;
                    selectedSubjectTxt.setSelected(true);
                    mSubjectName = text;
                    if (mCourseData != null && mCourseData.StudyCenter != null) {
                        mSubjectName = text;
                        for (StudyCenter studyCenter : mCourseData.StudyCenter) {
                            if (mSubjectName.equalsIgnoreCase(studyCenter.SubjectName)) {
                                StudyCenterActivity.this.studyCenter = studyCenter;
                                setUpStudyCentreData(studyCenter);
                                mAdapter.updateData(getChaptersForSubject(), text);
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                } else {
                    if (selectedSubjectTxt != null) {
                        selectedSubjectTxt.setSelected(false);
                    }
                    selectedSubjectTxt = textView;
                    selectedSubjectTxt.setSelected(true);
                    mSubjectName = text;
                    if (mCourseData != null && mCourseData.StudyCenter != null) {
                        mSubjectName = text;
                        List<OfflineContent> offlineContents = dbManager.getOfflineContents(AbstractBaseActivity.getSelectedCourseId());
                        for (StudyCenter studyCenter : mCourseData.StudyCenter) {
                            if (mSubjectName.equalsIgnoreCase(studyCenter.SubjectName)) {
                                StudyCenterActivity.this.studyCenter = studyCenter;
                                setUpStudyCentreData(studyCenter);
                                for (Chapter chapter : getChaptersForSubject()) {
                                    boolean idMatchFound = false;
                                    for (OfflineContent offlineContent : offlineContents) {
                                        if (chapter.idCourseSubjectchapter.equals(offlineContent.chapterId)) {
                                            idMatchFound = true;
                                        }
                                    }
                                    if (idMatchFound) {
                                        chapter.isChapterOffline = true;
                                    } else {
                                        chapter.isChapterOffline = false;
                                    }
                                }
                                mAdapter.updateData(getChaptersForSubject(), text);
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
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
        if (isTaskRoot()) {
            loadWelcomeScreen();
        } else {
            super.onBackPressed();
        }
    }
}
