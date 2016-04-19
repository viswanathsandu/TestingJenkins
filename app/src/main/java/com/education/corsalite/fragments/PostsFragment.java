package com.education.corsalite.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.activities.EditorActivity;
import com.education.corsalite.activities.ForumActivity;
import com.education.corsalite.adapters.PostAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.listener.SocialEventsListener;
import com.education.corsalite.models.requestmodels.Bookmark;
import com.education.corsalite.models.requestmodels.ForumLikeRequest;
import com.education.corsalite.models.responsemodels.BookmarkResponse;
import com.education.corsalite.models.responsemodels.CommonResponseModel;
import com.education.corsalite.models.responsemodels.CorsaliteError;
import com.education.corsalite.models.responsemodels.ForumPost;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by sridharnalam on 1/8/16.
 */
public class PostsFragment extends BaseFragment implements SocialEventsListener, View.OnClickListener {
    public static final String MEAL_TYPE_ARG = "MEAL_TYPE_ARG";

    private int mPage;
    @Bind(R.id.rcv_posts)
    RecyclerView mRecyclerView;
    @Bind(R.id.empty_layout)
    View emptyLayout;
    @Bind(R.id.new_post_btn)
    Button newPostBtn;

    private LinearLayoutManager mLayoutManager;
    private PostAdapter mPostAdapter;

