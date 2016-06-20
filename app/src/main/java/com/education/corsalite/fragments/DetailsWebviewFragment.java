package com.education.corsalite.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.L;
import com.education.corsalite.views.CorsaliteWebViewClient;

/**
 * Created by madhuri on 4/23/16.
 */
public class DetailsWebviewFragment extends BaseFragment {

    private String mUrl;
    private String mUrlPattern;
    private AppPref appPref;
    private final String URL_PATTERN_EXTRAS = "URL_Pattern";
    private WebView mWebView;
    private WebView loginWebview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_webview, null, false);
        mWebView = (WebView) myView.findViewById(R.id.webview);
        loginWebview = (WebView) myView.findViewById(R.id.login_webview);
        mUrlPattern = getArguments().getString(URL_PATTERN_EXTRAS);
        if (!TextUtils.isEmpty(mUrlPattern)) {
            mUrl = String.format(mUrlPattern, AbstractBaseActivity.getSelectedCourseId());
        }
        appPref = AppPref.getInstance(getActivity());
        loadLoginUrl();
        return myView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    public void onEvent(Course course) {
        if (!TextUtils.isEmpty(mUrlPattern)) {
            mUrl = String.format(mUrlPattern, AbstractBaseActivity.getSelectedCourseId());
            loadLoginUrl();
        }
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    private String getLoginUrl() {
        String loginUrl = String.format("%swebservices/AuthToken?LoginID=%s&PasswordHash=%s",
                ApiClientService.getBaseUrl(),
                appPref.getValue("loginId"),
                appPref.getValue("passwordHash"));
        return loginUrl;
    }

    private void loadLoginUrl() {
        loginWebview.getSettings().setJavaScriptEnabled(true);
        loginWebview.setWebViewClient(new LoginWebViewClient());
        loginWebview.loadUrl(getLoginUrl());
    }

    private class LoginWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loadWebpage();
        }
    }

    private void loadWebpage() {
        if(isAdded()) {
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebViewClient(new MyWebViewClient(getActivity()));
            if (!TextUtils.isEmpty(mUrl)) {
                mWebView.loadUrl(mUrl);
            }
        }
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
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            L.info("Finished Loading URL : " + url);
            if(isAdded()) {
                mWebView.setVisibility(View.VISIBLE);
            }
        }
    }
}
