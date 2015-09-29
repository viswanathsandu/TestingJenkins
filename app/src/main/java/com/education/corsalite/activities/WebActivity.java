package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Girish on 28/09/15.
 */
public class WebActivity extends AbstractBaseActivity {

    private final String URL = "URL";
    @Bind(R.id.subject1) TextView tvSubject1;
    @Bind(R.id.subject2) TextView tvSubject2;
    @Bind(R.id.subject3) TextView tvSubject3;
    @Bind(R.id.exercise) TextView tvExercise;

    @Bind(R.id.iv_editnotes) ImageView ivEditNotes;
    @Bind(R.id.iv_forum) ImageView ivForum;

    @Bind(R.id.webView_content_reading) WebView webviewContentReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_web, null);
        frameLayout.addView(myView);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle.containsKey("clear_cookies")) {
            webviewContentReading.clearCache(true);
        }
        if (bundle.containsKey(URL)) {
            webviewContentReading.getSettings().setJavaScriptEnabled(true);
            webviewContentReading.setWebViewClient(new MyWebViewClient());
            webviewContentReading.loadUrl(bundle.getString(URL));
        }
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content_reading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_read_offline:
                showToast("Read Offline");
                return true;

            case R.id.action_download_pdf:
                showToast("Download PDF");
                return true;

            case R.id.action_view_notes:
                showToast("View Notes");
                return true;

            case R.id.action_rate_it:
                showToast("Rate It");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webviewContentReading.canGoBack()) {
            webviewContentReading.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void setListeners() {
        tvSubject1.setOnClickListener(mClickListener);
        tvSubject2.setOnClickListener(mClickListener);
        tvSubject3.setOnClickListener(mClickListener);
        tvExercise.setOnClickListener(mClickListener);
        ivEditNotes.setOnClickListener(mClickListener);
        ivForum.setOnClickListener(mClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.subject1:
                case R.id.subject2:
                case R.id.subject3:
                    break;

                case R.id.exercise:
                    break;

                case R.id.iv_forum:
                    showToast("Forum button is clicked");
                    break;

                case R.id.iv_editnotes:
                    showToast("Add is clicked");
                    break;
            }
        }
    };

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