package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.responsemodels.Course;
import com.education.corsalite.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Girish on 21/10/15.
 */
public class VideoActivity extends AbstractBaseActivity {

    @Bind(R.id.wv_video)
    WebView wvVideo;

    List<ContentModel> mContentModels;
    int selectedPosition = -1;

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
            setToolbarForVideo(mContentModels, selectedPosition);
        }
    }

    @Override
    public void onEvent(int position) {
        loadWeb(position);
    }

    @Override
    public void onEvent(Course course) {
        super.onEvent(course);
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
            if (Uri.parse(url).getHost().equals("staging.corsalite.com")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    private void loadWeb(int selectedPosition) {
        // Initialize the WebView
        wvVideo.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        wvVideo.loadUrl(Constants.VIDEO_PREFIX_URL+"/files/topics/370/1_Introduction_to_function_inverses.mp4");
    }
}
