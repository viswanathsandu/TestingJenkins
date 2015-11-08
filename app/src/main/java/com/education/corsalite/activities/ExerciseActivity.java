package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.education.corsalite.R;
import com.education.corsalite.utils.Constants;
import com.education.corsalite.utils.L;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mt0060 on 04/11/15.
 */
public class ExerciseActivity extends AbstractBaseActivity {

    @Bind(R.id.vs_container) ViewSwitcher mViewSwitcher;
    @Bind(R.id.footer_layout) RelativeLayout webFooter;
    @Bind(R.id.btn_next) Button btnNext;
    @Bind(R.id.btn_previous) Button btnPrevious;
    @Bind(R.id.webview_question) WebView webviewQuestion;
    @Bind(R.id.tv_comment) TextView tvComment;
    @Bind(R.id.tv_header) TextView tvHeader;

    String webQuestion = "";
    private int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_exercise, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        initWebView();
        setListener();
        if(getIntent().hasExtra(Constants.SELECTED_POSITION)) {
            selectedPosition = getIntent().getExtras().getInt(Constants.SELECTED_POSITION);
        }

        if(selectedPosition >= 0) {
            setToolbarForExercise(WebActivity.exerciseModelList, selectedPosition);
        }
        if(WebActivity.exerciseModelList.size() > 1) {
            webFooter.setVisibility(View.VISIBLE);
        } else {
            webFooter.setVisibility(View.GONE);
        }
    }

    private void setListener() {
        btnNext.setOnClickListener(mClickListener);
        btnPrevious.setOnClickListener(mClickListener);
    }
    
    private void initWebView() {
        webviewQuestion.getSettings().setSupportZoom(true);
        webviewQuestion.getSettings().setBuiltInZoomControls(false);
        webviewQuestion.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webviewQuestion.setScrollbarFadingEnabled(true);
        webviewQuestion.getSettings().setLoadsImagesAutomatically(true);
        webviewQuestion.getSettings().setJavaScriptEnabled(true);

        if(getExternalCacheDir() != null) {
            webviewQuestion.getSettings().setAppCachePath(getExternalCacheDir().getAbsolutePath());
        } else {
            webviewQuestion.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        }
        webviewQuestion.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);

        webviewQuestion.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                L.info("JS return value " + message);
                showToast("Adding '"+message+"' to the notes");
                result.confirm();
                return true;
            }
        });
        webviewQuestion.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void test() {
                L.debug("JS", "test");
            }

            @JavascriptInterface
            public void onData(String value) {
                L.info("JS data" + value);
            }
        }, "android");
        // Load the URLs inside the WebView, not in the external web browser
        webviewQuestion.setWebViewClient(new MyWebViewClient());
    }

    @Override
    public void onEvent(Integer position) {
        super.onEvent(position);
        selectedPosition = position;
        if(WebActivity.exerciseModelList  != null && WebActivity.exerciseModelList.size() > 0) {
            loadQuestion(position);
        }
    }

    private void navigateButtonEnabled() {
        if(selectedPosition == 0) {
            btnPrevious.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            return;
        }
        if(selectedPosition == WebActivity.exerciseModelList.size() - 1) {
            btnPrevious.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            return;
        }
        btnPrevious.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_next:
                    showExerciseToolbar(null, selectedPosition + 1, true);
                    break;
                case R.id.btn_previous:
                    showExerciseToolbar(null, selectedPosition - 1, true);
                    break;
            }
        }
    };

    private void loadQuestion(int position) {

        String header = "Exercise: " + WebActivity.exerciseModelList.get(position).displayName.split("\\s+")[0];
        SpannableString headerText = new SpannableString(header);
        headerText.setSpan(new UnderlineSpan(), 0, header.length(), 0);
        tvHeader.setText(headerText);


        if(WebActivity.exerciseModelList.get(position).paragraphHtml != null) {
            webQuestion = WebActivity.exerciseModelList.get(position).paragraphHtml;
        }
        if(WebActivity.exerciseModelList.get(position).questionHtml != null) {
            webQuestion = webQuestion + WebActivity.exerciseModelList.get(position).questionHtml;
            webviewQuestion.loadData(webQuestion, "text/html; charset=UTF-8", null);
        }
        if(WebActivity.exerciseModelList.get(position).comment != null) {
            tvComment.setText(WebActivity.exerciseModelList.get(position).comment);
            tvComment.setVisibility(View.VISIBLE);
        } else {
            tvComment.setVisibility(View.GONE);
        }

        navigateButtonEnabled();
        if (mViewSwitcher.indexOfChild(mViewSwitcher.getCurrentView()) == 0) {
            mViewSwitcher.showNext();
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("staging.corsalite.com")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }


}