    public static PostsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(MEAL_TYPE_ARG, page);
        PostsFragment fragment = new PostsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(MEAL_TYPE_ARG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        ButterKnife.bind(this, view);
        setUI();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void setUI() {
        newPostBtn.setOnClickListener(this);
        mLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mPostAdapter = new PostAdapter(getActivity(), this, mPage);
        mRecyclerView.setAdapter(mPostAdapter);
    }

    private void refreshData() {
        switch (mPage) {
            case 0:
                loadForumPosts();
                break;
            case 1:
                loadForumMyPosts();
                break;
            case 2:
                loadForumLibrary();
                break;
            default:
                break;
        }
    }

    private void loadForumLibrary() {
        showProgress();
        ApiManager.getInstance(getActivity()).getMyComments(AbstractBaseActivity.selectedCourse.courseId + "", LoginUserCache.getInstance().loginResponse.userId, "forumLibrary",
                new ApiCallback<ArrayList<ForumPost>>(getActivity()) {
                    @Override
                    public void success(ArrayList<ForumPost> forumPosts, Response response) {
                        super.success(forumPosts, response);
                        closeProgress();
                        if (forumPosts != null && !forumPosts.isEmpty()) {
                            setForumPosts(forumPosts);
                        } else {
                            emptyLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void loadForumPosts() {
        showProgress();
        ApiManager.getInstance(getActivity()).getAllPosts(AbstractBaseActivity.selectedCourse.courseId + "", LoginUserCache.getInstance().loginResponse.userId, "AllPosts", "", "",
                new ApiCallback<ArrayList<ForumPost>>(getActivity()) {
                    @Override
                    public void success(ArrayList<ForumPost> forumPosts, Response response) {
                        super.success(forumPosts, response);
                        closeProgress();
                        if (forumPosts != null && !forumPosts.isEmpty()) {
                            setForumPosts(forumPosts);
                        } else {
                            emptyLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void loadForumMyPosts() {
        showProgress();
        ApiManager.getInstance(getActivity()).getMyPosts(AbstractBaseActivity.selectedCourse.courseId + "", LoginUserCache.getInstance().loginResponse.userId,
                new ApiCallback<ArrayList<ForumPost>>(getActivity()) {
                    @Override
                    public void success(ArrayList<ForumPost> forumPosts, Response response) {
                        super.success(forumPosts, response);
                        closeProgress();
                        if (forumPosts != null && !forumPosts.isEmpty()) {
                            setForumPosts(forumPosts);
                        } else {
                            emptyLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void failure(CorsaliteError error) {
                        super.failure(error);
                        closeProgress();
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setForumPosts(List<ForumPost> forumPosts) {
        mPostAdapter.setForumPostList(forumPosts);
    }

    @Override
    public void onLikeClicked(final int position) {
        final ForumPost forumPost = mPostAdapter.getItem(position);

        ApiManager.getInstance(getActivity()).addForumLike(new ForumLikeRequest(forumPost.idUser, forumPost.idUserPost),
                new ApiCallback<CommonResponseModel>(getActivity()) {
                    @Override
                    public void success(CommonResponseModel baseResponseModel, Response response) {
                        super.success(baseResponseModel, response);
                        if (baseResponseModel.isSuccessful()) {
                            forumPost.postLikes = Integer.parseInt(forumPost.postLikes) + 1 + "";
                            forumPost.IsLiked = "Y";
                            mPostAdapter.updateCurrentItem(position);
                        }
                    }
                });
    }

    @Override
    public void onCommentClicked(int position) {
        ForumPost item = mPostAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString("type", "Comment");
        bundle.putString("operation", "Add");
        bundle.putString("course_id",item.idCourse);
        bundle.putString("subject_id",item.idCourseSubject);
        bundle.putString("chapter_id", item.idCourseSubjectChapter);
        bundle.putString("topic_id", item.idTopic);
        bundle.putString("post_subject", item.PostSubject);
        bundle.putString("post_id", item.idUserPost);
        bundle.putString("is_author_only", item.isAuthorOnly);
        Intent intent = new Intent(getActivity(), EditorActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBookmarkClicked(final int position) {
        final ForumPost forumPost = mPostAdapter.getItem(position);
        final Bookmark bookmark = new Bookmark();
        if (forumPost.bookmark == null || (forumPost.bookmark != null && forumPost.bookmark.equalsIgnoreCase("N"))) {
            bookmark.bookmarkdelete = "Y";
        }else {
            bookmark.bookmarkdelete = "N";
        }
        bookmark.idUserPost = forumPost.idUserPost;
        bookmark.idUser = LoginUserCache.getInstance().loginResponse.userId;
        ApiManager.getInstance(getActivity()).postBookmark(bookmark, new ApiCallback<BookmarkResponse>(getActivity()) {
            @Override
            public void success(BookmarkResponse bookmarkResponse, Response response) {
                super.success(bookmarkResponse, response);
                if(bookmarkResponse.isSuccessful()){
                    forumPost.bookmark = bookmark.bookmarkdelete;
                    if (bookmark.bookmarkdelete.equalsIgnoreCase("Y")) {
                        Toast.makeText(getActivity(), "Post is successfully bookmarked.", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(), "Removed bookmark successfully.", Toast.LENGTH_SHORT).show();
                    }
                    mPostAdapter.updateCurrentItem(position);
                }
            }
        });
    }

    @Override
    public void onEditClicked(int position) {
        ForumPost forumPost = mPostAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString("type", "Forum");
        bundle.putString("operation", "Edit");
        bundle.putString("post_id", forumPost.idUserPost);
        bundle.putString("student_id", LoginUserCache.getInstance().getLongResponse().studentId);
        bundle.putString("subject_id", forumPost.idCourseSubject);
        bundle.putString("chapter_id", forumPost.idCourseSubjectChapter);
        bundle.putString("topic_id", forumPost.idTopic);
        bundle.putString("post_subject", forumPost.PostSubject);
        bundle.putString("content", forumPost.htmlText);
        bundle.putString("is_author_only", forumPost.isAuthorOnly);
        Intent intent = new Intent(getActivity(), EditorActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onLockClicked(int position) {
    }

    @Override
    public void onDeleteClicked(final int position) {
        final ForumPost forumPost = mPostAdapter.getItem(position);
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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

    private void deletePost(ForumPost forumPost, final int position) {
        ApiManager.getInstance(getActivity()).deleteForum(new ForumLikeRequest(forumPost.idUser, forumPost.idUserPost),
                new ApiCallback<CommonResponseModel>(getActivity()) {
                    @Override
                    public void success(CommonResponseModel baseResponseModel, Response response) {
                        super.success(baseResponseModel, response);
                        if (baseResponseModel.isSuccessful()) {
                            mPostAdapter.deleteForumPost(position);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_post_btn) {
            if (getActivity() instanceof ForumActivity) {
                ((ForumActivity) getActivity()).onNewPostClicked();
            }
        }
    }

}