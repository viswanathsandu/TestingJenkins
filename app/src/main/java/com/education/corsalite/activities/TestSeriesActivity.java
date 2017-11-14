package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.adapters.TestSeriesAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.enums.Tests;
import com.education.corsalite.event.TestDownloadCompletedEvent;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.listener.iTestSeriesClickListener;
import com.education.corsalite.models.db.MockTest;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.TestChapter;
import com.education.corsalite.models.responsemodels.TestSeriesMockData;
import com.education.corsalite.models.responsemodels.TestSeriesResponse;
import com.education.corsalite.models.responsemodels.TestSubject;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.SystemUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;


public class TestSeriesActivity extends AbstractBaseActivity implements iTestSeriesClickListener {

    private RecyclerView recyclerView;
    private TestSeriesAdapter mAdapter;
    private LinearLayout linearLayout;

    private List<TestSubject> mSubjects;
    private List<View> mSubjectViews = new ArrayList<>();
    private View mSelectedSubjectTxt;
    private TestSubject mSubject;
    private List<TestSeriesMockData> mMockData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_test_series, null);
        frameLayout.addView(myView);
        setToolbarForTestSeries();
        initUi();
        if (savedInstanceState != null) {
            String subjectsJson = savedInstanceState.getString("subjects");
            if (!TextUtils.isEmpty(subjectsJson)) {
                mSubjects = Gson.get().fromJson(subjectsJson, new TypeToken<List<TestSubject>>() {
                }.getType());
            }
            String mockJson = savedInstanceState.getString("mockdata");
            if (!TextUtils.isEmpty(mockJson)) {
                mMockData = Gson.get().fromJson(mockJson, new TypeToken<List<TestSeriesMockData>>() {
                }.getType());
            }
            if (mSubjects != null) {
                addSubjectsAndCreateViews();
            }
        } else {
            loadTestSeries();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSubjects == null) {
            loadTestSeries();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSubjects != null) {
            outState.putString("subjects", Gson.get().toJson(mSubjects));
        }
        if (mMockData != null) {
            outState.putString("mockdata", Gson.get().toJson(mMockData));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeProgress();
    }

    @Override
    protected void onCourseChanged(Course course) {
        super.onCourseChanged(course);
        if (course.isTestSeries()) {
            loadTestSeries();
        } else {
            startActivity(new Intent(this, StudyCenterActivity.class));
            finish();
        }
    }

    public void onEventMainThread(TestDownloadCompletedEvent event) {
        loadTestSeries();
    }

    private void initUi() {
        findViewById(R.id.all_colors).setVisibility(View.GONE);
        linearLayout = (LinearLayout) findViewById(R.id.subjects_name_id);
        recyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        mAdapter = new TestSeriesAdapter(this, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    private void addSubjectsAndCreateViews() {
        linearLayout.removeAllViews();
        mSubjectViews.clear();
        for (TestSubject subject : mSubjects) {
            linearLayout.addView(getSubjectView(subject));
        }
        if (!mSubjectViews.isEmpty()) {
            mSubjectViews.get(0).performClick();
        }
    }

    private View getSubjectView(TestSubject subject) {
        View v = getView();
        v.findViewById(R.id.arrow_img).setVisibility(View.GONE);
        v.findViewById(R.id.subjectLayout).setBackgroundDrawable(getSubjectColor(subject));
        TextView tv = (TextView) v.findViewById(R.id.subject);
        tv.setTypeface(Typeface.createFromAsset(getAssets(), getString(R.string.roboto_regular)));
        tv.setText(subject.subjectName);
        tv.setTag(subject.idCourseSubject);
        setListener(tv, subject);
        if (mSubjectViews != null) {
            mSubjectViews.add(tv);
        }
        return v;
    }

    private void setListener(final View view, final TestSubject subject) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedSubjectTxt != null) {
                    mSelectedSubjectTxt.setSelected(false);
                }
                mSubject = subject;
                mSelectedSubjectTxt = view;
                mSelectedSubjectTxt.setSelected(true);
                showData(subject, mMockData);
            }
        });
    }

    private Drawable getSubjectColor(TestSubject subject) {
        if (TextUtils.isEmpty(subject.btncolor)) {
            return getResources().getDrawable(R.drawable.blueshape);
        }
        switch (subject.btncolor) {
            case "btn-red":
                return getResources().getDrawable(R.drawable.redshape);
            case "btn-green":
                return getResources().getDrawable(R.drawable.greenshape);
            case "btn-amber":
                return getResources().getDrawable(R.drawable.ambershape);
            case "btn-blue":
            default:
                return getResources().getDrawable(R.drawable.blueshape);
        }
    }

    private View getView() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.study_center_text_view, null);
    }

    private void loadTestSeries() {
        showProgress();
        ApiManager.getInstance(this).getTestSeries(LoginUserCache.getInstance().getStudentId(),
                getSelectedCourseId(), getSelectedCourse().courseInstanceId,
                new ApiCallback<TestSeriesResponse>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                        L.error(error.message);
                    }

                    @Override
                    public void success(TestSeriesResponse testSeriesResponse, Response response) {
                        super.success(testSeriesResponse, response);
                        closeProgress();
                        if (testSeriesResponse != null && testSeriesResponse.subjects != null) {
                            if (SystemUtils.isNetworkConnected(TestSeriesActivity.this)) {
                                ApiCacheHolder.getInstance().setTestSeriesResponse(testSeriesResponse);
                                dbManager.saveReqRes(ApiCacheHolder.getInstance().testSeriesReqRes);
                            }
                            mSubjects = testSeriesResponse.subjects;
                            mMockData = testSeriesResponse.mockTests;
                            addSubjectsAndCreateViews();
                        }
                    }
                });
    }

    private void showData(TestSubject subject, List<TestSeriesMockData> mockTests) {
        mAdapter.update(subject, mockTests);
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void onTakeTest(TestChapter chapter) {
        if (SystemUtils.isNetworkConnected(this)) {
            Intent exerciseIntent = new Intent(this, TestStartActivity.class);
            exerciseIntent.putExtra(TestStartActivity.KEY_TEST_TYPE, Tests.CHAPTER.getType());
            exerciseIntent.putExtra(Constants.TEST_TITLE, "Take Test");
            exerciseIntent.putExtra(Constants.SELECTED_COURSE, AbstractBaseActivity.getSelectedCourseId());
            exerciseIntent.putExtra(Constants.SELECTED_SUBJECTID, mSubject.idCourseSubject);
            exerciseIntent.putExtra(Constants.SELECTED_SUBJECT_NAME, mSubject.subjectName);
            exerciseIntent.putExtra(Constants.SELECTED_CHAPTERID, chapter.idCourseSubjectChapter);
            exerciseIntent.putExtra(Constants.SELECTED_CHAPTER_NAME, chapter.ChapterName);
            exerciseIntent.putExtra(Constants.LEVEL_CROSSED, 4);
            exerciseIntent.putExtra(Constants.QUESTIONS_COUNT, Integer.parseInt(chapter.NumberOfQuestions));
            exerciseIntent.putExtra("chapter", Gson.get().toJson(chapter));
            startActivity(exerciseIntent);
        } else {
            Intent exerciseIntent = new Intent(this, OfflineActivity.class);
            exerciseIntent.putExtra("selection", 1);
            startActivity(exerciseIntent);
        }
        mSubjects = null;
        mMockData = null;
    }

    @Override
    public void onMockTest(TestSeriesMockData mock) {
        if (TextUtils.isEmpty(mock.idExamTemplate)) {
            return;
        }
        MockTest mockTest = new MockTest();
        mockTest.examTemplateId = mock.idExamTemplate;
        mockTest.displayName = mock.testName;
        mockTest.subjectId = mock.subjectId;
        mockTest.subjectName = mSubject.subjectName;
        List<MockTest> tests = new ArrayList<>();
        tests.add(mockTest);
        showMockTestsDialog(tests);
        mSubjects = null;
        mMockData = null;
    }

    @Override
    public void onMockTest(TestChapter chapter) {
        if (TextUtils.isEmpty(chapter.idExamTemplate)) {
            return;
        }
        MockTest mockTest = new MockTest();
        mockTest.examTemplateId = chapter.idExamTemplate;
        mockTest.displayName = chapter.ChapterName;
        mockTest.subjectId = mSubject.idCourseSubject;
        mockTest.subjectName = chapter.ChapterName;
        List<MockTest> tests = new ArrayList<>();
        tests.add(mockTest);
        showMockTestsDialog(tests);
        mSubjects = null;
        mMockData = null;
    }

    @Override
    public void onRecommendedReading(TestChapter chapter) {
        mSubjects = null;
        mMockData = null;
        Intent intent = new Intent(this, ContentReadingActivity.class);
        intent.putExtra("courseId", AbstractBaseActivity.getSelectedCourseId());
        intent.putExtra("subjectId", mSubject.idCourseSubject);
        intent.putExtra("chapterId", chapter.idCourseSubjectChapter);
        startActivity(intent);
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