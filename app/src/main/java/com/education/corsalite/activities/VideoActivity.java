package com.education.corsalite.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.ApiCacheHolder;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.ContentModel;
import com.education.corsalite.models.db.OfflineContent;
import com.education.corsalite.models.responsemodels.Content;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.FileUtils;
import com.education.corsalite.utils.SystemUtils;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Girish on 21/10/15.
 */
public class VideoActivity extends AbstractBaseActivity {

    @Bind(R.id.videoViewRelative) VideoView videoViewRelative;
    @Bind(R.id.youtube_container) View youtubeContainer;
    private YouTubePlayerSupportFragment youtubeFragment;

    List<ContentModel> mContentModels;
    long selectedPosition = 0;
    List<Content> contents;
    private String videoPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_videoview, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this);
        youtubeFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtube_fragment);
        if(getIntent().hasExtra("selectedPosition")) {
            selectedPosition = getIntent().getExtras().getInt("selectedPosition");
        }
        if(getIntent().hasExtra("videoList")) {
            mContentModels = (List<ContentModel>)getIntent().getExtras().getSerializable("videoList");
            setToolbarForVideo(mContentModels, (int) selectedPosition);
        }

        if(getIntent().hasExtra("videopath")){
            videoPath = getIntent().getStringExtra("videopath");
            loadLocalVideo();
        } else if (selectedPosition >= 0 && mContentModels != null) {
            getContent();
        }
    }

    @Override
    public void onEvent(Integer position) {
        super.onEvent(position);
        if(contents != null) {
            loadWeb(position);
        }
    }

    private void loadWeb(final int selectedPosition) {
        // Initialize the WebView
        try {
            OfflineContent offlineContent = dbManager.getOfflineContentWithContent(contents.get(selectedPosition).idContent);
            if(offlineContent != null && offlineContent.progress == 100) {
                videoPath = FileUtils.get(this).getVideoDownloadFilePath(offlineContent.contentId);
                loadLocalVideo();
                return;
            } else if(!SystemUtils.isNetworkConnected(this)) {
                showToast("Video is not available for offline");
                return;
            }
            if(contents.get(selectedPosition).url.contains("youtube.com")) {
                videoViewRelative.setVisibility(View.GONE);
                youtubeContainer.setVisibility(View.VISIBLE);
                youtubeFragment.initialize(getString(R.string.google_api_key),
                        new YouTubePlayer.OnInitializedListener() {
                            @Override
                            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                                YouTubePlayer youTubePlayer, boolean b) {
                                // do any work here to cue video, play video, etc.
                                String endPath = contents.get(selectedPosition).url.replaceFirst(".*/([^/?]+).*", "$1");
                                youTubePlayer.cueVideo(endPath);
                            }
                            @Override
                            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                                YouTubeInitializationResult youTubeInitializationResult) {
                                if(youTubeInitializationResult.isUserRecoverableError()) {
                                    youTubeInitializationResult.getErrorDialog(VideoActivity.this, 1).show();
                                } else {
                                    showToast(String.format("Error initializing YouTube player: %s", youTubeInitializationResult.toString()));
                                }
                            }
                        });
            } else {
                videoViewRelative.setVisibility(View.VISIBLE);
                youtubeContainer.setVisibility(View.GONE);
                videoViewRelative.seekTo(0);
                //set the uri of the video to be played
                videoViewRelative.setVideoURI(Uri.parse(ApiClientService.getBaseUrl() + contents.get(selectedPosition).url.replace("./", "")));
                // videoViewRelative.setVideoURI(Uri.parse("http://staging.corsalite.com/stagenewchanges/files/topics/1315/sunil/output.mpd"));
                videoViewRelative.setOnPreparedListener(new OnPreparedListener() {

                    public void onPrepared() {
                        // videoViewRelative.seekTo(selectedPosition);
                        videoViewRelative.start();
                    }
                });
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
        showProgress();
        ApiManager.getInstance(this).getContent(LoginUserCache.getInstance().getStudentId(), contentId, "", new ApiCallback<List<Content>>(this) {
            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                showToast("Sorry. Couldn't fetch video details");
                closeProgress();
                finish();
            }

            @Override
            public void success(List<Content> contentList, Response response) {
                super.success(contents, response);
                closeProgress();
                contents = contentList;
                ApiCacheHolder.getInstance().setContentResponse(contentList);
                dbManager.saveReqRes(ApiCacheHolder.getInstance().contentReqIndex);
                onEvent((int)selectedPosition);
            }
        });
    }

    private void loadLocalVideo(){
        videoViewRelative.setVisibility(View.VISIBLE);
        youtubeContainer.setVisibility(View.GONE);
        videoViewRelative.requestFocus();
        videoViewRelative.setVideoPath(videoPath);
//        videoViewRelative.start();
    }
}
