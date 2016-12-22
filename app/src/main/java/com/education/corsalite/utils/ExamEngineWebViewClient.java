package com.education.corsalite.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import com.education.corsalite.BuildConfig;
import com.education.corsalite.views.CorsaliteWebViewClient;

import java.net.URL;

/**
 * Created by vissu on 12/21/16.
 */

public class ExamEngineWebViewClient extends CorsaliteWebViewClient {

    public ExamEngineWebViewClient(Context context) {
        super(context);
    }

    // TODO : override other method
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        try {
            if (checkNetconnection(view, url)) {
                return true;
            }
            URL appBaseUrl = new URL(BuildConfig.BASE_URL);
            if (Uri.parse(url).getHost().equals(appBaseUrl.getHost())) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return false;
        }
    }
}
