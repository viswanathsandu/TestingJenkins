package com.education.corsalite.activities;

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

import com.education.corsalite.R;
import com.education.corsalite.adapters.ChapterAdapter;
import com.education.corsalite.adapters.SubjectAdapter;
import com.education.corsalite.adapters.TopicAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.ChapterModel;
import com.education.corsalite.models.SubjectModel;
import com.education.corsalite.models.TopicModel;
import com.education.corsalite.models.requestmodels.AddNoteRequest;
import com.education.corsalite.models.requestmodels.ForumModel;
import com.education.corsalite.models.requestmodels.Note;
import com.education.corsalite.models.requestmodels.UpdateNoteRequest;
import com.education.corsalite.models.responsemodels.ContentIndex;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.DefaultForumResponse;
import com.education.corsalite.models.responsemodels.DefaultNoteResponse;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by Madhuri on 14-04-2016.
 */
public class NewPostActivity extends AbstractBaseActivity {
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
        toolbar.setTitle("New Post");

        type = getIntent().getExtras().getString("type", "Note");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            operation = bundle.getString("operation", "Add");
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
            case R.id.cancel_btn:
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
        if (type.equals("Note")) {
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

    private void addContent() {
        if (type.equalsIgnoreCase("Note")) {
            addNotes();
        } else if (type.equalsIgnoreCase("Forum")) {
            addEditPostToForum(getForumPost());
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
            ApiManager.getInstance(NewPostActivity.this).addEditForumPost(post, new ApiCallback<DefaultForumResponse>(NewPostActivity.this) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    closeProgress();
                    Toast.makeText(NewPostActivity.this, "Failed to add post on Forum", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void success(DefaultForumResponse defaultNoteResponse, Response response) {
                    super.success(defaultNoteResponse, response);
                    closeProgress();
                    Toast.makeText(NewPostActivity.this, "Post added successfully", Toast.LENGTH_SHORT).show();
                    NewPostActivity.this.finish();
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
            ApiManager.getInstance(NewPostActivity.this).addNote(new Gson().toJson(request), new ApiCallback<DefaultNoteResponse>(NewPostActivity.this) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    closeProgress();
                    Toast.makeText(NewPostActivity.this, "Failed to add Note", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void success(DefaultNoteResponse defaultNoteResponse, Response response) {
                    super.success(defaultNoteResponse, response);
                    closeProgress();
                    Toast.makeText(NewPostActivity.this, "Note added successfully", Toast.LENGTH_SHORT).show();
                    NewPostActivity.this.finish();
                }
            });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void editNotes() {
        UpdateNoteRequest request = new UpdateNoteRequest(studentId, notesId, updateContent);
        showProgress();
        ApiManager.getInstance(NewPostActivity.this).updateNote(new Gson().toJson(request), new ApiCallback<DefaultNoteResponse>(NewPostActivity.this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                closeProgress();
                Toast.makeText(NewPostActivity.this, "Failed to update Note", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(DefaultNoteResponse defaultNoteResponse, Response response) {
                super.success(defaultNoteResponse, response);
                Toast.makeText(NewPostActivity.this, "Updated Note successfully", Toast.LENGTH_SHORT).show();
                closeProgress();
                NewPostActivity.this.finish();
            }
        });
    }

    private void getContentIndex(String courseId, String studentId) {
        showProgress();
        ApiManager.getInstance(NewPostActivity.this).getContentIndex(courseId, studentId,
                new ApiCallback<List<ContentIndex>>(NewPostActivity.this) {
                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                        if (error != null && !TextUtils.isEmpty(error.message)) {
                            ((AbstractBaseActivity) NewPostActivity.this).showToast(error.message);
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
        final SubjectAdapter subjectAdapter = new SubjectAdapter(mSubjectModelList, NewPostActivity.this);
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
        final ChapterAdapter chapterAdapter = new ChapterAdapter(mChapterModelList, NewPostActivity.this);
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
            final TopicAdapter topicAdapter = new TopicAdapter(mTopicModelList, NewPostActivity.this);
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
