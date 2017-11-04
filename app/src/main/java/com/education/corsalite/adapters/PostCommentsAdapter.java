package com.education.corsalite.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.corsalite.R;
import com.education.corsalite.listener.CommentEventsListener;
import com.education.corsalite.listener.SocialEventsListener;
import com.education.corsalite.models.responsemodels.ForumPost;
import com.education.corsalite.models.responsemodels.FourmCommentPostModel;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.AppPref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Viswanath on 1/8/16.
 */
public class PostCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ForumPost post;
    private List<ForumPost> mForumPostList;
    private CommentEventsListener mCommentsEventsListener;
    private SocialEventsListener mSocialEventsListener;
    private Context mActivity;

    public PostCommentsAdapter(Context activity, SocialEventsListener socialEventsListener, CommentEventsListener listener) {
        this.mActivity = activity;
        mForumPostList = new ArrayList<>();
        mCommentsEventsListener = listener;
        this.mSocialEventsListener = socialEventsListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_item_post, parent, false);
            return new HeaderHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_item_comment, parent, false);
            return new CommentHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position == 0) {
            if (post == null) {
                return;
            }
            ForumPost forumPost = post;
            HeaderHolder holder = (HeaderHolder) viewHolder;
            holder.tvActionLock.setVisibility(View.GONE);
            holder.tvActionComment.setVisibility(View.INVISIBLE);

            if (forumPost.idUser.equals(AppPref.get(mActivity).getUserId())) {
                holder.tvActionDelete.setVisibility(View.VISIBLE);
                holder.tvActionEdit.setVisibility(View.VISIBLE);
            }
            setupPostActionListener(holder, position);

            holder.tvQuestion.setText(forumPost.PostSubject);
            holder.tvDate.setText(forumPost.Datetime + " by");
            holder.tvUserName.setText(forumPost.DisplayName);
            holder.postDescriptionWebview.getSettings().setJavaScriptEnabled(true);
            holder.postDescriptionWebview.loadDataWithBaseURL("", forumPost.htmlText, "text/html", "UTF-8", "");
            if (TextUtils.isEmpty(forumPost.CourseName)) {
                holder.tvCourseName.setVisibility(View.GONE);
            } else {
                holder.tvCourseName.setVisibility(View.VISIBLE);
                holder.tvCourseName.setText(forumPost.CourseName);
            }
            if (TextUtils.isEmpty(forumPost.SubjectName)) {
                holder.tvSubjectName.setVisibility(View.GONE);
            } else {
                holder.tvSubjectName.setVisibility(View.VISIBLE);
                holder.tvSubjectName.setText(forumPost.SubjectName);
            }
            if (TextUtils.isEmpty(forumPost.ChapterName)) {
                holder.tvChapterName.setVisibility(View.GONE);
            } else {
                holder.tvChapterName.setVisibility(View.VISIBLE);
                holder.tvChapterName.setText(forumPost.ChapterName);
            }
            if (TextUtils.isEmpty(forumPost.TopicName)) {
                holder.tvTopicName.setVisibility(View.GONE);
            } else {
                holder.tvTopicName.setVisibility(View.VISIBLE);
                holder.tvTopicName.setText(forumPost.TopicName);
            }

            holder.tvComments.setText(forumPost.postReplies + " Comments");
            holder.tvViews.setText(forumPost.postViews + " Views");


            if (!TextUtils.isEmpty(forumPost.IsLiked) && forumPost.IsLiked.equalsIgnoreCase("Y")) {
                holder.tvLikes.setClickable(false);
                Drawable img = mActivity.getResources().getDrawable(R.drawable.like_green);
                holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                holder.tvLikes.setText(forumPost.postLikes + " Likes");
            } else {
                holder.tvLikes.setClickable(true);
                Drawable img = mActivity.getResources().getDrawable(R.drawable.like);
                holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                holder.tvLikes.setText("Like");
            }

            Drawable img = mActivity.getResources().getDrawable(!TextUtils.isEmpty(forumPost.bookmark) ? R.drawable.bookmark_green : R.drawable.bookmark);
            holder.tvActionBookmark.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

            Picasso.with(mActivity)
                    .load(ApiClientService.getBaseUrl() + forumPost.PhotoUrl)
                    .placeholder(R.drawable.profile_pic)
                    .error(R.drawable.profile_pic).fit()
                    .into(holder.ivUserPic);
        } else {
            final ForumPost forumPost = mForumPostList.get(position - 1);
            CommentHolder holder = (CommentHolder) viewHolder;
            setupCommentsActionListener(holder, position);
            // TODO : uncomment it after implementing the edit/delete functionality
            //    if (forumPost.idUser.equals(LoginUserCache.get().getLoginResponse().idUser)) {
            //        holder.tvActionDelete.setVisibility(View.VISIBLE);
            //        holder.tvActionEdit.setVisibility(View.VISIBLE);
            //    }
            holder.tvDate.setText(forumPost.Datetime);
            holder.tvUserName.setText(forumPost.DisplayName);
            holder.postDescriptionWebview.getSettings().setJavaScriptEnabled(true);
            holder.postDescriptionWebview.loadDataWithBaseURL("", forumPost.htmlText, "text/html", "UTF-8", "");
            Picasso.with(mActivity)
                    .load(ApiClientService.getBaseUrl() + forumPost.PhotoUrl)
                    .placeholder(R.drawable.profile_pic)
                    .error(R.drawable.profile_pic).fit()
                    .into(holder.ivUserPic);
        }
    }

    private void setupCommentsActionListener(CommentHolder holder, final int position) {
        holder.tvActionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentsEventsListener.onEditClicked(position);
            }
        });
        holder.tvActionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentsEventsListener.onDeleteClicked(position);
            }
        });
    }

    private void setupPostActionListener(HeaderHolder holder, final int position) {
        holder.tvActionBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocialEventsListener.onBookmarkClicked(position);
            }
        });
        holder.tvLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocialEventsListener.onLikeClicked(position);
            }
        });
        holder.tvActionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocialEventsListener.onDeleteClicked(position);
            }
        });
        holder.tvActionLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocialEventsListener.onLockClicked(position);
            }
        });
        holder.tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocialEventsListener.onCommentClicked(position);
            }
        });
        holder.tvComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocialEventsListener.onCommentClicked(position);
            }
        });
        holder.tvActionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocialEventsListener.onEditClicked(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mForumPostList.size() + 1;
    }

    public void setForumPostList(FourmCommentPostModel model) {
        if (mForumPostList != null) {
            mForumPostList.clear();
        } else {
            mForumPostList = new ArrayList<>();
        }
        post = model.post;
        if (model.commentPosts != null) {
            mForumPostList.addAll(model.commentPosts);
        }
        notifyDataSetChanged();
    }

    public void updatePost(ForumPost post) {
        this.post = post;
        notifyDataSetChanged();
    }

    public void updateCurrentItem(int position) {
        notifyItemChanged(position);
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_comment_user_pic)
        ImageView ivUserPic;
        @Bind(R.id.tv_comment_action_edit)
        TextView tvActionEdit;
        @Bind(R.id.tv_comment_action_delete)
        TextView tvActionDelete;
        @Bind(R.id.tv_comment_date)
        TextView tvDate;
        @Bind(R.id.tv_comment_username)
        TextView tvUserName;
        @Bind(R.id.comment_webview)
        WebView postDescriptionWebview;

        public CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_post_user_pic)
        ImageView ivUserPic;
        @Bind(R.id.tv_post_action_bookmark)
        TextView tvActionBookmark;
        @Bind(R.id.tv_post_action_Comment)
        TextView tvActionComment;
        @Bind(R.id.tv_post_action_edit)
        TextView tvActionEdit;
        @Bind(R.id.tv_post_action_lock)
        TextView tvActionLock;
        @Bind(R.id.tv_post_action_delete)
        TextView tvActionDelete;
        @Bind(R.id.tv_post_question)
        TextView tvQuestion;
        @Bind(R.id.tv_post_date)
        TextView tvDate;
        @Bind(R.id.tv_post_user_name)
        TextView tvUserName;
        @Bind(R.id.post_desc_webview)
        WebView postDescriptionWebview;
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

        public HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
