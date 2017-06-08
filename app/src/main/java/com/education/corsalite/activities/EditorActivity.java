package com.education.corsalite.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.education.corsalite.R;
import com.education.corsalite.adapters.ChapterAdapter;
import com.education.corsalite.adapters.SubjectAdapter;
import com.education.corsalite.adapters.TopicAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.gson.Gson;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.TopicModel;
import com.education.corsalite.models.requestmodels.AddNoteRequest;
import com.education.corsalite.models.requestmodels.ForumModel;
import com.education.corsalite.models.requestmodels.Note;
import com.education.corsalite.models.requestmodels.UpdateNoteRequest;
import com.education.corsalite.models.responsemodels.CommonResponseModel;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.models.responsemodels.DefaultForumResponse;
import com.education.corsalite.models.responsemodels.DefaultNoteResponse;
import com.education.corsalite.utils.L;
import com.education.corsalite.views.CustomButton;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by Madhuri on 14-04-2016.
 */
public class EditorActivity extends AbstractBaseActivity {
    private WebView webview;
    private RelativeLayout forumHeaderLayout;
    private Spinner subjectSpinner;
    private Spinner chapterSpinner;
    private Spinner topicSpinner;
    private EditText titleTxt;
    private CheckBox isAuthorOnlyCkb;
    private View progress;
    private CustomButton edit_btn;

    private String type;
    private String operation;
    private String studentId;
    private String subjectId;
    private String chapterId;
    private String topicId;
    private String contentId;
    private String notesId;
    private String postId;
    private String postsubject;
    private String userId;
    private String locked;
    private String courseId;
    private String isAuthorOnly;
    private String originalContent;
    private String updateContent;

    private List<ContentIndex> mContentIndexList;
    private List<SubjectModel> mSubjectModelList;
    private List<ChapterModel> mChapterModelList;
    private List<TopicModel> mTopicModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_dialog_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        edit_btn = (CustomButton)findViewById(R.id.edit_btn);

