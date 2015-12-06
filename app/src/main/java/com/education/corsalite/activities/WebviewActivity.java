package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.education.corsalite.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebviewActivity extends AbstractBaseActivity {

    private final String URL = "URL";
    @Bind(R.id.webview)
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_webview, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle.containsKey("clear_cookies")) {
            webview.clearCache(true);
        }
        String title = bundle.getString(LoginActivity.TITLE, "Corsalite");
        setToolbarForWebActivity(title);
        if(title.equals(getString(R.string.forgot_password))){

            setDrawerIconInvisible();
        }
        if (bundle.containsKey(URL)) {
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new MyWebViewClient());
            String url = bundle.getString(URL);
            webview.loadUrl(getUrlWithNoHeadersAndFooters(url));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            finish();
        }
    }

    private String getUrlWithNoHeadersAndFooters(String url) {
        if(!url.contains("?")) {
            url += "?Header=0&Footer=0";
        } else if(!url.contains("Header=0&Footer=0")) {
            url += "Header=0&Footer=0";
        }
        return url;
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("staging.corsalite.com")) {
                if(!url.contains("?") || !url.contains("Header=0&Footer=0")) {
                    view.loadUrl(getUrlWithNoHeadersAndFooters(url));
                }
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
}
