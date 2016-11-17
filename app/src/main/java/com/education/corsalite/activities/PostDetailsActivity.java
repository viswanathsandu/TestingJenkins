package com.education.corsalite.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.listener.CommentEventsListener;
import com.education.corsalite.listener.SocialEventsListener;
import com.education.corsalite.models.requestmodels.Bookmark;
import com.education.corsalite.models.requestmodels.ForumLikeRequest;
import com.education.corsalite.models.responsemodels.CommonResponseModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ForumPost;
import com.education.corsalite.models.responsemodels.FourmCommentPostModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by Madhuri on 20-04-2016.
 */
public class PostDetailsActivity extends AbstractBaseActivity implements CommentEventsListener, SocialEventsListener {

    @Bind(R.id.rcv_comments)
    RecyclerView commentsRecyclerView;
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
        toolbar.findViewById(R.id.comment_btn).setOnClickListener(new View.OnClickListener() {
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
        setUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPostDetails(userId, postId);
    }

    private void setUI() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(commentsRecyclerView.getContext());
        commentsRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new PostCommentsAdapter(this, this, this);
        commentsRecyclerView.setAdapter(adapter);
    }

    private void getPostDetails(final String userId, final String postId) {
        showProgress();
        ApiManager.getInstance(this).getPostDetails(userId, postId, new ApiCallback<FourmCommentPostModel>(this) {
            @Override
            public void success(FourmCommentPostModel commentPostModel, Response response) {
                super.success(commentPostModel, response);
                closeProgress();
                if (commentPostModel == null) {
                    return;
                }
                post = commentPostModel.post;
                if (post != null && commentPostModel.commentPosts != null) {
                    adapter.setForumPostList(commentPostModel);
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
    public void onLikeClicked(final int position) {
        showProgress();
        ApiManager.getInstance(this).addForumLike(new ForumLikeRequest(appPref.getUserId(), post.idUserPost),
                new ApiCallback<CommonResponseModel>(this) {
                    @Override
                    public void success(CommonResponseModel baseResponseModel, Response response) {
                        super.success(baseResponseModel, response);
                        closeProgress();
                        getPostDetails(userId, postId);
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                    }
                });
    }

    @Override
    public void onBookmarkClicked(final int position) {
        Bookmark bookmark = new Bookmark();
        final String bookmarkDelete = (post.bookmark == null || (post.bookmark != null && post.bookmark.equalsIgnoreCase("N"))) ? "Y" : "N";
        bookmark.bookmarkdelete = bookmarkDelete;
        bookmark.idUserPost = post.idUserPost;
        bookmark.idUser = appPref.getUserId();
        showProgress();
        ApiManager.getInstance(this).postBookmark(bookmark, new ApiCallback<CommonResponseModel>(this) {
            @Override
            public void success(CommonResponseModel bookmarkResponse, Response response) {
                super.success(bookmarkResponse, response);
                closeProgress();
                if (bookmarkResponse.isSuccessful()) {
                    post.bookmark = bookmarkDelete;
                    if (bookmarkDelete.equalsIgnoreCase("Y")) {
                        showToast("Post is successfully bookmarked.");
                    } else {
                        showToast("Removed bookmark successfully.");
                    }
                    getPostDetails(userId, postId);
                }
            }

            @Override
            public void failure(CorsaliteError error) {
                super.failure(error);
                closeProgress();
            }
        });
    }

    @Override
    public void onEditClicked(int position) {
        ForumPost forumPost = post;
        Bundle bundle = new Bundle();
        bundle.putString("type", "Forum");
        bundle.putString("operation", "Edit");
        bundle.putString("post_id", forumPost.idUserPost);
        bundle.putString("student_id", LoginUserCache.getInstance().getLoginResponse().studentId);
        bundle.putString("subject_id", forumPost.idCourseSubject);
        bundle.putString("chapter_id", forumPost.idCourseSubjectChapter);
        bundle.putString("topic_id", forumPost.idTopic);
        bundle.putString("post_subject", forumPost.PostSubject);
        bundle.putString("content", forumPost.htmlText);
        bundle.putString("is_author_only", forumPost.isAuthorOnly);
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onLockClicked(int position) {
    }

    @Override
    public void onDeleteClicked(final int position) {
        final ForumPost forumPost = post;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm");
        alert.setMessage("Do you want to delete the post?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePost(forumPost, position);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void deletePost(ForumPost forumPost, int position) {
        showProgress();
        ApiManager.getInstance(this).deleteForum(new ForumLikeRequest(forumPost.idUser, forumPost.idUserPost),
                new ApiCallback<CommonResponseModel>(this) {
                    @Override
                    public void success(CommonResponseModel baseResponseModel, Response response) {
                        super.success(baseResponseModel, response);
                        closeProgress();
                        showToast("Post deleted successfully");
                        finish();
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                        showToast("Post couldn't be deleted. Please try again");
                    }
                });
    }

    @Override
    public void onCommentClicked(int position) {
        // DO nothing as adding coments is handled through add comment button
    }
}