        type = getIntent().getExtras().getString("type", "Note");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            operation = bundle.getString("operation", "Add");
            courseId = bundle.getString("course_id", "");
            userId = bundle.getString("user_id", "");
            locked = bundle.getString("locked", "");
            studentId = bundle.getString("student_id", "");
            subjectId = bundle.getString("subject_id", "");
            chapterId = bundle.getString("chapter_id", "");
            topicId = bundle.getString("topic_id", "");
            contentId = bundle.getString("content_id", "");
            notesId = bundle.getString("notes_id", "");
            postId = bundle.getString("post_id", "");
            originalContent = bundle.getString("content", "");
            isAuthorOnly = bundle.getString("is_author_only", "");
            postsubject = bundle.getString("post_subject", "");
        }
        getSupportActionBar().setTitle(operation+" "+type);
        initUi();
        loadWebview();

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.loadUrl("javascript:getUpdatedHtml()");
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUi() {
        forumHeaderLayout = (RelativeLayout) findViewById(R.id.forum_header_layout);
        subjectSpinner = (Spinner) findViewById(R.id.subject_spinner);
        chapterSpinner = (Spinner) findViewById(R.id.chapter_spinner);
        topicSpinner = (Spinner) findViewById(R.id.topic_spinner);
        progress = findViewById(R.id.progress);
        if (type.equals("Note") || type.equals("Comment")) {
            forumHeaderLayout.setVisibility(View.GONE);
        } else if (type.equals("Forum")) {
            forumHeaderLayout.setVisibility(View.VISIBLE);
            getContentIndex(getSelectedCourseId(), LoginUserCache.getInstance().getStudentId());
        }
        isAuthorOnlyCkb = (CheckBox) findViewById(R.id.is_author_only_ckb);
        if (isAuthorOnly.equals("Y")) {
            isAuthorOnlyCkb.setChecked(true);
        } else if (isAuthorOnly.equals("N")) {
            isAuthorOnlyCkb.setChecked(false);
        } else {
            isAuthorOnlyCkb.setVisibility(View.GONE);
        }
        titleTxt = (EditText) findViewById(R.id.title_txt);
        if (!TextUtils.isEmpty(postsubject)) {
            titleTxt.setText(postsubject);
        }
        initWebview();
    }

    @SuppressLint("JavascriptInterface")
    private void initWebview() {
        webview = (WebView) findViewById(R.id.editor_webview);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.setScrollbarFadingEnabled(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                webview.loadUrl("javascript:loadHtml('" + originalContent + "')");
            }
        });
        webview.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void updateContent(String content) {
                updateContent = content;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        L.info("UpdatedContent : " + updateContent);
                        try {
//                            updateContent = updateContent.replace("+", "%2B");
                            updateContent = URLEncoder.encode(updateContent, "UTF-8");
                        } catch (Exception e) {
                            L.error(e.getMessage(), e);
                        }
                        if (operation.equalsIgnoreCase("Add")) {
                            addContent();
                        } else if (operation.equalsIgnoreCase("Edit")) {
                            editContent();
                        }
                    }
                });
            }
        }, "Android");
    }

    private void loadWebview() {
        webview.loadUrl("file:///android_asset/ckeditor/samples/index.html");
    }

    private void addComment(ForumModel post) {
        try {
            showProgress();
            ApiManager.getInstance(this).addComment(post, new ApiCallback<CommonResponseModel>(this){
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    closeProgress();
                    showToast("Failed to add comment");
                }
                @Override
                public void success(CommonResponseModel commonResponseModel, Response response) {
                    super.success(commonResponseModel, response);
                    closeProgress();
                    showToast("Comment added successfully");
                    finish();
                }
            });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private ForumModel getComment() {
        ForumModel post = new ForumModel();
        post.userId = appPref.getUserId();
        post.studentId = LoginUserCache.getInstance().getLoginResponse().studentId;
        post.courseId = courseId;
        post.idCourseSubject = subjectId;
        post.idCourseSubjectChapter = chapterId;
        post.topicId = topicId;
        post.postSubject = postsubject;
        if(!TextUtils.isEmpty(postId)) {
            post.idUserPost = null;
            post.referIdUserPost = postId;
        }
        post.postContent = updateContent;
        post.isAuthorOnly = isAuthorOnly;
        post.locked="Y";
        return post;
    }

    private void addContent() {
        if(updateContent.trim().isEmpty()) {
            showToast("Content can not be empty");
            return;
        }
        if (type.equalsIgnoreCase("Note")) {
            addNotes();
        } else if (type.equalsIgnoreCase("Forum")) {
            addEditPostToForum(getForumPost());
        }else if(type.equalsIgnoreCase("Comment")) {
            addComment(getComment());
        }
    }

    private void editContent() {
        if(updateContent.trim().isEmpty()) {
            showToast("Content can not be empty");
            return;
        }
        if (type.equalsIgnoreCase("Note")) {
            editNotes();
        } else if (type.equalsIgnoreCase("Forum")) {
            addEditPostToForum(getForumPost());
        }
    }

    private ForumModel getForumPost() {
        ForumModel post = new ForumModel();
        if (!TextUtils.isEmpty(postId)) {
            post.idUserPost = postId;
        }
        try {
            post.studentId = LoginUserCache.getInstance().getStudentId();
            post.userId = appPref.getUserId();
            post.courseId = AbstractBaseActivity.getSelectedCourseId();
            if (mSubjectModelList != null && !mSubjectModelList.isEmpty()) {
                post.idCourseSubject = mSubjectModelList.get(subjectSpinner.getSelectedItemPosition()).idSubject;
            }
            if (mChapterModelList != null && !mChapterModelList.isEmpty()) {
                post.idCourseSubjectChapter = mChapterModelList.get(chapterSpinner.getSelectedItemPosition()).idChapter;
            }
            if (mTopicModelList != null && !mTopicModelList.isEmpty()) {
                post.topicId = mTopicModelList.get(topicSpinner.getSelectedItemPosition()).idTopic;
            }
            post.postContent = updateContent;
            post.referIdUserPost = operation.equalsIgnoreCase("Add") ? null : "";
            post.postSubject = titleTxt.getText().toString();
            post.isAuthorOnly = isAuthorOnlyCkb.isChecked() ? "Y" : "N";
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return post;
    }

    private void addEditPostToForum(ForumModel post) {
        try {
            showProgress();
            ApiManager.getInstance(EditorActivity.this).addEditForumPost(post, new ApiCallback<DefaultForumResponse>(EditorActivity.this) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    closeProgress();
                    showToast("Failed to add post on Forum");
                }

                @Override
                public void success(DefaultForumResponse defaultNoteResponse, Response response) {
                    super.success(defaultNoteResponse, response);
                    closeProgress();
                    showToast("Post added successfully");
                    finish();
                }
            });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void addNotes() {
        try {
            showProgress();
            AddNoteRequest request = new AddNoteRequest(studentId, new Note(topicId, contentId, updateContent));
            ApiManager.getInstance(EditorActivity.this).addNote(Gson.get().toJson(request), new ApiCallback<DefaultNoteResponse>(EditorActivity.this) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    closeProgress();
                    showToast("Failed to add Note");
                }

                @Override
                public void success(DefaultNoteResponse defaultNoteResponse, Response response) {
                    super.success(defaultNoteResponse, response);
                    closeProgress();
                    showToast("Note added successfully");
                    finish();
                }
            });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void editNotes() {
        UpdateNoteRequest request = new UpdateNoteRequest(studentId, notesId, updateContent);
        showProgress();
        ApiManager.getInstance(EditorActivity.this).updateNote(Gson.get().toJson(request), new ApiCallback<DefaultNoteResponse>(EditorActivity.this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                closeProgress();
                showToast("Failed to update Note");
            }

            @Override
            public void success(DefaultNoteResponse defaultNoteResponse, Response response) {
                super.success(defaultNoteResponse, response);
                showToast("Updated Note successfully");
                closeProgress();
                finish();
            }
        });
    }

    @Override
    protected void getContentIndex(Course course, String studentId) {
        // Do nothing
    }

    protected void getContentIndex(String courseId, String studentId) {
        showProgress();
        ApiManager.getInstance(EditorActivity.this).getContentIndex(courseId, studentId,
                new ApiCallback<List<ContentIndex>>(EditorActivity.this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            ((AbstractBaseActivity) EditorActivity.this).showToast(error.message);
                        }
                    }

                    @Override
                    public void success(List<ContentIndex> contentIndexs, Response response) {
                        super.success(contentIndexs, response);
                        closeProgress();
                        if (contentIndexs != null) {
                            mContentIndexList = contentIndexs;
                            showSubject();
                        }
                    }
                });
    }

    private void showSubject() {
        ContentIndex mContentIndex = mContentIndexList.get(0);
        mSubjectModelList = new ArrayList<>(mContentIndex.subjectModelList);
        final SubjectAdapter subjectAdapter = new SubjectAdapter(mSubjectModelList, EditorActivity.this);
        subjectSpinner.setAdapter(subjectAdapter);

        int listSize = mSubjectModelList.size();
        if (!subjectId.isEmpty()) {
            for (int i = 0; i < listSize; i++) {
                if (mSubjectModelList.get(i).idSubject.equalsIgnoreCase(subjectId)) {
                    subjectSpinner.setSelection(i);
                    subjectId = "";
                    break;
                }
            }
        }

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showChapter(position);
                subjectAdapter.setSelectedPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showChapter(int subjectPosition) {

        mChapterModelList = new ArrayList<>(mSubjectModelList.get(subjectPosition).chapters);
        Collections.sort(mChapterModelList); // sort the chapters based on the chapterSortOrder
        final ChapterAdapter chapterAdapter = new ChapterAdapter(mChapterModelList, EditorActivity.this);
        chapterSpinner.setAdapter(chapterAdapter);

        if (!chapterId.isEmpty()) {
            int listSize = mChapterModelList.size();
            for (int i = 0; i < listSize; i++) {
                if (mChapterModelList.get(i).idChapter.equalsIgnoreCase(chapterId)) {
                    chapterSpinner.setSelection(i);
                    chapterId = "";
                    break;
                }
            }
        }

        chapterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showTopic(position);
                chapterAdapter.setSelectedPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showTopic(final int chapterPosition) {
        mTopicModelList = new ArrayList<>(mChapterModelList.get(chapterPosition).topicMap);
        Collections.sort(mTopicModelList);
        if (mTopicModelList != null) {
            final TopicAdapter topicAdapter = new TopicAdapter(mTopicModelList, EditorActivity.this);
            topicSpinner.setAdapter(topicAdapter);

            if (!topicId.isEmpty()) {
                int listSize = mTopicModelList.size();
                for (int i = 0; i < listSize; i++) {
                    if (mTopicModelList.get(i).idTopic.equalsIgnoreCase(topicId)) {
                        topicSpinner.setSelection(i);
                        topicId = "";
                        break;
                    }
                }
            }

            topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    topicAdapter.setSelectedPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    public void showProgress() {
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    public void closeProgress() {
        progress.setVisibility(View.GONE);
    }

}
