package com.education.corsalite.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.education.corsalite.R;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.WebUrls;
import com.education.corsalite.views.CorsaliteWebViewClient;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebviewActivity extends AbstractBaseActivity {

    private final String URL = "URL";
    @Bind(R.id.webview)
    WebView webview;
    @Bind(R.id.login_webview)
    WebView loginWebview;
    @Bind(R.id.progress_bar_tab)
    ProgressBar mProgressBar;
    private boolean handleBackClick = false;
    private String pageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_webview, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("clear_cookies")) {
            webview.clearCache(true);
            loginWebview.clearCache(true);
            ;
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();
        }
        String title = bundle.getString(LoginActivity.TITLE, "Corsalite");
        setToolbarForWebActivity(title);
        if (title.equals(getString(R.string.forgot_password)) || title.equals(getString(R.string.computer_adaptive_test))) {
            hideDrawerIcon();
        }
        pageUrl = bundle.getString(URL);
        if (!title.equals(getString(R.string.forgot_password))) {
            loginIntoWebview();
        } else {
            loadWebpage();
        }
        sendAnalytics(getString(R.string.screen_webview));
    }

    private void loadWebpage() {
        if (!TextUtils.isEmpty(pageUrl)) {
            webview.getSettings().setJavaScriptEnabled(true);
            webview.setWebViewClient(new MyWebViewClient(this));
//            webview.setWebChromeClient(new WebChromeClient());
            webview.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
                {
                    new AlertDialog.Builder(WebviewActivity.this)
                            .setTitle(view.getTitle())
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            result.confirm();
                                        }
                                    })
                            .setCancelable(false)
                            .create()
                            .show();

                    return true;
                };

                @Override
                public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                    AlertDialog.Builder b = new AlertDialog.Builder(view.getContext())
                            .setTitle(view.getTitle())
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    result.cancel();
                                }
                            });

                    b.show();

                    // Indicate that we're handling this manually
                    return true;
                }
            });
            webview.loadUrl(getUrlWithNoHeadersAndFooters(pageUrl));
            L.info("Load Url : " + getUrlWithNoHeadersAndFooters(pageUrl));
        }
    }

    // Call this method before calling any url that needs authentication
    private void loginIntoWebview() {
        loginWebview.getSettings().setJavaScriptEnabled(true);
        loginWebview.setWebViewClient(new LoginWebViewClient());
        loginWebview.loadUrl(getLoginUrl());
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
        if (webview.canGoBack() && !handleBackClick) {
            webview.goBack();
        } else {
            finish();
        }
    }

    private String getUrlWithNoHeadersAndFooters(String url) {
        if (!url.contains("?")) {
            url += "?Header=0&Footer=0&tabCourseId="+AbstractBaseActivity.getSelectedCourseId();
        } else if (!url.contains("Header=0&Footer=0")) {
            url += "Header=0&Footer=0&tabCourseId="+AbstractBaseActivity.getSelectedCourseId();
        }
        return url;
    }

    private class MyWebViewClient extends CorsaliteWebViewClient {

        public MyWebViewClient(Context context) {
            super(context);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (checkNetconnection(view, url)) {
                return true;
            }
            Intent intent = WebUrls.getIntent(WebviewActivity.this, url);
            if (intent != null) {
                startActivity(intent);
                finish();
                return true;
            } else if (url.contains(ApiClientService.getBaseUrl()) && !WebUrls.isHandler(url)) {
                if (!url.contains("?") || !url.contains("Header=0&Footer=0")) {
                    view.loadUrl(getUrlWithNoHeadersAndFooters(url));
                }
                return false;
            }
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (!checkNetconnection(view, url)) {
                handleBackClick = url.contains("examination/examSubmit");
                if(handleBackClick) {
                    showDrawerIcon();
                    view.clearHistory();
                }
                super.onPageFinished(view, url);
                L.info("Finished Loading URL : " + url);
                mProgressBar.setVisibility(View.GONE);
                webview.setVisibility(View.VISIBLE);
            }
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
}
