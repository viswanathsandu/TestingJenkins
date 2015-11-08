package com.education.corsalite.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.models.requestmodels.AddNoteRequest;
import com.education.corsalite.models.requestmodels.Note;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.DefaultNoteResponse;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import java.util.logging.Logger;

import retrofit.client.Response;

/**
 * Created by vissu on 11/3/15.
 */
public class EditorDialogFragment extends DialogFragment implements View.OnClickListener {

    private WebView webview;
    private Button addBtn;
    private Button editBtn;
    private Button cancelBtn;

    private String type;
    private String operation;
    private String studentId;
    private String topicId;
    private String contentId;
    private String originalContent;
    private String updateContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.editor_dialog_layout, container,
                false);

        type = getArguments().getString("type", "Notes");
        operation = getArguments().getString("operation", "Add");
        studentId = getArguments().getString("student_id", "");
        topicId = getArguments().getString("topic_id", "");
        contentId = getArguments().getString("content_id", "");
        originalContent = getArguments().getString("content", "");

        getDialog().setTitle("Corsalite Html Editor");
        initUi(rootView);
        loadWebview();
        return rootView;
    }

    private void initUi(View view) {
        addBtn = (Button) view.findViewById(R.id.add_btn);
        addBtn.setOnClickListener(this);
        editBtn = (Button) view.findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(this);
        cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(this);
        if(operation.equalsIgnoreCase("Add")) {
            addBtn.setVisibility(View.VISIBLE);
        } else if(operation.equalsIgnoreCase("Edit")) {
            editBtn.setVisibility(View.VISIBLE);
        }
        initWebview(view);
    }

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
                webview.loadUrl("javascript:getUpdatedHtml()");
            }
        });
        webview.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void updateContent(String content) {
                updateContent = content;
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
                addContent();
                break;
            case R.id.edit_btn:
                editContent();
                break;
            case R.id.cancel_btn:
                this.dismiss();
                break;
            default :
                break;
        }
    }

    private void addContent() {
        if(type.equalsIgnoreCase("Notes")) {
            addNotes();
        }
    }

    private void editContent() {
        if(type.equalsIgnoreCase("Notes")) {
            editNotes();
        }
    }

    private void addNotes() {
        AddNoteRequest request = new AddNoteRequest(studentId, new Note(topicId, contentId, updateContent));
        ApiManager.getInstance(getActivity()).addNote(new Gson().toJson(request), new ApiCallback<DefaultNoteResponse>(getActivity()) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                Toast.makeText(getActivity(), "Failed to add notes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(DefaultNoteResponse defaultNoteResponse, Response response) {
                super.success(defaultNoteResponse, response);
                Toast.makeText(getActivity(), "Added Notes successfull", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void editNotes() {

    }
}
