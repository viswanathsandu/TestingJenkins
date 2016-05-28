package com.corsalite.tabletapp.listener;

/**
 * Created by sridharnalam on 1/14/16.
 */
public interface SocialEventsListener {

    void onLikeClicked(int position);

    void onCommentClicked(int position);

    void onBookmarkClicked(int position);

    void onEditClicked(int position);

    void onLockClicked(int position);

    void onDeleteClicked(int position);
}
