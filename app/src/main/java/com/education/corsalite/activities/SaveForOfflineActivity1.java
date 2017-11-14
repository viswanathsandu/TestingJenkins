package com.education.corsalite.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.education.corsalite.R;
import com.education.corsalite.adapters.SaveForOfflineAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.TopicModel;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Praveen on 14-11-2017.
 */

public class SaveForOfflineActivity1 extends AbstractBaseActivity {
    @Bind(R.id.content_recyclerview)
    RecyclerView contentRecyclerview;
    @Bind(R.id.headerProgress)
    ProgressBar mProgressBar;
    @Bind(R.id.download)
    LinearLayout downloadImage;
    private LinearLayoutManager mLayoutManager;
    // explorer_buttonsLayout  content_recyclerview headerProgress
    public static final String COMMA_STRING = ",";
    public static final String SUBJECT_ID = "subjectId";
    public static final String CHAPTER_ID = "chapterId";
    public static final String TOPIC_ID = "topicId";
    public static final String CONTENT_ID = "contentId";
    public static final String SUBJECT = "subject";
    public static final String COURSE_ID = "courseId";
    public static final String CHAPTER_NAME = "chapterName";
    public static final String COURSE_NAME = "courseName";
    private String mSubjectId = "";
    private String mChapterId = "";
    private String mTopicId = "";
    private String mContentId = "";
    private String mSubjectName = "";
    private String mCourseId = "";
    private String mChapterName = "";
    private String mCourseName = "";
    private Bundle savedInstanceState;

    private ContentIndex contentIndexList;
    private ArrayList<SubjectModel> subjectModelList;
    private ArrayList<ChapterModel> chapterModelList;
    private ArrayList<TopicModel> topicModelList;
    private boolean mLoading = true;
    private SaveForOfflineAdapter saveForOfflineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_offline_subject1, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);

        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   loopCheckedViews();
            }
        });
        setToolbarForOfflineContentReading();

        mLayoutManager = new LinearLayoutManager(this);
        contentRecyclerview.setLayoutManager(mLayoutManager);
        getBundleData();
        getContentIndex(mCourseId, LoginUserCache.getInstance().getStudentId());
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(SUBJECT_ID) && bundle.getString(SUBJECT_ID) != null) {
                mSubjectId = bundle.getString(SUBJECT_ID);
            }
            if (bundle.containsKey(CHAPTER_ID) && bundle.getString(CHAPTER_ID) != null) {
                mChapterId = bundle.getString(CHAPTER_ID);
            }
            if (bundle.containsKey(TOPIC_ID) && bundle.getString(TOPIC_ID) != null) {
                mTopicId = bundle.getString(TOPIC_ID);
            }
            if (bundle.containsKey(CONTENT_ID) && bundle.getString(CONTENT_ID) != null) {
                mContentId = bundle.getString(CONTENT_ID);
            }
            if (bundle.containsKey(SUBJECT) && bundle.getString(SUBJECT) != null) {
                mSubjectName = bundle.getString(SUBJECT);
            }
            if (bundle.containsKey(COURSE_ID) && bundle.getString(COURSE_ID) != null) {
                mCourseId = bundle.getString(COURSE_ID);
            }
            if (bundle.containsKey(CHAPTER_NAME) && bundle.getString(CHAPTER_NAME) != null) {
                mChapterName = bundle.getString(CHAPTER_NAME);
            }
            if (bundle.containsKey(COURSE_NAME) && bundle.getString(COURSE_NAME) != null) {
                mCourseName = bundle.getString(COURSE_NAME);
            }
        }
    }


    @Override
    protected void getContentIndex(Course course, String studentId) {
        // Do nothing
    }

    protected void getContentIndex(String courseId, String studentId) {
        ApiManager.getInstance(this).getContentIndex(courseId, studentId,
                new ApiCallback<List<ContentIndex>>(this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            showToast(error.message);
                        }
                        mProgressBar.setVisibility(View.GONE);
                        mLoading = false;
                    }

                    @Override
                    public void success(List<ContentIndex> mContentIndexs, Response response) {
                        mLoading = false;
                        super.success(mContentIndexs, response);
                        mProgressBar.setVisibility(View.GONE);
                        if (mContentIndexs != null || mContentIndexs.isEmpty()) {
                            contentIndexList = mContentIndexs.get(0);

                          System.out.println("Hello " + contentIndexList.subjectModelList);
                            return;
                        }
                        contentRecyclerview.setVisibility(View.VISIBLE);
                       /* if (saveForOfflineAdapter == null) {
                          //  saveForOfflineAdapter = new SaveForOfflineAdapter(mContentIndexs, getLayoutInflater(),
                          //          SaveForOfflineActivity1.this);
                            contentRecyclerview.setAdapter(saveForOfflineAdapter);
                        } else {
                            saveForOfflineAdapter.addAll(mContentIndexs);
                            saveForOfflineAdapter.notifyDataSetChanged();
                        }*/

                    }
                });
    }

    private void setSaveForOfflineData() {
        mProgressBar.setVisibility(View.VISIBLE);
        contentRecyclerview.setVisibility(View.GONE);
    }
}
