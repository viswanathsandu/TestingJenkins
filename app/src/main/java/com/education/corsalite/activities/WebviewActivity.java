package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.education.corsalite.R;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.L;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebviewActivity extends AbstractBaseActivity {

    private final String URL = "URL";
    @Bind(R.id.webview) WebView webview;
    @Bind(R.id.login_webview) WebView loginWebview;
    @Bind(R.id.progress_bar_tab)ProgressBar mProgressBar;

    private String pageUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_webview, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle.containsKey("clear_cookies")) {
            webview.clearCache(true);
            loginWebview.clearCache(true);;
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();
        }
        String title = bundle.getString(LoginActivity.TITLE, "Corsalite");
        setToolbarForWebActivity(title);
        if(title.equals(getString(R.string.forgot_password)) || title.equals(getString(R.string.computer_adaptive_test))) {
            setDrawerIconInvisible();
            lockScreenOrientation();
        }
        pageUrl = bundle.getString(URL);
        if(!title.equals(getString(R.string.forgot_password))) {
            loginIntoWebview();
        } else {
            loadWebpage();
        }
        sendAnalytics(getString(R.string.screen_webview));
    }

    private void loadWebpage() {
        if (!TextUtils.isEmpty(pageUrl)) {
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new MyWebViewClient());
            webview.loadUrl(getUrlWithNoHeadersAndFooters(pageUrl));
            L.info("Load Url : "+getUrlWithNoHeadersAndFooters(pageUrl));
        }
    }

    // Call this method before calling any url that needs authentication
    private void loginIntoWebview() {
        loginWebview.getSettings().setJavaScriptEnabled(true);
        loginWebview.setWebViewClient(new LoginWebViewClient());
        loginWebview.loadUrl(getLoginUrl());
        L.info("Load Url : "+getLoginUrl());
    }

    private String getLoginUrl() {
        String loginUrl = String.format("%swebservices/AuthToken?LoginID=%s&PasswordHash=%s",
                                        ApiClientService.getBaseUrl(),
                                        appPref.getValue("loginId"),
                                        appPref.getValue("passwordHash"));
        return loginUrl;
    }

    @Override
    public void onBackPressed() {
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
            if (url.contains(ApiClientService.getBaseUrl())) {
                if(!url.contains("?") || !url.contains("Header=0&Footer=0")) {
                    view.loadUrl(getUrlWithNoHeadersAndFooters(url));
                }
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            L.info("Finished Loading URL : "+url);
            mProgressBar.setVisibility(View.GONE);
            webview.setVisibility(View.VISIBLE);
        }
    }

    private class LoginWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            L.info("Login page loading completed in webview");
            loadWebpage();
        }
    }

    private void lockScreenOrientation(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}
