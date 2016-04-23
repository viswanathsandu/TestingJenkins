package com.education.corsalite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.education.corsalite.R;
import com.education.corsalite.adapters.PostCommentsAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.listener.CommentEventsListener;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ForumPost;
import com.education.corsalite.models.responsemodels.FourmCommentPostModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Madhuri on 20-04-2016.
 */
public class PostDetailsActivity extends AbstractBaseActivity implements CommentEventsListener {

    @Bind(R.id.rcv_comments) RecyclerView commentsRecyclerView;
    private String postId;
    private String userId;
    private PostCommentsAdapter adapter;
    private ForumPost post;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout myView = (LinearLayout) inflater.inflate(R.layout.activity_post_comments, null);
        frameLayout.addView(myView);
        ButterKnife.bind(this, myView);
        setToolbarForPostcomments();
        toolbar.findViewById(R.id.new_post_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNewCommentClicked();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("user_id", "");
            postId = bundle.getString("post_id", "");
        }
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(postId)) {
            showToast("Invalid post");
            finish();
            return;
        }
        getPostDetails(userId, postId);
        setUI();
    }

    private void setUI() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(commentsRecyclerView.getContext());
        commentsRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new PostCommentsAdapter(new ArrayList<ForumPost>(), this);
        commentsRecyclerView.setAdapter(adapter);
    }

    private void getPostDetails(final String userId, final String postId) {
        showProgress();
        ApiManager.getInstance(this).getPostDetails(userId, postId, new ApiCallback<FourmCommentPostModel>(this) {
            @Override
            public void success(FourmCommentPostModel commentPostModel, Response response) {
                super.success(commentPostModel, response);
                closeProgress();
                if(commentPostModel == null) {
                    return;
                }
                post = commentPostModel.post;
                if(post != null && commentPostModel.commentPosts != null) {
                    adapter.setForumPostList(commentPostModel.commentPosts);
                }
            }

            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                closeProgress();
                showToast("Something went wrong");
            }
        });
    }

    public void onNewCommentClicked() {
        ForumPost item = post;
        Bundle bundle = new Bundle();
        bundle.putString("type", "Comment");
        bundle.putString("operation", "Add");
        bundle.putString("course_id", item.idCourse);
        bundle.putString("subject_id", item.idCourseSubject);
        bundle.putString("chapter_id", item.idCourseSubjectChapter);
        bundle.putString("topic_id", item.idTopic);
        bundle.putString("post_subject", item.PostSubject);
        bundle.putString("post_id", item.idUserPost);
        bundle.putString("is_author_only", item.isAuthorOnly);
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onEditClicked(int position) {

    }

    @Override
    public void onDeleteClicked(int position) {

    }
}

