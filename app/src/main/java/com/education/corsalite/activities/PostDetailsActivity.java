package com.education.corsalite.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.webkit.WebView;

import com.education.corsalite.R;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ForumPost;

import retrofit.client.Response;

/**
 * Created by Madhuri on 20-04-2016.
 */
public class PostDetailsActivity extends AbstractBaseActivity {
    private WebView webview;
    private String postId;
    private String userId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_dialog_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("user_id", "");
            postId = bundle.getString("post_id", "");
        }
        getSupportActionBar().setTitle("Post Details");

        if(userId!=null && !TextUtils.isEmpty(userId) && postId!=null && !TextUtils.isEmpty(postId))
            getPostDetails(userId,postId);

    }


    private void getPostDetails(final String userId, final String postId){
            ApiManager.getInstance(this).getPostDetails(userId, postId, new ApiCallback<ForumPost>(this) {
                @Override
                public void success(ForumPost forumPost, Response response) {
                    super.success(forumPost, response);
                    closeProgress();
                }

                @Override
                public void failure(CorsaliteError error) {
                    super.failure(error);
                    closeProgress();
                }
            });
        }
    }

