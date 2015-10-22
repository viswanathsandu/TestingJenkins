package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Girish on 21/10/15.
 */
public class VideoActivity extends AbstractBaseActivity {

    @Bind(R.id.vs_container) ViewSwitcher viewSwitcher;
    @Bind(R.id.wv_video) WebView wvVideo;

    List<ContentModel> mContentModels;
    int selectedPosition = -1;
    List<Content> contents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_videoview, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        initWebView();
        if(getIntent().hasExtra("selectedPosition")) {
            selectedPosition = getIntent().getExtras().getInt("selectedPosition");
        }
        if(getIntent().hasExtra("videoList")) {
            mContentModels = (List<ContentModel>)getIntent().getExtras().getSerializable("videoList");
        }
        if(selectedPosition >= 0) {
            getContent();
            setToolbarForVideo(mContentModels, selectedPosition);
        }
    }

    @Override
    public void onEvent(Integer position) {
        super.onEvent(position);
        if(contents != null) {
            loadWeb(position);
        }
    }

    private void initWebView() {
        wvVideo.getSettings().setSupportZoom(true);
        wvVideo.getSettings().setBuiltInZoomControls(true);
        wvVideo.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wvVideo.setScrollbarFadingEnabled(true);
        wvVideo.getSettings().setLoadsImagesAutomatically(true);
        wvVideo.getSettings().setJavaScriptEnabled(true);

        if(getExternalCacheDir() != null) {
            wvVideo.getSettings().setAppCachePath(getExternalCacheDir().getAbsolutePath());
        } else {
            wvVideo.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        }
        wvVideo.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);

        // Load the URLs inside the WebView, not in the external web browser
        wvVideo.setWebViewClient(new MyWebViewClient());
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().contains("corsalite.com")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    private void loadWeb(int selectedPosition) {
        // Initialize the WebView
        wvVideo.loadUrl(Constants.VIDEO_PREFIX_URL + contents.get(selectedPosition).url.replace("./", ""));
    }

    private void getContent() {
        String contentId = "";
        for(ContentModel contentModel : mContentModels) {
            if(contentId.trim().length() > 0) {
                contentId = contentId + ",";
            }
            contentId = contentId + contentModel.idContent;
        }
        ApiManager.getInstance(this).getContent(contentId, "", new ApiCallback<List<Content>>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                if (viewSwitcher.indexOfChild(viewSwitcher.getCurrentView()) == 0) {
                    viewSwitcher.showNext();
                }
            }

            @Override
            public void success(List<Content> contentList, Response response) {
                super.success(contents, response);
                contents = contentList;
                if (viewSwitcher.indexOfChild(viewSwitcher.getCurrentView()) == 0) {
                    viewSwitcher.showNext();
                }
                onEvent(selectedPosition);
            }
        });
    }
}
