package com.corsalite.tabletapp.views;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.corsalite.tabletapp.utils.SystemUtils;

/**
 * Created by vissu on 5/24/16.
 */
public class CorsaliteWebViewClient extends WebViewClient {

    private Context mcontext;

    public CorsaliteWebViewClient(Context context) {
        super();
        this.mcontext = context;
    }

    private String noNetConnectionHtml = "<h1 style=\"color: #5e9ca0; text-align: center;\">&nbsp;</h1>\n" +
                                        "<h1 style=\"color: #5e9ca0; text-align: center;\">&nbsp;</h1>\n" +
                                        "<h1 style=\"color: #5e9ca0; text-align: center;\">&nbsp;</h1>\n" +
                                        "<h1 style=\"color: #5e9ca0; text-align: center;\">No Internet Connection</h1>\n" +
                                        "<h3 style=\"text-align: center;\"><span style=\"color: #808080;\">Please Try again later</span></h3>";


    protected boolean checkNetconnection(WebView view, String url) {
        if(url.startsWith("http") &&  mcontext != null && !SystemUtils.isNetworkConnected(mcontext)) {
            view.loadData(noNetConnectionHtml, "text/html", "UTF-8");
            return true;
        }
        return false;
    }
}
