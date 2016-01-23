package com.education.corsalite.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.education.corsalite.R;
import com.education.corsalite.listener.SocialEventsListener;
import com.education.corsalite.models.responsemodels.ForumPost;
import com.education.corsalite.services.ApiClientService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sridharnalam on 1/8/16.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private List<ForumPost> mForumPostList;
    private SocialEventsListener mSocialEventsListener;
    private int mPage;

    public PostAdapter(SocialEventsListener listener, int page) {
        mForumPostList = new ArrayList<>();
        mSocialEventsListener = listener;
        mPage = page;
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item_post, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(PostHolder holder, int position) {
        ForumPost forumPost = mForumPostList.get(position);
        if(mPage==0){
            holder.tvActionLike.setVisibility(View.VISIBLE);
            holder.tvActionComment.setVisibility(View.VISIBLE);
            holder.tvActionBookmark.setVisibility(View.VISIBLE);
            holder.tvActionDelete.setVisibility(View.GONE);
            holder.tvActionLock.setVisibility(View.GONE);
            holder.tvActionComment.setText("Comment");
        } else if(mPage==1) {
            holder.tvActionLike.setVisibility(View.GONE);
            holder.tvActionComment.setVisibility(View.VISIBLE);
            holder.tvActionBookmark.setVisibility(View.VISIBLE);
            holder.tvActionDelete.setVisibility(View.VISIBLE);
            holder.tvActionLock.setVisibility(View.VISIBLE);
            holder.tvActionComment.setText("Edit");
        }

        holder.tvQuestion.setText(forumPost.getSearchPost());
        holder.tvDate.setText(forumPost.getDatetime()+" by");
        holder.tvUserName.setText(forumPost.getDisplayName());
        holder.tvQuestionDesc.setText(Html.fromHtml(forumPost.getHtmlText()));
        if(forumPost.getCourseName()==null || forumPost.getCourseName().isEmpty()){
            holder.tvCourseName.setVisibility(View.GONE);
        } else {
            holder.tvCourseName.setVisibility(View.VISIBLE);
            holder.tvCourseName.setText(forumPost.getCourseName());
        }
        if(forumPost.getSubjectName()==null || forumPost.getSubjectName().isEmpty()){
            holder.tvSubjectName.setVisibility(View.GONE);
        } else {
            holder.tvSubjectName.setVisibility(View.VISIBLE);
            holder.tvSubjectName.setText(forumPost.getSubjectName());
        }
        if(forumPost.getChapterName()==null || forumPost.getChapterName().isEmpty()){
            holder.tvChapterName.setVisibility(View.GONE);
        } else {
            holder.tvChapterName.setVisibility(View.VISIBLE);
            holder.tvChapterName.setText(forumPost.getChapterName());
        }
        if(forumPost.getTopicName()==null || forumPost.getTopicName().isEmpty()){
            holder.tvTopicName.setVisibility(View.GONE);
        } else {
            holder.tvTopicName.setVisibility(View.VISIBLE);
            holder.tvTopicName.setText(forumPost.getTopicName());
        }
        holder.tvComments.setText(forumPost.getPostReplies()+" Comments");
        holder.tvLikes.setText(forumPost.getPostLikes()+" Likes");
        holder.tvViews.setText(forumPost.getPostViews()+" Views");

        holder.tvActionBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocialEventsListener.onBookmarkClicked();
            }
        });

        holder.tvActionComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocialEventsListener.onCommentClicked();
            }
        });

        holder.tvActionLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocialEventsListener.onLikeClicked();
            }
        });

        try {
            URL url = new URL(new URL(ApiClientService.getBaseUrl()), forumPost.getPhotoUrl());
            Glide.with(holder.ivUserPic.getContext())
                    .load(url.toString())
                    .centerCrop()
                    .placeholder(R.drawable.ico_time_selected)
                    .crossFade()
                    .into(holder.ivUserPic);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mForumPostList.size();
    }

    public void setForumPostList(List<ForumPost> forumPosts) {
        mForumPostList.clear();
        mForumPostList.addAll(forumPosts);
        notifyDataSetChanged();
    }

    public class PostHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_post_user_pic)
        ImageView ivUserPic;
        @Bind(R.id.tv_post_action_bookmark)
        TextView tvActionBookmark;
        @Bind(R.id.tv_post_action_Comment)
        TextView tvActionComment;
        @Bind(R.id.tv_post_action_lock)
        TextView tvActionLock;
        @Bind(R.id.tv_post_action_delete)
        TextView tvActionDelete;
        @Bind(R.id.tv_post_action_Like)
        TextView tvActionLike;
        @Bind(R.id.tv_post_question)
        TextView tvQuestion;
        @Bind(R.id.tv_post_date)
        TextView tvDate;
        @Bind(R.id.tv_post_user_name)
        TextView tvUserName;
        @Bind(R.id.tv_post_question_desc)
        TextView tvQuestionDesc;
        @Bind(R.id.tv_post_response_comments)
        TextView tvComments;
        @Bind(R.id.tv_post_response_likes)
        TextView tvLikes;
        @Bind(R.id.tv_post_response_views)
        TextView tvViews;
        @Bind(R.id.ll_post_tags)
        LinearLayout llTags;
        @Bind(R.id.tv_course_name)
        TextView tvCourseName;
        @Bind(R.id.tv_subject_name)
        TextView tvSubjectName;
        @Bind(R.id.tv_chapter_name)
        TextView tvChapterName;
        @Bind(R.id.tv_topic_name)
        TextView tvTopicName;
        @Bind(R.id.ll_post_views)
        LinearLayout llViews;
        @Bind(R.id.ll_post_actions)
        LinearLayout llActions;

        public PostHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
