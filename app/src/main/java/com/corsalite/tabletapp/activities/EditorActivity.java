package com.corsalite.tabletapp.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
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
import android.widget.Toast;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.adapters.ChapterAdapter;
import com.corsalite.tabletapp.adapters.SubjectAdapter;
import com.corsalite.tabletapp.adapters.TopicAdapter;
import com.corsalite.tabletapp.api.ApiCallback;
import com.corsalite.tabletapp.api.ApiManager;
import com.corsalite.tabletapp.cache.LoginUserCache;
import com.corsalite.tabletapp.models.ChapterModel;
import com.corsalite.tabletapp.models.SubjectModel;
import com.corsalite.tabletapp.models.TopicModel;
import com.corsalite.tabletapp.models.requestmodels.AddNoteRequest;
import com.corsalite.tabletapp.models.requestmodels.ForumModel;
import com.corsalite.tabletapp.models.requestmodels.Note;
import com.corsalite.tabletapp.models.requestmodels.UpdateNoteRequest;
import com.corsalite.tabletapp.models.responsemodels.CommonResponseModel;
import com.corsalite.tabletapp.models.responsemodels.ContentIndex;
import com.corsalite.tabletapp.models.responsemodels.CorsaliteError;
import com.corsalite.tabletapp.models.responsemodels.DefaultForumResponse;
import com.corsalite.tabletapp.models.responsemodels.DefaultNoteResponse;
import com.corsalite.tabletapp.utils.L;
import com.google.gson.Gson;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.edit_btn:
                webview.loadUrl("javascript:getUpdatedHtml()");
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
            getContentIndex(AbstractBaseActivity.selectedCourse.courseId + "", LoginUserCache.getInstance().loginResponse.studentId);
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
        post.userId = LoginUserCache.getInstance().getLongResponse().userId;
        post.studentId = LoginUserCache.getInstance().getLongResponse().studentId;
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
        if (type.equalsIgnoreCase("Note")) {
            addNotes();
        } else if (type.equalsIgnoreCase("Forum")) {
            addEditPostToForum(getForumPost());
        }else if(type.equalsIgnoreCase("Comment")) {
            addComment(getComment());
        }
    }

    private void editContent() {
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
        post.studentId = LoginUserCache.getInstance().loginResponse.studentId;
        post.userId = LoginUserCache.getInstance().loginResponse.userId;
        post.courseId = AbstractBaseActivity.selectedCourse.courseId.toString();
        post.idCourseSubject = mSubjectModelList.get(subjectSpinner.getSelectedItemPosition()).idSubject;
        post.idCourseSubjectChapter = mChapterModelList.get(chapterSpinner.getSelectedItemPosition()).idChapter;
        post.topicId = mTopicModelList.get(topicSpinner.getSelectedItemPosition()).idTopic;
        post.postContent = updateContent;
        post.referIdUserPost = operation.equalsIgnoreCase("Add") ? null : "";
        post.postSubject = titleTxt.getText().toString();
        post.isAuthorOnly = isAuthorOnlyCkb.isChecked() ? "Y" : "N";
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
                    Toast.makeText(EditorActivity.this, "Failed to add post on Forum", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void success(DefaultForumResponse defaultNoteResponse, Response response) {
                    super.success(defaultNoteResponse, response);
                    closeProgress();
                    Toast.makeText(EditorActivity.this, "Post added successfully", Toast.LENGTH_SHORT).show();
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
            ApiManager.getInstance(EditorActivity.this).addNote(new Gson().toJson(request), new ApiCallback<DefaultNoteResponse>(EditorActivity.this) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    closeProgress();
                    Toast.makeText(EditorActivity.this, "Failed to add Note", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void success(DefaultNoteResponse defaultNoteResponse, Response response) {
                    super.success(defaultNoteResponse, response);
                    closeProgress();
                    Toast.makeText(EditorActivity.this, "Note added successfully", Toast.LENGTH_SHORT).show();
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
        ApiManager.getInstance(EditorActivity.this).updateNote(new Gson().toJson(request), new ApiCallback<DefaultNoteResponse>(EditorActivity.this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                closeProgress();
                Toast.makeText(EditorActivity.this, "Failed to update Note", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(DefaultNoteResponse defaultNoteResponse, Response response) {
                super.success(defaultNoteResponse, response);
                Toast.makeText(EditorActivity.this, "Updated Note successfully", Toast.LENGTH_SHORT).show();
                closeProgress();
                finish();
            }
        });
    }

    private void getContentIndex(String courseId, String studentId) {
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
