package com.corsalite.tabletapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.corsalite.tabletapp.R;
import com.corsalite.tabletapp.listener.CommentEventsListener;
import com.corsalite.tabletapp.models.responsemodels.ForumPost;
import com.corsalite.tabletapp.services.ApiClientService;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Viswanath on 1/8/16.
 */
public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsAdapter.CommentHolder> {

    private List<ForumPost> mForumPostList;
    private CommentEventsListener mCommentsEventsListener;

    public PostCommentsAdapter(List<ForumPost> comments, CommentEventsListener listener) {
        mForumPostList = comments;
        mCommentsEventsListener = listener;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item_comment, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        final ForumPost forumPost = mForumPostList.get(position);
        // TODO : uncomment it after implementing the edit/delete functionality
        //    if (forumPost.idUser.equals(LoginUserCache.getInstance().getLongResponse().userId)) {
        //        holder.tvActionDelete.setVisibility(View.VISIBLE);
        //        holder.tvActionEdit.setVisibility(View.VISIBLE);
        //    }
        setupActionListener(holder, position);

        holder.tvDate.setText(forumPost.Datetime);
        holder.tvUserName.setText(forumPost.DisplayName);
        holder.postDescriptionWebview.getSettings().setJavaScriptEnabled(true);
        holder.postDescriptionWebview.loadDataWithBaseURL("", forumPost.htmlText, "text/html", "UTF-8", "");

        Glide.with(holder.ivUserPic.getContext())
                .load(ApiClientService.getBaseUrl() + forumPost.PhotoUrl)
                .centerCrop()
                .placeholder(R.drawable.profile_pic)
                .crossFade()
                .into(holder.ivUserPic);

    }

    private void setupActionListener(CommentHolder holder, final int position) {
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

    @Override
    public int getItemCount() {
        return mForumPostList.size();
    }

    public ForumPost getItem(int position) {
        return mForumPostList.get(position);
    }

    public void setForumPostList(List<ForumPost> forumPosts) {
        if (mForumPostList != null) {
            mForumPostList.clear();
            if (forumPosts != null) {
                mForumPostList.addAll(forumPosts);
            }
            notifyDataSetChanged();
        }
    }

    public void deleteForumPost(int position) {
        mForumPostList.remove(position);
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
}
