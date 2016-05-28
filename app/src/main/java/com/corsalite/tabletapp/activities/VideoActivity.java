package com.corsalite.tabletapp.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.ViewSwitcher;

import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.api.ApiCallback;
import com.corsalite.tabletapp.api.ApiManager;
import com.corsalite.tabletapp.models.ContentModel;
import com.corsalite.tabletapp.models.responsemodels.Content;
import com.corsalite.tabletapp.models.responsemodels.CorsaliteError;
import com.corsalite.tabletapp.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Girish on 21/10/15.
 */
public class VideoActivity extends AbstractBaseActivity {

    @Bind(R.id.vs_container) ViewSwitcher viewSwitcher;
    @Bind(R.id.videoViewRelative) VideoView videoViewRelative;

    List<ContentModel> mContentModels;
    int selectedPosition = 0;
    List<Content> contents;
    private String videoPath;

    private MediaController mediaControls;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_videoview, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        initVideoView();
        if(getIntent().hasExtra("selectedPosition")) {
            selectedPosition = getIntent().getExtras().getInt("selectedPosition");
        }
        if(getIntent().hasExtra("videoList")) {
            mContentModels = (List<ContentModel>)getIntent().getExtras().getSerializable("videoList");
        }

        if(getIntent().hasExtra("videopath")){
            videoPath = getIntent().getStringExtra("videopath");
            loadLocalVideo();
        }
        if(selectedPosition >= 0 && mContentModels != null) {
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

    private void initVideoView() {
        //set the media controller buttons
        if (mediaControls == null) {
            mediaControls = new MediaController(this);
        }
        videoViewRelative.setMediaController(mediaControls);
    }

    private void loadWeb(final int selectedPosition) {
        // Initialize the WebView
        try {
            //set the uri of the video to be played
            videoViewRelative.setVideoURI(Uri.parse(Constants.VIDEO_PREFIX_URL + contents.get(selectedPosition).url.replace("./", "")));
            videoViewRelative.requestFocus();
            //we also set an setOnPreparedListener in order to know when the video file is ready for playback
            videoViewRelative.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mediaPlayer) {
                    // close the progress bar and play the video
                    videoViewRelative.seekTo(selectedPosition);
                    if (selectedPosition == 0) {
                        videoViewRelative.start();
                    } else {
                        //if we come from a resumed activity, video playback will be paused
                        videoViewRelative.pause();
                    }
                }
            });
            if (viewSwitcher.indexOfChild(viewSwitcher.getCurrentView()) == 0) {
                viewSwitcher.showNext();
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        selectedPosition = videoViewRelative.getCurrentPosition();
        if(videoViewRelative != null && videoViewRelative.isPlaying()) {
            videoViewRelative.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(videoViewRelative != null && selectedPosition > 0) {
            videoViewRelative.seekTo(selectedPosition);
            videoViewRelative.pause();
        }
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

    private void loadLocalVideo(){
        videoViewRelative.setVideoPath(videoPath);
        videoViewRelative.start();
    }
}
