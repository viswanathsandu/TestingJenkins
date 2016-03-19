package com.education.corsalite.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.listener.OnRefreshNotesListener;
import com.education.corsalite.models.requestmodels.AddNoteRequest;
import com.education.corsalite.models.requestmodels.ForumModel;
import com.education.corsalite.models.requestmodels.Note;
import com.education.corsalite.models.requestmodels.UpdateNoteRequest;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.DefaultForumResponse;
import com.education.corsalite.models.responsemodels.DefaultNoteResponse;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import retrofit.client.Response;

/**
 * Created by vissu on 11/3/15.
 */
public class EditorDialogFragment extends DialogFragment implements View.OnClickListener {

    private WebView webview;
    private RelativeLayout forumHeaderLayout;
    private EditText titleTxt;
    private CheckBox isAuthorOnlyCkb;
    private Button addBtn;
    private Button editBtn;
    private Button cancelBtn;

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
    private OnRefreshNotesListener onRefreshNotesListener;

    public void setRefreshNoteListener(OnRefreshNotesListener onRefreshNotesListener) {
        this.onRefreshNotesListener = onRefreshNotesListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.editor_dialog_layout, container,
                false);

        type = getArguments().getString("type", "Note");
        operation = getArguments().getString("operation", "Add");
        studentId = getArguments().getString("student_id", "");
        subjectId = getArguments().getString("subject_id", "");
        chapterId = getArguments().getString("chapter_id", "");
        topicId = getArguments().getString("topic_id", "");
        contentId = getArguments().getString("content_id", "");
        notesId = getArguments().getString("notes_id", "");
        postId = getArguments().getString("post_id", "");
        originalContent = getArguments().getString("content", "");
        isAuthorOnly = getArguments().getString("is_author_only", "");
        postsubject = getArguments().getString("post_subject", "");
        getDialog().setTitle(operation+" "+type);
        initUi(rootView);
        loadWebview();
        return rootView;
    }

    private void initUi(View view) {
        forumHeaderLayout = (RelativeLayout) view.findViewById(R.id.forum_header_layout);
        if(type.equals("Note")) {
            forumHeaderLayout.setVisibility(View.GONE);
        } else if(type.equals("Forum")) {
            forumHeaderLayout.setVisibility(View.VISIBLE);
        }
        isAuthorOnlyCkb = (CheckBox)view.findViewById(R.id.is_author_only_ckb);
        if(isAuthorOnly.equals("Y")) {
            isAuthorOnlyCkb.setChecked(true);
        } else if(isAuthorOnly.equals("N")) {
            isAuthorOnlyCkb.setChecked(false);
        }
        titleTxt = (EditText) view.findViewById(R.id.title_txt);
        if(!TextUtils.isEmpty(postsubject)) {
            titleTxt.setText(postsubject);
        }
        if(operation.equals("Add")) {
            addBtn = (Button) view.findViewById(R.id.add_btn);
            addBtn.setOnClickListener(this);
            addBtn.setVisibility(View.VISIBLE);
        } else if(operation.equals("Edit")) {
            editBtn = (Button) view.findViewById(R.id.edit_btn);
            editBtn.setOnClickListener(this);
            editBtn.setVisibility(View.VISIBLE);
        }
        cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(this);
        initWebview(view);
    }

    @SuppressLint("JavascriptInterface")
    private void initWebview(View view) {
        webview = (WebView)view.findViewById(R.id.editor_webview);
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

            public void onPageFinished(WebView view, String url){
                webview.loadUrl("javascript:loadHtml('"+originalContent+"')");
            }
        });
        webview.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void updateContent(String content) {
                updateContent = content;
                L.info("UpdatedContent : " + updateContent);
                if(operation.equalsIgnoreCase("Add")) {
                    addContent();
                } else if(operation.equalsIgnoreCase("Edit")) {
                    editContent();
                }
            }

        }, "Android");
    }

    private void loadWebview() {
        webview.loadUrl("file:///android_asset/ckeditor/samples/index.html");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
            case R.id.edit_btn:
                webview.loadUrl("javascript:getUpdatedHtml()");
                break;
            case R.id.cancel_btn:
                this.dismiss();
                break;
            default :
                break;
        }
    }

    private void addContent() {
        if(type.equalsIgnoreCase("Note")) {
            addNotes();
        } else if(type.equalsIgnoreCase("Forum")) {
            addEditPostToForum(getForumPost());
        }
    }

    private void editContent() {
        if(type.equalsIgnoreCase("Note")) {
            editNotes();
        } else if(type.equalsIgnoreCase("Forum")) {
            addEditPostToForum(getForumPost());
        }
    }

    private ForumModel getForumPost() {
        ForumModel post = new ForumModel();
        if(!TextUtils.isEmpty(postId)) {
            post.idUserPost = postId;
        }
        post.studentId = LoginUserCache.getInstance().loginResponse.studentId;
        post.userId = LoginUserCache.getInstance().loginResponse.userId;
        post.courseId = AbstractBaseActivity.selectedCourse.courseId.toString();
        post.idCourseSubject = "44";//subjectId;
        post.idCourseSubjectChapter = "1179";//chapterId;
        post.topicId = "114";//topicId;
        post.postContent = updateContent;
        post.referIdUserPost = operation.equalsIgnoreCase("Add") ? null : "";
        post.postSubject = titleTxt.getText().toString();
        post.isAuthorOnly = isAuthorOnlyCkb.isChecked() ? "Y" : "N";
        return post;
    }

    private void addEditPostToForum(ForumModel post) {
        try {
            ApiManager.getInstance(getActivity()).addEditForumPost(post, new ApiCallback<DefaultForumResponse>(getActivity()) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    Toast.makeText(getActivity(), "Failed to add post on Forum", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void success(DefaultForumResponse defaultNoteResponse, Response response) {
                    super.success(defaultNoteResponse, response);
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Added post successfully", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
            });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void addNotes() {
        try {
            AddNoteRequest request = new AddNoteRequest(studentId, new Note(topicId, contentId, updateContent));
            ApiManager.getInstance(getActivity()).addNote(new Gson().toJson(request), new ApiCallback<DefaultNoteResponse>(getActivity()) {
                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    Toast.makeText(getActivity(), "Failed to add Note", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void success(DefaultNoteResponse defaultNoteResponse, Response response) {
                    super.success(defaultNoteResponse, response);
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Added Note successfully", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
            });
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    private void editNotes() {
        UpdateNoteRequest request = new UpdateNoteRequest(studentId, notesId, updateContent);
        ApiManager.getInstance(getActivity()).updateNote(new Gson().toJson(request), new ApiCallback<DefaultNoteResponse>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                Toast.makeText(getActivity(), "Failed to update Note", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(DefaultNoteResponse defaultNoteResponse, Response response) {
                super.success(defaultNoteResponse, response);
                Toast.makeText(getActivity(), "Updated Note successfully", Toast.LENGTH_SHORT).show();
                if(onRefreshNotesListener != null) {
                    onRefreshNotesListener.refreshNotes();
                }
                dismiss();
            }
        });
    }
}