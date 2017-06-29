package com.education.corsalite.listener;

/**
 * Created by sridharnalam on 1/14/16.
 */
public interface SocialEventsListener {

    void onTitleClicked(int position);

    void onLikeClicked(int position);

    void onCommentClicked(int position);

    void onBookmarkClicked(int position);

    void onEditClicked(int position);

    void onLockClicked(int position);

    void onDeleteClicked(int position);
}
