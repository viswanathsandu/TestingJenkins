package com.education.corsalite.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.education.corsalite.R;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.AppPref;
import com.education.corsalite.utils.L;

/**
 * Created by madhuri on 4/23/16.
 */
public class DetailsWebviewFragment extends Fragment {

    private String mUrl = null;
    private AppPref appPref;
    private final String URL = "URL";
    private WebView mWebView;
    private WebView loginWebview;

    public DetailsWebviewFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout myView = (RelativeLayout) inflater.inflate(R.layout.activity_webview, null, false);
        mWebView = (WebView) myView.findViewById(R.id.webview);
        loginWebview = (WebView) myView.findViewById(R.id.login_webview);
        mUrl = getArguments().getString("URL");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mUrl);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            L.info("Finished Loading URL : " + url);
            mWebView.setVisibility(View.VISIBLE);
        }
    }
}
