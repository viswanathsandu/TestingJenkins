package com.education.corsalite.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.education.corsalite.R;
import com.education.corsalite.activities.AbstractBaseActivity;
import com.education.corsalite.adapters.PostAdapter;
import com.education.corsalite.api.ApiCallback;
import com.education.corsalite.api.ApiManager;
import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.listener.SocialEventsListener;
import com.education.corsalite.models.requestmodels.ForumLikeRequest;
import com.education.corsalite.models.responsemodels.CommonResponseModel;
import com.education.corsalite.models.responsemodels.ForumPost;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.client.Response;

/**
 * Created by sridharnalam on 1/8/16.
 */
public class PostsFragment extends BaseFragment implements SocialEventsListener {
    public static final String MEAL_TYPE_ARG = "MEAL_TYPE_ARG";

    private int mPage;
    @Bind(R.id.rcv_posts)
    RecyclerView mRecyclerView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUI();
    }

    private void setUI() {
        mLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mPostAdapter = new PostAdapter(getActivity(), this, mPage);
        mRecyclerView.setAdapter(mPostAdapter);
        switch (mPage){
            case 0:
                loadForumPosts();
                break;
            case 1:
                loadForumMyPosts();
                break;
            case 2:
                loadForumMyComments();
                break;
            default:
                break;
        }
    }

    private void loadForumMyComments() {
        ApiManager.getInstance(getActivity()).getMyComments(AbstractBaseActivity.selectedCourse.courseId+"", LoginUserCache.getInstance().loginResponse.userId, "MyComments",
                new ApiCallback<ArrayList<ForumPost>>(getActivity()) {
                    @Override
                    public void success(ArrayList<ForumPost> forumPosts, Response response) {
                        super.success(forumPosts, response);
                        setForumPosts(forumPosts);
                    }
                });
    }

    private void loadForumPosts() {
        ApiManager.getInstance(getActivity()).getAllPosts(AbstractBaseActivity.selectedCourse.courseId+"", LoginUserCache.getInstance().loginResponse.userId, "AllPosts", "", "",
                new ApiCallback<ArrayList<ForumPost>>(getActivity()) {
                    @Override
                    public void success(ArrayList<ForumPost> forumPosts, Response response) {
                        super.success(forumPosts, response);
                        setForumPosts(forumPosts);
                    }
                });
    }

    private void loadForumMyPosts() {
        // http://staging.corsalite.com/v1/webservices/Forums?idCourse=17&idUser=69
        ApiManager.getInstance(getActivity()).getMyPosts("17", "69",
                new ApiCallback<ArrayList<ForumPost>>(getActivity()) {
                    @Override
                    public void success(ArrayList<ForumPost> forumPosts, Response response) {
                        super.success(forumPosts, response);
                        setForumPosts(forumPosts);
                    }
                });
    }

    private void setForumPosts(List<ForumPost> forumPosts) {
        mPostAdapter.setForumPostList(forumPosts);
    }

    @Override
    public void onLikeClicked(final int position) {
        final ForumPost forumPost=mPostAdapter.getItem(position);

        ApiManager.getInstance(getActivity()).addForumLike(new ForumLikeRequest(forumPost.idUser, forumPost.idUserPost),
                            new ApiCallback<CommonResponseModel>(getActivity()) {
            @Override
            public void success(CommonResponseModel baseResponseModel, Response response) {
                super.success(baseResponseModel, response);
                if(baseResponseModel.isSuccessful()){
                   forumPost.postLikes = Integer.parseInt(forumPost.postLikes) + 1 + "";
                    mPostAdapter.updateCurrentItem(position);
                }
            }
        });
    }

    @Override
    public void onCommentClicked(int position) {
    }

    @Override
    public void onBookmarkClicked(int position) {
    }

    @Override
    public void onEditClicked(int position) {
        ForumPost forumPost = mPostAdapter.getItem(position);
        EditorDialogFragment fragment = new EditorDialogFragment();
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
        fragment.setArguments(bundle);
        fragment.show(getActivity().getSupportFragmentManager(), "ForumEditorDialog");
    }

    @Override
    public void onLockClicked(int position) {
    }

    @Override
    public void onDeleteClicked(final int position) {
        final ForumPost forumPost = mPostAdapter.getItem(position);

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
}